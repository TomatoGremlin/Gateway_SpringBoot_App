package com.example.gateway.ratesCollector.service;

import com.example.gateway.ratesCollector.controller.dto.CurrentResponse;
import com.example.gateway.exceptions.HistoryRatesNotFoundException;
import com.example.gateway.exceptions.RatesNotFoundException;
import com.example.gateway.ratesCollector.model.AuditLog;
import com.example.gateway.ratesCollector.model.ExchangeRateEntry;
import com.example.gateway.cache.ExchangeRatesRedisAggregate;
import com.example.gateway.cache.ExchangeRateRedisRepo;
import com.example.gateway.ratesCollector.repository.AuditLogRepo;
import com.example.gateway.ratesCollector.repository.RatesRepo;
import com.example.gateway.ratesCollector.controller.dto.RatesDTO;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
public class RatesCollectorService {

    @Autowired
    FixerApiService fixerApiService;
    @Autowired
    AuditLogService auditLogService;
    @Autowired
    RatesUtilService ratesUtilService;
    @Autowired
    AmqpTemplate amqpTemplate;


    @Autowired
    RatesRepo ratesRepo;
    @Autowired
    AuditLogRepo auditLogRepo;
    @Autowired
    ExchangeRateRedisRepo exchangeRateRedisRepo;


    public RatesDTO getResponse() {
        RatesDTO fetchedData = fixerApiService.fetchData();
        saveRatesDataToRedis(fetchedData);
        storeToDataBase(fetchedData);
        amqpTemplate.convertAndSend("direct-exchange", "rates", fetchedData);
        return fetchedData;
    }


    public List<ExchangeRateEntry> storeToDataBase(RatesDTO ratesDto) {
        List<ExchangeRateEntry> responseDataList = new ArrayList<>();
        String base = ratesDto.getBase();
        int rowsByBase = ratesRepo.findLatestRowsByCurrency(base);
        if (rowsByBase == 0) {
            for (Map.Entry<String, BigDecimal> entry : ratesDto.getRates().entrySet()) {
                String currency = entry.getKey();
                BigDecimal rate = entry.getValue();
                ExchangeRateEntry savedEntry = save(ratesDto, currency, rate);

                responseDataList.add(savedEntry);
            }
        } else {
            for (Map.Entry<String, BigDecimal> entry : ratesDto.getRates().entrySet()) {
                String currency = entry.getKey();
                BigDecimal rate = entry.getValue();
                ExchangeRateEntry entryToUpdate = ratesRepo.findLatestRatesByBaseQuotePair(base, currency).get();
                entryToUpdate = update(entryToUpdate, ratesDto, currency, rate);

                responseDataList.add(entryToUpdate);
            }
        }
        return responseDataList;
    }

    public ExchangeRateEntry save(RatesDTO ratesDto, String currency, BigDecimal rate) {
        ExchangeRateEntry exchangeRateEntryToSave = new ExchangeRateEntry();
        exchangeRateEntryToSave.setRequestId(UUID.randomUUID());
        exchangeRateEntryToSave.setTimestamp(ratesDto.getTimestamp());
        exchangeRateEntryToSave.setBase(ratesDto.getBase());
        exchangeRateEntryToSave.setCurrency(currency);
        exchangeRateEntryToSave.setRate(rate);

        exchangeRateEntryToSave = ratesRepo.save(exchangeRateEntryToSave);
        auditLogService.createAuditLog(ratesDto, "SAVE", currency, rate);
        return exchangeRateEntryToSave;
    }

    public ExchangeRateEntry update(ExchangeRateEntry entryToUpdate, RatesDTO ratesDto, String currency, BigDecimal rate) {
        entryToUpdate.setTimestamp(ratesDto.getTimestamp());
        entryToUpdate.setRate(rate);
        entryToUpdate = ratesRepo.save(entryToUpdate);
        auditLogService.createAuditLog(ratesDto, "UPDATE", currency, rate);
        return entryToUpdate;
    }


    public void saveRatesDataToRedis(RatesDTO ratesDTO) {
        ExchangeRatesRedisAggregate exchangeRatesRedisAggregate = new ExchangeRatesRedisAggregate();
        exchangeRatesRedisAggregate.setBaseCurrency(ratesDTO.getBase());
        exchangeRatesRedisAggregate.setForeignRates(ratesDTO.getRates());
        exchangeRatesRedisAggregate.setTimestamp(ratesDTO.getTimestamp());
        exchangeRateRedisRepo.save(exchangeRatesRedisAggregate);
    }

    public CurrentResponse getLatestRatesData(String base) throws RatesNotFoundException {
        CurrentResponse currentResponse = getRatesFromCacheOrDatabase(base);
        return currentResponse;
    }

    public List<AuditLog> getLatestRatesForPeriod(String baseCurrency, long period) throws HistoryRatesNotFoundException {
        LocalDateTime startTime = ratesUtilService.calculateTimeStamp(period);
        Optional<List<AuditLog>> rates = auditLogRepo.findLatestRatesByTimeStamp(baseCurrency, startTime);
        if (!rates.isPresent()) {
            throw new HistoryRatesNotFoundException("Rates in the last " + period + " hours for base currency " + baseCurrency + " were not found", baseCurrency, period);
        }
        List<AuditLog> existigHistoryRates = rates.get();
        return existigHistoryRates;
    }


    public CurrentResponse getSpecificRateData(String base, String currency) throws RatesNotFoundException {
        CurrentResponse currentResponse = getRatesFromCacheOrDatabase(base);
        if (!currentResponse.getRates().containsKey(currency)) {
            throw new RatesNotFoundException("Rate for currency " + currency + " was not found for base " + base, base);
        }
        CurrentResponse specificRateResponse = new CurrentResponse();
        specificRateResponse.setBase(base);
        BigDecimal correspondingRateValue = currentResponse.getRates().get(currency);
        specificRateResponse.setRates(Collections.singletonMap(currency, correspondingRateValue));
        specificRateResponse.setDateLastUpdated(currentResponse.getDateLastUpdated());
        return specificRateResponse;
    }

    private CurrentResponse getRatesFromCacheOrDatabase(String base) throws RatesNotFoundException {
        CurrentResponse currentResponse = new CurrentResponse();
        currentResponse.setBase(base);

        Optional<ExchangeRatesRedisAggregate> cacheResult = exchangeRateRedisRepo.findById(base);
        if (cacheResult.isPresent()) {
            updateResponseFromCache(currentResponse, cacheResult.get());
        } else {
            updateResponseFromDatabase(currentResponse, base);
        }
        return currentResponse;
    }

    private void updateResponseFromCache(CurrentResponse currentResponse, ExchangeRatesRedisAggregate cacheData) {
        long timestamp = cacheData.getTimestamp();
        LocalDateTime dateTime = ratesUtilService.convertUnixTimestampToUTC(timestamp);

        currentResponse.setRates(cacheData.getForeignRates());
        currentResponse.setDateLastUpdated(dateTime);
    }

    private void updateResponseFromDatabase(CurrentResponse currentResponse, String base) throws RatesNotFoundException {
        Optional<List<ExchangeRateEntry>> databaseResult = ratesRepo.findLatestRatesByBaseCurrency(base);
        if (!databaseResult.isPresent()) {
            throw new RatesNotFoundException("Rates for base " + base + " were not found in the database", base);
        }

        List<ExchangeRateEntry> databaseEntries = databaseResult.get();
        long timestamp = databaseEntries.get(0).getTimestamp();
        LocalDateTime dateTime = ratesUtilService.convertUnixTimestampToUTC(timestamp);

        Map<String, BigDecimal> ratesMap = ratesUtilService.makeEntriesIntoMap(databaseEntries);
        currentResponse.setRates(ratesMap);
        currentResponse.setDateLastUpdated(dateTime);

        RatesDTO ratesCache = new RatesDTO(true, timestamp, base, dateTime.toLocalDate(), ratesMap);
        saveRatesDataToRedis(ratesCache);
    }
}




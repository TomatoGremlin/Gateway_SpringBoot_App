package com.example.gateway.ratesCollector.service;

import com.example.gateway.ratesCollector.controller.dto.RatesResponse;
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
    public RatesResponse getLatestRatesData(String base) throws RatesNotFoundException {
        RatesResponse ratesResponse = getRatesFromCacheOrDatabase(base);
        return ratesResponse;
    }

    public List<AuditLog> getLatestRatesForPeriod(String baseCurrency, long period) throws HistoryRatesNotFoundException {
        LocalDateTime startTime = ratesUtilService.calculateTimeStamp(period);
        Optional<List<AuditLog>> rates = auditLogRepo.findLatestRatesByTimeStamp(baseCurrency, startTime);
        if (!rates.isPresent()){
            throw new HistoryRatesNotFoundException("Rates in the last " + period + " hours for base currency "+ baseCurrency + " were not found", baseCurrency, period );
        }
        List<AuditLog> existigHistoryRates = rates.get();

        return existigHistoryRates;
    }


    public RatesResponse getSpecificRateData(String base, String currency) throws RatesNotFoundException {
        RatesResponse ratesResponse = getRatesFromCacheOrDatabase(base);

        if (!ratesResponse.getRates().containsKey(currency)) {
            throw new RatesNotFoundException("Rate for currency " + currency + " was not found for base " + base, base);
        }

        RatesResponse specificRateResponse = new RatesResponse();
        specificRateResponse.setBase(base);
        specificRateResponse.setRates(Collections.singletonMap(currency, ratesResponse.getRates().get(currency)));
        specificRateResponse.setDateLastUpdated(ratesResponse.getDateLastUpdated());

        return specificRateResponse;
    }

    private RatesResponse getRatesFromCacheOrDatabase(String base) throws RatesNotFoundException {
        RatesResponse ratesResponse = new RatesResponse();
        ratesResponse.setBase(base);

        Optional<ExchangeRatesRedisAggregate> exchangeRatesRedisAggregate = exchangeRateRedisRepo.findById(base);
        LocalDateTime dateTime = null;
        long timestamp = 0;

        if (exchangeRatesRedisAggregate.isPresent()) {
            ExchangeRatesRedisAggregate existingCache = exchangeRatesRedisAggregate.get();
            timestamp = existingCache.getTimestamp();
            dateTime = ratesUtilService.convertUnixTimestampToUTC(timestamp);
            ratesResponse.setRates(existingCache.getForeignRates());
            ratesResponse.setDateLastUpdated(dateTime);

        } else {
            Optional<List<ExchangeRateEntry>> dataBaseEntries = ratesRepo.findLatestRatesByBaseCurrency(base);
            if (!dataBaseEntries.isPresent()){
                throw new RatesNotFoundException("Rates for base " +  base + " were not found in database", base);
            }
            List<ExchangeRateEntry> existingDataBaseEntries = dataBaseEntries.get();
            timestamp = existingDataBaseEntries.get(0).getTimestamp();
            dateTime = ratesUtilService.convertUnixTimestampToUTC(timestamp);

            Map<String, BigDecimal> map = ratesUtilService.makeEntriesIntoMap(existingDataBaseEntries);
            ratesResponse.setRates(map);
            ratesResponse.setDateLastUpdated(dateTime);
            
            RatesDTO ratesCache = new RatesDTO(true, timestamp, base, dateTime.toLocalDate(), map);
            saveRatesDataToRedis(ratesCache);
        }
        return ratesResponse;
    }


}




package com.example.gateway.RatesCollector.service;

import com.example.gateway.Api.service.ClientRequestService;
import com.example.gateway.RatesCollector.model.DTO.CommonResponse;
import com.example.gateway.RatesCollector.model.DTO.SpecificRate;
import com.example.gateway.RatesCollector.model.DataBase.AuditLog;
import com.example.gateway.RatesCollector.model.DataBase.ExchangeRateEntry;
import com.example.gateway.RatesCollector.model.redis.ExchangeRatesRedisAggregate;
import com.example.gateway.RatesCollector.model.redis.ExchangeRateRedisRepo;
import com.example.gateway.RatesCollector.repository.AuditLogRepo;
import com.example.gateway.RatesCollector.repository.RatesRepo;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Service
public class RatesService {
    @Autowired
    private RatesRepo ratesRepo;

    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private AuditLogRepo auditLogRepo;


    @Autowired
    FixerApiService fixerApiService;

    @Autowired
    ExchangeRateRedisRepo exchangeRateRedisRepo;
    @Autowired
    ClientRequestService clientRequestService;

    public List<ExchangeRateEntry> getResponse(RatesDTO ratesDto) {
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
                ExchangeRateEntry entryToUpdate = ratesRepo.findLatestRatesByBaseQuotePair(base, currency);
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


    public List<AuditLog> getLatestRatesForPeriod(String currency, long period) {
        LocalDateTime startTime = calculateTimeStamp(period);
        List<AuditLog> rates = auditLogRepo.findLatestRatesByTimeStamp(currency, startTime);
        return rates;
    }

    public LocalDateTime calculateTimeStamp(long periodInHours) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startTime = currentTime.minusHours(periodInHours);
        return startTime;
    }

    public void saveRatesDataToRedis(RatesDTO ratesDTO) {
        ExchangeRatesRedisAggregate exchangeRatesRedisAggregate = new ExchangeRatesRedisAggregate();
        exchangeRatesRedisAggregate.setBaseCurrency(ratesDTO.getBase());
        exchangeRatesRedisAggregate.setForeignRates(ratesDTO.getRates());
        exchangeRatesRedisAggregate.setTimestamp(ratesDTO.getTimestamp());
        exchangeRateRedisRepo.save(exchangeRatesRedisAggregate);
    }

    public RatesDTO getLatestRatesData(String base) {
        RatesDTO ratesResponse = new RatesDTO();
        ratesResponse.setBase(base);
        ratesResponse.setSuccess(true);

        CommonResponse commonResponse = getRatesFromCacheOrDatabase(base);

        ratesResponse.setRates(commonResponse.getRates());
        ratesResponse.setTimestamp(commonResponse.getTimestamp());
        ratesResponse.setDate(commonResponse.getLocalDateTime().toLocalDate());



        return ratesResponse;
    }

    public SpecificRate getSpecificRateData(String base, String currency) {
        SpecificRate specificRate = new SpecificRate();
        specificRate.setBase(base);
        specificRate.setCurrency(currency);

        CommonResponse commonResponse = getRatesFromCacheOrDatabase(base);

        specificRate.setRate(commonResponse.getRates().get(currency));
        specificRate.setDateTime(commonResponse.getLocalDateTime());

        return specificRate;
    }

    private CommonResponse getRatesFromCacheOrDatabase(String base) {
        CommonResponse commonResponse = new CommonResponse();

        Optional<ExchangeRatesRedisAggregate> exchangeRatesRedisAggregate = exchangeRateRedisRepo.findById(base);
        LocalDateTime dateTime = null ;
        long timestamp = 0;

        if (exchangeRatesRedisAggregate.isPresent()) {
            ExchangeRatesRedisAggregate existingCache = exchangeRatesRedisAggregate.get();
            timestamp = existingCache.getTimestamp();
            dateTime = convertUnixTimestampToUTC(timestamp);

            commonResponse.setRates(existingCache.getForeignRates());
            commonResponse.setTimestamp(timestamp);
            commonResponse.setLocalDateTime(dateTime);

        } else {
            List<ExchangeRateEntry> existingDataBaseEntries = ratesRepo.findLatestRatesByCurrency(base);
            timestamp = existingDataBaseEntries.get(0).getTimestamp();
            dateTime = convertUnixTimestampToUTC(timestamp);

            Map<String, BigDecimal> map = new HashMap<>();
            for (ExchangeRateEntry rate : existingDataBaseEntries) {
                map.put(rate.getCurrency(), rate.getRate());
            }

            commonResponse.setRates(map);
            commonResponse.setTimestamp(timestamp);
            commonResponse.setLocalDateTime(dateTime);
            RatesDTO ratesDTO = new RatesDTO(true, timestamp, base, dateTime.toLocalDate() ,map);

            saveRatesDataToRedis(ratesDTO);
        }
        return commonResponse;
    }

    public LocalDateTime convertUnixTimestampToUTC(long unixTimestamp) {
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }


}




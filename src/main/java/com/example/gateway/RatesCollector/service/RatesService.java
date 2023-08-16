package com.example.gateway.RatesCollector.service;

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

        Optional<ExchangeRatesRedisAggregate> exchangeRatesRedisAggregate = exchangeRateRedisRepo.findById(base);

        //get from redis
        if (exchangeRatesRedisAggregate.isPresent()) {
            ExchangeRatesRedisAggregate existingCache = exchangeRatesRedisAggregate.get();
            ratesResponse.setRates(existingCache.getForeignRates());
            ratesResponse.setTimestamp(existingCache.getTimestamp());
            LocalDateTime localDateTime = convertUnixTimestampToUTC(existingCache.getTimestamp());
            ratesResponse.setDate(String.valueOf(localDateTime));

        } // get from database
        else {
            List<ExchangeRateEntry> existingDataBaseEntries = ratesRepo.findLatestRatesByCurrency(base);
            long timestamp = existingDataBaseEntries.get(0).getTimestamp();
            Map<String, BigDecimal> map = new HashMap<>();
            for (ExchangeRateEntry rate : existingDataBaseEntries) {
                map.put(rate.getCurrency(), rate.getRate());
            }
            ratesResponse.setRates(map);
            ratesResponse.setTimestamp(timestamp);
            LocalDateTime localDateTime = convertUnixTimestampToUTC(timestamp);
            ratesResponse.setDate(String.valueOf(localDateTime));


            saveRatesDataToRedis(ratesResponse);
        }
        return ratesResponse;
    }


    public SpecificRate getSpecifictRateData(String base, String currency) {
        SpecificRate specificRate = new SpecificRate();
        specificRate.setBase(base);
        specificRate.setCurrency(currency);

        Optional<ExchangeRatesRedisAggregate> exchangeRatesRedisAggregate = exchangeRateRedisRepo.findById(base);

        //get from redis
        if (exchangeRatesRedisAggregate.isPresent()) {
            ExchangeRatesRedisAggregate existingCache = exchangeRatesRedisAggregate.get();
            specificRate.setRate(existingCache.getForeignRates().get(currency));
            LocalDateTime localDateTime = convertUnixTimestampToUTC(existingCache.getTimestamp());
            specificRate.setDateTime(localDateTime);

        } // get from database
        else {
            List<ExchangeRateEntry> existingDataBaseEntries = ratesRepo.findLatestRatesByCurrency(base);
            long timestamp = existingDataBaseEntries.get(0).getTimestamp();
            Map<String, BigDecimal> map = new HashMap<>();
            for (ExchangeRateEntry rate : existingDataBaseEntries) {
                map.put(rate.getCurrency(), rate.getRate());
            }
            specificRate.setRate(map.get(currency));
            LocalDateTime localDateTime = convertUnixTimestampToUTC(timestamp);
            specificRate.setDateTime(localDateTime);
        }
        return specificRate;
    }


    public LocalDateTime convertUnixTimestampToUTC(long unixTimestamp) {
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }


}




//    public RatesDTO makeResponse(String base, Object object){
//        RatesDTO ratesResponse = new RatesDTO();
//
//        ratesResponse.setBase(base);
//        ratesResponse.setSuccess(true);
//        ratesResponse.setRates(existingCache.getForeignRates());
//        ratesResponse.setTimestamp(existingCache.getTimestamp());
//        LocalDateTime localDateTime = convertUnixTimestampToUTC(existingCache.getTimestamp());
//        ratesResponse.setDate(String.valueOf(localDateTime));
//    }

package com.example.gateway.ratesCollector.service;

import com.example.gateway.ratesCollector.model.ExchangeRateEntry;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RatesUtilService {
    public LocalDateTime calculateTimeStamp(long periodInHours) {
        LocalDateTime currentTime = LocalDateTime.now();
        LocalDateTime startTime = currentTime.minusHours(periodInHours);
        return startTime;
    }

    public LocalDateTime convertUnixTimestampToUTC(long unixTimestamp) {
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
    }

    public Map<String, BigDecimal> makeEntriesIntoMap (List<ExchangeRateEntry> dataBaseEntries){
        Map<String, BigDecimal> map = new HashMap<>();
        for (ExchangeRateEntry rate : dataBaseEntries) {
            map.put(rate.getCurrency(), rate.getRate());
        }
        return map;
    }



}

package com.example.gateway.RatesCollector.service;

import com.example.gateway.RatesCollector.model.DataBase.AuditLog;
import com.example.gateway.RatesCollector.model.DataBase.ExchangeRate;
import com.example.gateway.RatesCollector.model.DataBase.RatesResponseData;
import com.example.gateway.RatesCollector.model.redis.CurrencyExchangeRates;
import com.example.gateway.RatesCollector.model.redis.ExchangeReateRedisRepository;
import com.example.gateway.RatesCollector.repository.AuditLogRepo;
import com.example.gateway.RatesCollector.repository.ExchangeRatesRepo;
import com.example.gateway.RatesCollector.repository.RatesRepo;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class RatesService {
    @Autowired
    private RatesRepo ratesRepo;
    @Autowired
    private ExchangeRatesRepo exchangeRatesRepo;
    @Autowired
    private AuditLogService auditLogService;
    @Autowired
    private AuditLogRepo auditLogRepo;


    @Autowired
    FixerApiService fixerApiService;

    @Autowired
    ExchangeReateRedisRepository exchangeReateRedisRepository;

    public RatesResponseData putInDataBase(RatesDTO ratesDto) {
        Optional<RatesResponseData> existingDataOptional = ratesRepo.findByBaseCurrency(ratesDto.getBase());
        if (existingDataOptional.isPresent()) {
            return update(existingDataOptional, ratesDto);
        }
        return save(ratesDto);
    }

    public RatesResponseData save(RatesDTO ratesDto) {
        RatesResponseData ratesResponseDataToSave = new RatesResponseData();

        UUID id = UUID.randomUUID();

        ratesResponseDataToSave.setRequestId(id);
        ratesResponseDataToSave.setTimestamp(ratesDto.getTimestamp());
        ratesResponseDataToSave.setBase(ratesDto.getBase());
        ratesResponseDataToSave.setDate(ratesDto.getDate());

        ratesResponseDataToSave = ratesRepo.save(ratesResponseDataToSave);

        List<ExchangeRate> quoteRatesList = new ArrayList<>();

        for (Map.Entry<String, BigDecimal> entry : ratesDto.getRates().entrySet()) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrency(entry.getKey());
            exchangeRate.setRatesId(UUID.randomUUID());
            exchangeRate.setRate(entry.getValue());
            exchangeRate.setRatesResponseData(ratesResponseDataToSave);

            exchangeRatesRepo.save(exchangeRate);
            auditLogService.createAuditLog(ratesDto, entry.getKey(), entry.getValue());
            quoteRatesList.add(exchangeRate);
        }

        ratesResponseDataToSave.setExchangeRateList(quoteRatesList);
        return ratesResponseDataToSave;
    }

    public RatesResponseData update(Optional<RatesResponseData> existingDataOptional, RatesDTO ratesDto) {

        RatesResponseData existingData = existingDataOptional.get();
        existingData.setTimestamp(ratesDto.getTimestamp());
        existingData.setDate(ratesDto.getDate());
        existingData = ratesRepo.save(existingData);

        Map<String, BigDecimal> updatedRates = ratesDto.getRates();
        List<ExchangeRate> exchangeRates = existingData.getExchangeRateList();

        if (updatedRates != null) {
            for (Map.Entry<String, BigDecimal> entry : updatedRates.entrySet()) {
                // Find the corresponding ExchangeRate entity (if it exists) and update it
                Optional<ExchangeRate> exchangeRateOptional = exchangeRates.stream()
                        .filter(rate -> rate.getCurrency().equals(entry.getKey()))
                        .findFirst();

                if (exchangeRateOptional.isPresent()) {
                    ExchangeRate exchangeRate = exchangeRateOptional.get();
                    exchangeRate.setRate(entry.getValue());
                    exchangeRatesRepo.save(exchangeRate);
                    auditLogService.createAuditLog(ratesDto, entry.getKey(), entry.getValue());

                } else {
                    ExchangeRate newExchangeRate = new ExchangeRate();
                    newExchangeRate.setRatesId(UUID.randomUUID());
                    newExchangeRate.setCurrency(entry.getKey());
                    newExchangeRate.setRate(entry.getValue());
                    newExchangeRate.setRatesResponseData(existingData);
                    exchangeRates.add(newExchangeRate);
                    exchangeRatesRepo.save(newExchangeRate);
                    auditLogService.createAuditLog(ratesDto, entry.getKey(), entry.getValue());

                }
            }
        }
        existingData.setExchangeRateList(exchangeRates);
        return existingData;
    }



    public RatesResponseData getLatestRatesForCurrency(String currency) {
        RatesResponseData latestRates = ratesRepo.findLatestRatesByCurrency(currency);
        return latestRates;
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
        CurrencyExchangeRates currencyExchangeRates = new CurrencyExchangeRates();
        currencyExchangeRates.setBaseCurrency(ratesDTO.getBase());
        currencyExchangeRates.setForeignRates(ratesDTO.getRates());

        exchangeReateRedisRepository.save(currencyExchangeRates);
    }

    @Cacheable(value = "exchangeRatesCache", key = "#ratesDTO.base")
    public CurrencyExchangeRates getCachedRatesData(RatesDTO ratesDTO) {
        return exchangeReateRedisRepository.findByBaseCurrency(ratesDTO.getBase());
    }


}
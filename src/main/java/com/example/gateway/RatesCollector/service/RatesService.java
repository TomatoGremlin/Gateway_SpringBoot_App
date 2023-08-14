package com.example.gateway.RatesCollector.service;

import com.example.gateway.RatesCollector.model.ExchangeRate;
import com.example.gateway.RatesCollector.model.RatesResponseData;
import com.example.gateway.RatesCollector.repository.ExchangeRatesRepo;
import com.example.gateway.RatesCollector.repository.RatesRepo;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RatesService {
    @Autowired
    private RatesRepo ratesRepo;

    @Autowired
    private ExchangeRatesRepo exchangeRatesRepo;
    @Autowired
    private AuditLogService auditLogService;


    public RatesResponseData saveRateData(RatesDTO ratesDto) {
        RatesResponseData ratesResponseDataToSave = new RatesResponseData();

        UUID id = UUID.randomUUID();
        ratesResponseDataToSave.setRequestId(id);
        ratesResponseDataToSave.setTimestamp(ratesDto.getTimestamp());
        ratesResponseDataToSave.setBase(ratesDto.getBase());
        ratesResponseDataToSave.setDate(ratesDto.getDate());

        ratesResponseDataToSave = ratesRepo.save(ratesResponseDataToSave); // Save RatesResponseData entity first

        List<ExchangeRate> exchangeRates = new ArrayList<>();

        for (Map.Entry<String, Double> entry : ratesDto.getRates().entrySet()) {
            ExchangeRate exchangeRate = new ExchangeRate();
            exchangeRate.setCurrency(entry.getKey());
            exchangeRate.setRatesId(UUID.randomUUID());
            exchangeRate.setRate(entry.getValue());
            exchangeRate.setRatesResponseData(ratesResponseDataToSave); // Set the RatesResponseData entity for the association

            exchangeRatesRepo.save(exchangeRate); // Save ExchangeRate entity
            auditLogService.createAuditLog( ratesDto, entry.getKey(), entry.getValue() );
            exchangeRates.add(exchangeRate);
        }

        ratesResponseDataToSave.setExchangeRateList(exchangeRates); // Set the list of ExchangeRate


        return ratesResponseDataToSave;
    }

    public RatesResponseData update(RatesDTO ratesDto) {

        Optional<RatesResponseData> existingDataOptional = ratesRepo.findByBaseCurrency( ratesDto.getBase() );
        //int rowsInTable = ratesRepo.checkIfEmpty();

            if (existingDataOptional.isPresent()  ) {
                RatesResponseData existingData = existingDataOptional.get();

                existingData.setTimestamp(ratesDto.getTimestamp());
                existingData.setDate(ratesDto.getDate());
                existingData = ratesRepo.save(existingData); // Save RatesResponseData entity first

                Map<String, Double> updatedRates = ratesDto.getRates();
                List<ExchangeRate> exchangeRates = existingData.getExchangeRateList();

                if (updatedRates != null) {
                    for (Map.Entry<String, Double> entry : updatedRates.entrySet()) {
                        // Find the corresponding ExchangeRate entity (if it exists) and update it
                        Optional<ExchangeRate> exchangeRateOptional = exchangeRates.stream()
                                .filter(rate -> rate.getCurrency().equals(entry.getKey()))
                                .findFirst();

                        if (exchangeRateOptional.isPresent()) {
                            ExchangeRate exchangeRate = exchangeRateOptional.get();
                            exchangeRate.setRate(entry.getValue());
                            exchangeRatesRepo.save(exchangeRate);
                            auditLogService.createAuditLog( ratesDto, entry.getKey(), entry.getValue() );

                        } else {

                            ExchangeRate newExchangeRate = new ExchangeRate();
                            newExchangeRate.setRatesId(UUID.randomUUID());
                            newExchangeRate.setCurrency(entry.getKey());
                            newExchangeRate.setRate(entry.getValue());
                            newExchangeRate.setRatesResponseData(existingData);
                            exchangeRates.add(newExchangeRate);
                            exchangeRatesRepo.save(newExchangeRate);
                            auditLogService.createAuditLog( ratesDto, entry.getKey(), entry.getValue() );

                        }

                    }
                }

                existingData.setExchangeRateList(exchangeRates);

            return existingData;
        }
            return saveRateData(ratesDto);
    }


    public RatesResponseData getLatestRatesForCurrency(String currency) {
        RatesResponseData latestRates = ratesRepo.findLatestRatesByCurrency(currency);
        return latestRates;
    }

    public List<RatesResponseData> getLatestRatesForPeriod(String currency, int period) {
        long startTime = calculateTimeStamp(period);
        List<RatesResponseData> rates = ratesRepo.findLatestRatesByTimeStamp(currency, startTime);
        return rates;

    }

    public long calculateTimeStamp(int period) {
        // calculate the beginning timestamp for the period
        long currentTimeStamp = System.currentTimeMillis() / 1000; // in seconds
        long periodInSeconds = period * 120;
        long startTimeStamp = currentTimeStamp - periodInSeconds;
        return startTimeStamp;
    }


}
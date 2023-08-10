package com.example.gateway.RatesCollector.service;

import com.example.gateway.RatesCollector.model.ExchangeRate;
import com.example.gateway.RatesCollector.model.RatesResponseData;
import com.example.gateway.RatesCollector.repository.ExchangeRatesRepo;
import com.example.gateway.RatesCollector.repository.RatesRepo;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RatesService {
    @Autowired
    RatesRepo ratesRepo;

    @Autowired
    ExchangeRatesRepo exchangeRatesRepo;

    public RatesResponseData saveRates(RatesDTO ratesDto){
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
            exchangeRates.add(exchangeRate);
        }

        ratesResponseDataToSave.setExchangeRateList(exchangeRates); // Set the list of ExchangeRate

        return ratesResponseDataToSave;
    }

    public RatesResponseData getLatestRatesForCurrency(String currency){


    }



}
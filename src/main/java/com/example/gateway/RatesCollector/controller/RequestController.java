package com.example.gateway.RatesCollector.controller;

import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.model.DataBase.ExchangeRateEntry;
import com.example.gateway.RatesCollector.service.FixerApiService;
import com.example.gateway.RatesCollector.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/fixer")
public class RequestController {

    @Autowired
    private FixerApiService fixerApiService;
    @Autowired

    private RatesService ratesService;

    //@GetMapping("/getRates")
    public RatesDTO fetch() {
        return fixerApiService.fetchData();
    }

    //@Scheduled(fixedRate = 40000) // the parameter is in milliseconds , Fetch every 30 sec
    @PostMapping("/saveRates")
    public ResponseEntity<List<ExchangeRateEntry>> save() {
        RatesDTO fetchedData = fixerApiService.fetchData();
        ratesService.saveRatesDataToRedis(fetchedData);
        List<ExchangeRateEntry> savedRatesResponseData = ratesService.getResponse(fetchedData);
        return ResponseEntity.ok(savedRatesResponseData);
    }


}

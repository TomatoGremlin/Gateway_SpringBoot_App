package com.example.gateway.RatesCollector.controller;

import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.model.RatesResponseData;
import com.example.gateway.RatesCollector.service.FixerFetchService;
import com.example.gateway.RatesCollector.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fixer")
public class RequestController {

    @Autowired
    private FixerFetchService fixerFetchService;
    @Autowired
    private RatesService ratesService;

    @GetMapping("/fetchRates")
    public RatesDTO fetch() {
        return fixerFetchService.fetchData();
    }

    //@Scheduled(fixedRate = 40000) // the parameter is in milliseconds , Fetch every 30 sec
    @PostMapping("/fetchAndSaveRates")
    public ResponseEntity<RatesResponseData> fetchSave() {
        RatesDTO fetchedData = fixerFetchService.fetchData();
        RatesResponseData savedRatesResponseData = ratesService.update(fetchedData);
        return ResponseEntity.ok(savedRatesResponseData);
    }

}

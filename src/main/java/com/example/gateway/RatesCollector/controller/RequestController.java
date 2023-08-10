package com.example.gateway.RatesCollector.controller;

import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.model.RatesResponseData;
import com.example.gateway.RatesCollector.service.FixerService;
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
    private FixerService fixerService;
    @Autowired
    private RatesService ratesService;

    @GetMapping("/fetchRates")
    public RatesDTO fetch() {
        return fixerService.fetchData();
    }

    //@Scheduled(fixedRate = 40000) // the parameter is in milliseconds , Fetch every 30 sec
    @PostMapping("/fetchAndSaveRates")
    public ResponseEntity<RatesResponseData> fetchSave() {
        RatesDTO fetchedData = fixerService.fetchData();
        RatesResponseData savedRatesResponseData = ratesService.saveRates(fetchedData);
        return ResponseEntity.ok(savedRatesResponseData);
    }

}

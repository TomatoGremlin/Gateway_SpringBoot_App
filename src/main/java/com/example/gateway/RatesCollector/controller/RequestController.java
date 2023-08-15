package com.example.gateway.RatesCollector.controller;

import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.model.DataBase.RatesResponseData;
import com.example.gateway.RatesCollector.service.FixerApiService;
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
    private FixerApiService fixerApiService;
    @Autowired
    private RatesService ratesService;

    @GetMapping("/fetchRates")
    public RatesDTO fetch() {
        return fixerApiService.fetchData();
    }

    //@Scheduled(fixedRate = 40000) // the parameter is in milliseconds , Fetch every 30 sec
    @PostMapping("/fetchAndSaveRates")
    public ResponseEntity<RatesResponseData> fetchSave() {
        RatesDTO fetchedData = fixerApiService.fetchData();
        //savetoRedis
        ratesService.saveRatesDataToRedis(fetchedData);
        RatesResponseData savedRatesResponseData = ratesService.putInDataBase(fetchedData);
        return ResponseEntity.ok(savedRatesResponseData);
    }


}

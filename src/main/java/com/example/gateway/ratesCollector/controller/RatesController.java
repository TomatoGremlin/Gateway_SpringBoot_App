package com.example.gateway.ratesCollector.controller;

import com.example.gateway.ratesCollector.controller.dto.RatesDTO;
import com.example.gateway.ratesCollector.service.RatesCollectorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/fixer")
public class RatesController {

    @Autowired
    private RatesCollectorService ratesCollectorService;

//     @GetMapping("/getRates")
//    public ResponseEntity<RatesDTO> fetch() {
//        return ResponseEntity.ok(fixerApiService.fetchData());
//    }

    //@Scheduled(fixedRate = 30000) // the parameter is in milliseconds , Fetch every 30 sec
    @PostMapping("/saveRates")
    public ResponseEntity<RatesDTO> save() {
        RatesDTO savedRatesResponseData = ratesCollectorService.getResponse();
        return ResponseEntity.ok(savedRatesResponseData);
    }

}


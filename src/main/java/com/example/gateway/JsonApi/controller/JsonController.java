package com.example.gateway.JsonApi.controller;

import com.example.gateway.JsonApi.model.ClientRequest;
import com.example.gateway.JsonApi.model.ClientRequestDTO;
import com.example.gateway.JsonApi.service.ClientRequestService;
import com.example.gateway.RatesCollector.model.RatesResponseData;
import com.example.gateway.RatesCollector.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/json_api")
public class JsonController {
    @Autowired
    private ClientRequestService clientRequestService;
    @Autowired
    private RatesService ratesService;


    @GetMapping("/getLatestRates")
    public ResponseEntity<RatesResponseData> getCurrent(@RequestBody ClientRequestDTO clientRequestDTO){
        String baseCurrency = clientRequestDTO.getCurrency();
        RatesResponseData fetched = ratesService.getLatestRatesForCurrency(baseCurrency);
        return ResponseEntity.ok(fetched);
    }



    @PostMapping("/postClientRequest")
    public ResponseEntity<ClientRequest> postRequest(@RequestBody ClientRequestDTO clientRequestDTO){
        ClientRequest saved = clientRequestService.save(clientRequestDTO);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/current")
    public ResponseEntity<RatesResponseData> postCurrent(@RequestBody ClientRequestDTO clientRequestDTO){
        clientRequestService.save(clientRequestDTO);

        String baseCurrency = clientRequestDTO.getCurrency();
        RatesResponseData fetched = ratesService.getLatestRatesForCurrency(baseCurrency);
        return ResponseEntity.ok(fetched);
    }


    @PostMapping("/history")
    public List<RatesResponseData> postHistory(@RequestBody ClientRequestDTO clientRequestDTO){

        List<RatesResponseData> responseDataList = ratesService.getLatestRatesForPeriod(clientRequestDTO.getCurrency(), clientRequestDTO.getPeriod()) ;
        return responseDataList;
    }





}

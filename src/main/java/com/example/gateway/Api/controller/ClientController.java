package com.example.gateway.Api.controller;

import com.example.gateway.Api.model.ClientRequest;
import com.example.gateway.Api.model.ClientRequestDTO;
import com.example.gateway.Api.service.ClientRequestService;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.model.DTO.SpecificRate;
import com.example.gateway.RatesCollector.model.DataBase.AuditLog;
import com.example.gateway.RatesCollector.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/json_api")
public class ClientController {
    @Autowired
    private ClientRequestService clientRequestService;
    @Autowired
    private RatesService ratesService;


    @PostMapping("/current")
    public ResponseEntity<RatesDTO> postCurrent(@RequestBody ClientRequestDTO clientRequestDTO){
        clientRequestService.save(clientRequestDTO);
        RatesDTO fetched = ratesService.getLatestRatesData(clientRequestDTO.getCurrency());
        return ResponseEntity.ok(fetched);
    }

    @PostMapping("/current/{quoteCurrency}")
    public ResponseEntity<SpecificRate> postCurrentByQuoteRate(@RequestBody ClientRequestDTO clientRequestDTO, @PathVariable String quoteCurrency){
        clientRequestService.save(clientRequestDTO);
        SpecificRate fetched = ratesService.getSpecificRateData(clientRequestDTO.getCurrency(), quoteCurrency);
        return ResponseEntity.ok(fetched);
    }


    @PostMapping("/history")
    public ResponseEntity<List<AuditLog>> postHistory(@RequestBody ClientRequestDTO clientRequestDTO){
        List<AuditLog> responseDataList = ratesService.getLatestRatesForPeriod(clientRequestDTO.getCurrency(), clientRequestDTO.getPeriod()) ;
        return ResponseEntity.ok(responseDataList);
    }


    @PostMapping("/postClientRequest")
    public ResponseEntity<ClientRequest> postRequest(@RequestBody ClientRequestDTO clientRequestDTO){
        ClientRequest saved = clientRequestService.save(clientRequestDTO);
        return ResponseEntity.ok(saved);
    }



}

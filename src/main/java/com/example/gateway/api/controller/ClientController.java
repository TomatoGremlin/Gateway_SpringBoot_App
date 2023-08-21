package com.example.gateway.api.controller;

import com.example.gateway.api.controller.dto.ClientHistoryRequestDTO;
import com.example.gateway.api.controller.dto.ClientRequestDTO;
import com.example.gateway.api.model.ClientRequest;
import com.example.gateway.api.service.ClientRequestService;
import com.example.gateway.exceptions.ClientRequestExeption;
import com.example.gateway.ratesCollector.controller.dto.RatesResponse;
import com.example.gateway.exceptions.RatesNotFoundException;
import com.example.gateway.ratesCollector.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/json_api")
public class ClientController {
    @Autowired
    private ClientRequestService clientRequestService;

    @PostMapping("/current")
    public ResponseEntity<RatesResponse> postCurrent(@RequestBody ClientRequestDTO clientRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        RatesResponse fetched = clientRequestService.getCurrentResponse(clientRequestDTO);
        return ResponseEntity.ok(fetched);
    }

    @PostMapping("/history")
    public ResponseEntity<List<AuditLog>> postHistory(@RequestBody ClientHistoryRequestDTO clientHistoryRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        List<AuditLog> responseDataList = clientRequestService.getHistoryResponse(clientHistoryRequestDTO);
        return ResponseEntity.ok(responseDataList);
    }

   /* @PostMapping("/current/{quoteCurrency}")
     public ResponseEntity<RatesResponse> postCurrentByQuoteRate(@RequestBody ClientRequestDTO clientRequestDTO, @PathVariable String quoteCurrency) throws RatesNotFoundException, ClientRequestExeption {
         RatesResponse fetched = clientRequestService.getCurrentPairResponse(clientRequestDTO, quoteCurrency);
         return ResponseEntity.ok(fetched);
     }*/
    @PostMapping("/postClientRequest")
    public ResponseEntity<ClientRequest> postRequest(@RequestBody ClientRequestDTO clientRequestDTO){
        ClientRequest saved = clientRequestService.save(clientRequestDTO);
        return ResponseEntity.ok(saved);
    }



}

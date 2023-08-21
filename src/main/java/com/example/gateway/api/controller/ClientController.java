package com.example.gateway.api.controller;

import com.example.gateway.api.controller.dto.ClientHistoryRequestDTO;
import com.example.gateway.api.controller.dto.ClientRequestDTO;
import com.example.gateway.api.model.ClientRequest;
import com.example.gateway.api.service.ClientRequestService;
import com.example.gateway.exceptions.ClientRequestExeption;
import com.example.gateway.ratesCollector.controller.dto.CommonResponse;
import com.example.gateway.ratesCollector.controller.dto.RatesDTO;
import com.example.gateway.exceptions.RatesNotFoundException;
import com.example.gateway.ratesCollector.model.AuditLog;
import com.example.gateway.ratesCollector.service.RatesCollectorService;
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
    public ResponseEntity<CommonResponse> postCurrent(@RequestBody ClientRequestDTO clientRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        CommonResponse fetched = clientRequestService.getCurrentResponse(clientRequestDTO);
        return ResponseEntity.ok(fetched);
    }

    @PostMapping("/history")
    public ResponseEntity<List<AuditLog>> postHistory(@RequestBody ClientHistoryRequestDTO clientHistoryRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        List<AuditLog> responseDataList = clientRequestService.getHistoryResponse(clientHistoryRequestDTO);
        return ResponseEntity.ok(responseDataList);
    }

    /* @PostMapping("/current/{quoteCurrency}")
     public ResponseEntity<SpecificRateResponse> postCurrentByQuoteRate(@RequestBody ClientRequestDTO clientRequestDTO, @PathVariable String quoteCurrency) throws RatesNotFoundException {
         clientRequestService.save(clientRequestDTO);
         SpecificRateResponse fetched = ratesCollectorService.getSpecificRateData(clientRequestDTO.getBaseCurrency(), quoteCurrency);
         return ResponseEntity.ok(fetched);
     }
    @PostMapping("/postClientRequest")
    public ResponseEntity<ClientRequest> postRequest(@RequestBody ClientRequestDTO clientRequestDTO){
        ClientRequest saved = clientRequestService.save(clientRequestDTO);
        return ResponseEntity.ok(saved);
    }
    */


}

package com.example.gateway.api.controller;

import com.example.gateway.api.controller.dto.ClientHistoryRequestDTO;
import com.example.gateway.api.controller.dto.ClientRequestDTO;
import com.example.gateway.ratesCollector.controller.dto.CurrentResponse;
import com.example.gateway.ratesCollector.controller.dto.HistoryResponse;
import com.example.gateway.api.service.ClientRequestService;
import com.example.gateway.exceptions.ClientRequestExeption;
import com.example.gateway.exceptions.RatesNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/json_api")
public class ClientController {
    @Autowired
    private ClientRequestService clientRequestService;

    @PostMapping("/current")
    public ResponseEntity<CurrentResponse> postCurrent(@RequestBody ClientRequestDTO clientRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        CurrentResponse fetchedResponse = clientRequestService.getCurrentResponse(clientRequestDTO);
        return ResponseEntity.ok(fetchedResponse);
    }

    @PostMapping("/history")
    public ResponseEntity<HistoryResponse> postHistory(@RequestBody ClientHistoryRequestDTO clientHistoryRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        HistoryResponse fetchedResponse = clientRequestService.getHistoryResponse(clientHistoryRequestDTO);
        return ResponseEntity.ok(fetchedResponse);
    }

   /* @PostMapping("/current/{quoteCurrency}")
     public ResponseEntity<CurrentResponse> postCurrentByQuoteRate(@RequestBody ClientRequestDTO clientRequestDTO, @PathVariable String quoteCurrency) throws RatesNotFoundException, ClientRequestExeption {
         CurrentResponse fetched = clientRequestService.getCurrentPairResponse(clientRequestDTO, quoteCurrency);
         return ResponseEntity.ok(fetched);
     }*/

    /*@PostMapping("/postClientRequest")
    public ResponseEntity<ClientRequest> postRequest(@RequestBody ClientRequestDTO clientRequestDTO){
        ClientRequest saved = clientRequestService.save(clientRequestDTO);
        return ResponseEntity.ok(saved);
    }*/



}

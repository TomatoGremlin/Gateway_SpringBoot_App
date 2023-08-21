package com.example.gateway.publisher;

import com.example.gateway.api.model.ClientRequest;
import com.example.gateway.ratesCollector.model.ExchangeRateEntry;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "producer/send-message/direct")
public class RabbitMQController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping(value = "/clientRequests")
    public ResponseEntity<ClientRequest> sendRequestToQueue(@RequestBody ClientRequest clientRequest) {
        amqpTemplate.convertAndSend("direct-exchange", "clientRequest", clientRequest);
        return ResponseEntity.ok(clientRequest);
    }


    @PostMapping(value = "/rates")
    public ResponseEntity<ExchangeRateEntry> sendRatesToQueue(@RequestBody ExchangeRateEntry exchangeRateEntry) {
        amqpTemplate.convertAndSend("direct-exchange", "rates", exchangeRateEntry);
        return ResponseEntity.ok(exchangeRateEntry);
    }

}


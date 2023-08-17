package com.example.gateway.Api.controller;

import com.example.gateway.Api.model.ClientRequest;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/send-message/direct")
public class RabbitMQController {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @PostMapping(value = "/producer")
    public ResponseEntity<ClientRequest> producer(@RequestParam("exchangeName") String exchange, @RequestParam("routingKey") String routingKey,
                                                  @RequestBody ClientRequest clientRequest) {

        amqpTemplate.convertAndSend(exchange, routingKey, clientRequest);
        return ResponseEntity.ok(clientRequest);
    }

}


package com.example.gateway.api.controller.dto;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Random;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientRequestDTO {
    UUID requestId;
    String service;
    long timestamp;
    String client;
    String baseCurrency;


    public void setServiceRandom() {
        Random random = new Random();
        int randomNumber = random.nextInt(2) + 1;
        this.service =  "EXT_SERVICE_" + randomNumber ;
    }
}

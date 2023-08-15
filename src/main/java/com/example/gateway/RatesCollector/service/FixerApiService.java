package com.example.gateway.RatesCollector.service;

import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

// fetch rates data from the fixer api
@Service
public class FixerApiService {
    @Autowired
    private RestTemplate restTemplate;
    private static final String URL = "http://data.fixer.io/api/latest?access_key=1a60404e6ffe7202fe8acda599f9871a&base=EUR&symbols=GBP,JPY,CAD";

    public RatesDTO fetchData() {
        ResponseEntity<RatesDTO> response = restTemplate.getForEntity(URL, RatesDTO.class);
        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("API request failed with status code: " + response.getStatusCode());
        }
    }
}
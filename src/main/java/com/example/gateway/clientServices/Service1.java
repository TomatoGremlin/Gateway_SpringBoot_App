/*package com.example.gateway.clientServices;

import com.example.gateway.JsonApi.model.ClientRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@AllArgsConstructor
@Service
public class Service1 {

    private final String apiUrl;
    private final RestTemplate restTemplate;
    private String service;
    private

    public void sendApiRequest(ClientRequest apiRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ClientRequest> requestEntity = new HttpEntity<>(apiRequest, headers);

        restTemplate.postForObject(apiUrl, requestEntity, Void.class);
    }
}
*/
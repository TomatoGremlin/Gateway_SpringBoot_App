package com.example.gateway.JsonApi.service;

import com.example.gateway.JsonApi.repository.ClientRequestsRepo;
import com.example.gateway.JsonApi.model.ClientRequest;
import com.example.gateway.JsonApi.model.ClientRequestDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientRequestService {
    @Autowired
    private ClientRequestsRepo clientRequestsRepo;

    public ClientRequest save(ClientRequestDTO clientRequestDTO){
        ClientRequest requestToSave = new ClientRequest();
        requestToSave.setRequestId(clientRequestDTO.getRequestId());
        requestToSave.setService(clientRequestDTO.getService());
        requestToSave.setClient(clientRequestDTO.getClient());
        requestToSave.setTime(clientRequestDTO.getTimestamp());

        clientRequestsRepo.save(requestToSave);
        return requestToSave;
    }

}

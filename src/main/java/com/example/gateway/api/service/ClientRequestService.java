package com.example.gateway.api.service;

import com.example.gateway.api.controller.dto.ClientHistoryRequestDTO;
import com.example.gateway.api.repository.ClientRequestsRepo;
import com.example.gateway.api.model.ClientRequest;
import com.example.gateway.api.controller.dto.ClientRequestDTO;
import com.example.gateway.ratesCollector.controller.dto.CommonResponse;
import com.example.gateway.exceptions.ClientRequestExeption;
import com.example.gateway.exceptions.RatesNotFoundException;
import com.example.gateway.ratesCollector.model.AuditLog;
import com.example.gateway.ratesCollector.service.RatesCollectorService;
import com.example.gateway.ratesCollector.service.RatesUtilService;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClientRequestService {
    @Autowired
    private ClientRequestsRepo clientRequestsRepo;
    @Autowired
    private RatesCollectorService ratesCollectorService;
    @Autowired
    private AmqpTemplate amqpTemplate;
    @Autowired
    private RatesUtilService ratesUtilService;

    public CommonResponse getCurrentResponse(ClientRequestDTO clientRequestDTO) throws RatesNotFoundException, ClientRequestExeption {
        handleClientRequest(clientRequestDTO);
        CommonResponse fetched = ratesCollectorService.getLatestRatesData(clientRequestDTO.getBaseCurrency());
        return fetched;
    }
    public List<AuditLog>  getHistoryResponse(ClientHistoryRequestDTO historyRequestDTO ) throws RatesNotFoundException, ClientRequestExeption {
        handleClientRequest(historyRequestDTO);
        List<AuditLog> fetched = ratesCollectorService.getLatestRatesForPeriod(historyRequestDTO.getBaseCurrency(), historyRequestDTO.getPeriod());
        return fetched;
    }


    private void  handleClientRequest(ClientRequestDTO clientRequestDTO) throws  ClientRequestExeption {
        checkIfRequestExists(clientRequestDTO.getRequestId());
        ClientRequest clientRequest = save(clientRequestDTO);
        amqpTemplate.convertAndSend("direct-exchange", "clientRequest", clientRequest);
    }

    public void checkIfRequestExists(UUID id) throws ClientRequestExeption {
        Optional<ClientRequest> clientRequest = clientRequestsRepo.findRequestById(id);
        if (clientRequest.isPresent()) {
            throw new ClientRequestExeption("Request with id " + id + " already exists in database", id );
        }
    }


    public ClientRequest save(ClientRequestDTO clientRequestDTO){
        ClientRequest requestToSave = new ClientRequest();
        requestToSave.setRequestId(clientRequestDTO.getRequestId());
        requestToSave.setService(clientRequestDTO.getService());
        requestToSave.setClient(clientRequestDTO.getClient());
        long timestamp = clientRequestDTO.getTimestamp();
        LocalDateTime time = ratesUtilService.convertUnixTimestampToUTC(timestamp);
        requestToSave.setTime(time);

        clientRequestsRepo.save(requestToSave);
        return requestToSave;
    }

}

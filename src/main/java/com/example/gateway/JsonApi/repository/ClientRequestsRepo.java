package com.example.gateway.JsonApi.repository;

import com.example.gateway.JsonApi.model.ClientRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRequestsRepo extends JpaRepository<ClientRequest, UUID> {

}

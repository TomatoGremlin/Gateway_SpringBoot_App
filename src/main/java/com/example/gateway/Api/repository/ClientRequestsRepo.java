package com.example.gateway.Api.repository;

import com.example.gateway.Api.model.ClientRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClientRequestsRepo extends JpaRepository<ClientRequest, UUID> {

}

package com.example.gateway.api.repository;

import com.example.gateway.api.model.ClientRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ClientRequestsRepo extends JpaRepository<ClientRequest, UUID> {
    @Query(value = "SELECT * FROM client_statistics  WHERE request_id = :request_id ", nativeQuery = true)
    public Optional<ClientRequest> findRequestById(@Param("request_id") UUID request_id);

}

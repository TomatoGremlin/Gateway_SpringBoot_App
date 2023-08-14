package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.AuditLog;
import com.example.gateway.RatesCollector.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
}

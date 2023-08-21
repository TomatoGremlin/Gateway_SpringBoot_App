package com.example.gateway.ratesCollector.service;

import com.example.gateway.ratesCollector.model.AuditLog;
import com.example.gateway.ratesCollector.controller.dto.RatesDTO;
import com.example.gateway.ratesCollector.repository.AuditLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepo auditLogRepo;

    public AuditLog createAuditLog(RatesDTO ratesDTO, String operationType, String currency, BigDecimal rate) {
        AuditLog auditLog = new AuditLog();
        auditLog.setRequestId(UUID.randomUUID());
        auditLog.setOperationType(operationType);
        auditLog.setBase(ratesDTO.getBase());
        auditLog.setCurrency(currency);
        auditLog.setRate(rate);
        auditLog.setDateUpdated(LocalDateTime.now());

        return  auditLogRepo.save(auditLog);
    }

}

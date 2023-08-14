package com.example.gateway.RatesCollector.service;

import com.example.gateway.RatesCollector.model.AuditLog;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepository auditLogRepository;

    public AuditLog createAuditLog(RatesDTO ratesDTO, String currency, double rate) {
        AuditLog auditLog = new AuditLog();
        auditLog.setRequestId(UUID.randomUUID());
        auditLog.setBase(ratesDTO.getBase());
        auditLog.setCurrency(currency);
        auditLog.setRate(rate);
        auditLog.setDateUpdated(LocalDateTime.now());

        return  auditLogRepository.save(auditLog);
    }

}

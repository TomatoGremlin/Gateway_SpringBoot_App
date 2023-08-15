package com.example.gateway.RatesCollector.service;

import com.example.gateway.RatesCollector.model.DataBase.AuditLog;
import com.example.gateway.RatesCollector.model.DTO.RatesDTO;
import com.example.gateway.RatesCollector.repository.AuditLogRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditLogService {
    @Autowired
    private AuditLogRepo auditLogRepo;

    public AuditLog createAuditLog(RatesDTO ratesDTO, String currency, BigDecimal rate) {
        AuditLog auditLog = new AuditLog();
        auditLog.setRequestId(UUID.randomUUID());
        auditLog.setBase(ratesDTO.getBase());
        auditLog.setCurrency(currency);
        auditLog.setRate(rate);
        auditLog.setDateUpdated(LocalDateTime.now());

        return  auditLogRepo.save(auditLog);
    }

}

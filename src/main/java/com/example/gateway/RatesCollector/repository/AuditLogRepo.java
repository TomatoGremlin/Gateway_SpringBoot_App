package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.DataBase.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepo extends JpaRepository<AuditLog, UUID> {
    @Query(value = "SELECT * FROM update_logs  WHERE base = :currency AND date_updated >= :date_updated ORDER BY date_updated DESC", nativeQuery = true)
    public List<AuditLog> findLatestRatesByTimeStamp(@Param("currency") String currency, @Param("date_updated") LocalDateTime date_updated);
}

package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.RatesResponseData;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatesRepo extends JpaRepository<RatesResponseData, UUID> {
    @Query("SELECT r FROM RatesResponseData r JOIN FETCH r.exchangeRateList e WHERE r.base = :currency ORDER BY r.timestamp DESC")
    List<RatesResponseData> findLatestRatesByCurrency(@Param("currency") String currency, Pageable pageable);
    @Query(value = "SELECT * FROM rates WHERE timestamp = :timestamp", nativeQuery = true)
    public Optional<RatesResponseData> findByTimestamp(@Param("timestamp") long timestamp);
}



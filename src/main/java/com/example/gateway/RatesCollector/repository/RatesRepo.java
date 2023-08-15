package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.DataBase.RatesResponseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatesRepo extends JpaRepository<RatesResponseData, UUID> {

    @Query(value = "SELECT * FROM rates_data  WHERE base = :currency ", nativeQuery = true)
    public RatesResponseData findLatestRatesByCurrency(@Param("currency") String currency);

    @Query(value = "SELECT * FROM rates_data WHERE base = :currency", nativeQuery = true)
    public Optional<RatesResponseData> findByBaseCurrency(@Param("currency") String currency);

    @Query(value = "SELECT * FROM rates_data WHERE time_stamp = :timestamp", nativeQuery = true)
    public Optional<RatesResponseData> findByTimestamp(@Param("timestamp") long timestamp);

}



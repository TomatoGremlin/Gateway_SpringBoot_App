package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.RatesResponseData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatesRepo extends JpaRepository<RatesResponseData, UUID> {

    @Query(value = "SELECT * FROM rates_data  WHERE base = :currency ORDER BY time_stamp DESC LIMIT 1", nativeQuery = true)
    public RatesResponseData findLatestRatesByCurrency(@Param("currency") String currency);

    @Query(value = "SELECT * FROM rates_data  WHERE base = :currency AND time_stamp >= :periodTimeStamp ORDER BY time_stamp DESC", nativeQuery = true)
    public List<RatesResponseData> findLatestRatesByTimeStamp(@Param("currency") String currency, @Param("periodTimeStamp") long periodTimeStamp);

    @Query(value = "SELECT * FROM rates_data WHERE time_stamp = :timestamp", nativeQuery = true)
    public Optional<RatesResponseData> findByTimestamp(@Param("timestamp") long timestamp);

    @Query(value = "SELECT * FROM rates_data WHERE base = :currency", nativeQuery = true)
    public Optional<RatesResponseData> findByBaseCurrency(@Param("currency") String currency);

    @Query(value = "SELECT COUNT(*) FROM rates_data", nativeQuery = true)
    public int checkIfEmpty();

}



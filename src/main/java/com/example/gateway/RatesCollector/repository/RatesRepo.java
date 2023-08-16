package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.DataBase.ExchangeRateEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RatesRepo extends JpaRepository<ExchangeRateEntry, UUID> {

    @Query(value = "SELECT * FROM rates_data  WHERE base = :base AND currency =:quoteCurrency ", nativeQuery = true)
    public ExchangeRateEntry findLatestRatesByBaseQuotePair(@Param("base") String base, @Param("quoteCurrency") String quoteCurrency);
    @Query(value = "SELECT * FROM rates_data  WHERE base = :currency ", nativeQuery = true)
    public List<ExchangeRateEntry> findLatestRatesByCurrency(@Param("currency") String currency);

    @Query(value = "SELECT COUNT(*)  FROM rates_data  WHERE base = :currency ", nativeQuery = true)
    public int findLatestRowsByCurrency(@Param("currency") String currency);

    @Query(value = "SELECT * FROM rates_data WHERE base = :currency", nativeQuery = true)
    public Optional<ExchangeRateEntry> findByBaseCurrency(@Param("currency") String currency);

    @Query(value = "SELECT * FROM rates_data WHERE time_stamp = :timestamp", nativeQuery = true)
    public Optional<ExchangeRateEntry> findByTimestamp(@Param("timestamp") long timestamp);

}



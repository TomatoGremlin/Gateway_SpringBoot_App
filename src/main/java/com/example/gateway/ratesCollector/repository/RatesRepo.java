package com.example.gateway.ratesCollector.repository;

import com.example.gateway.ratesCollector.model.ExchangeRateEntry;
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
    public Optional<ExchangeRateEntry> findLatestRatesByBaseQuotePair(@Param("base") String base, @Param("quoteCurrency") String quoteCurrency);
    @Query(value = "SELECT * FROM rates_data  WHERE base = :currency ", nativeQuery = true)
    public Optional<List<ExchangeRateEntry>> findLatestRatesByBaseCurrency(@Param("currency") String currency);

    @Query(value = "SELECT COUNT(*)  FROM rates_data  WHERE base = :currency ", nativeQuery = true)
    public int findLatestRowsByCurrency(@Param("currency") String currency);

}



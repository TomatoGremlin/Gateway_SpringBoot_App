package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
@Repository
public interface ExchangeRatesRepo extends JpaRepository<ExchangeRate, UUID> {
    List<ExchangeRate> findByRatesId(UUID ratesId);

}

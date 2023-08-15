package com.example.gateway.RatesCollector.repository;

import com.example.gateway.RatesCollector.model.DataBase.ExchangeRate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface ExchangeRatesRepo extends JpaRepository<ExchangeRate, UUID> {

}

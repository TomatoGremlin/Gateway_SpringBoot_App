package com.example.gateway.RatesCollector.model.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRedisRepo extends CrudRepository<ExchangeRatesRedisAggregate, String> {
}

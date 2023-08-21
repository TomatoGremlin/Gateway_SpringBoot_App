package com.example.gateway.cache;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExchangeRateRedisRepo extends CrudRepository<ExchangeRatesRedisAggregate, String> {
}

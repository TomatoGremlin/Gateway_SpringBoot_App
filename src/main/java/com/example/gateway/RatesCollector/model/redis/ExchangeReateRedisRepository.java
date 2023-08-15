package com.example.gateway.RatesCollector.model.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExchangeReateRedisRepository extends CrudRepository<CurrencyExchangeRates, String> {
    CurrencyExchangeRates  findByBaseCurrency(String baseCurrency);
}

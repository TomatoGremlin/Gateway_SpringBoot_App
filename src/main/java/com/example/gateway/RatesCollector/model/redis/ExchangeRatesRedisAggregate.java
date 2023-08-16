package com.example.gateway.RatesCollector.model.redis;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)

@RedisHash("currency-exchange-rate")
public class ExchangeRatesRedisAggregate implements Serializable {
    @Id
    String baseCurrency;
    Map<String, BigDecimal> foreignRates = new HashMap<>();
    long timestamp;
}

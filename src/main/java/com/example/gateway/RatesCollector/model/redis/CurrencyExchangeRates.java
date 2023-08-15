package com.example.gateway.RatesCollector.model.redis;


import lombok.*;
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
@RedisHash("currency-exchange-rate")
public class CurrencyExchangeRates implements Serializable {
    @Id
    private String baseCurrency;
    private Map<String, BigDecimal> foreignRates = new HashMap<>();
}

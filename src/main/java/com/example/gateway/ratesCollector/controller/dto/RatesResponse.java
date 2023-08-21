package com.example.gateway.ratesCollector.controller.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class RatesResponse {
    String base;
    Map<String, BigDecimal> rates;
    LocalDateTime dateLastUpdated;
}

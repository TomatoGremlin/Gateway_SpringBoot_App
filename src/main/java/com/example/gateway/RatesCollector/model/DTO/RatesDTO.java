package com.example.gateway.RatesCollector.model.DTO;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class RatesDTO {
    boolean success;
    long timestamp;
    String base;
    String date;
    Map<String, BigDecimal> rates;
}

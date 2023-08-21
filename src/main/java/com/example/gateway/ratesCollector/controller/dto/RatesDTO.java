package com.example.gateway.ratesCollector.controller.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    LocalDate date;
    Map<String, BigDecimal> rates;
}

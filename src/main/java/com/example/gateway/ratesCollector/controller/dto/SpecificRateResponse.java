package com.example.gateway.ratesCollector.controller.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor

public class SpecificRateResponse {
    String base;
    String currency;
    BigDecimal rate;
    LocalDateTime dateTime;
}

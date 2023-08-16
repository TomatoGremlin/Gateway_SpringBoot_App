package com.example.gateway.RatesCollector.model.DTO;

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

public class SpecificRate {
    String base;
    String currency;
    BigDecimal rate;
    LocalDateTime dateTime;
}

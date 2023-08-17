package com.example.gateway.RatesCollector.model.DTO;

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
public class CommonResponse {
    Map<String, BigDecimal> rates;
    long timestamp;
    LocalDateTime localDateTime;
}

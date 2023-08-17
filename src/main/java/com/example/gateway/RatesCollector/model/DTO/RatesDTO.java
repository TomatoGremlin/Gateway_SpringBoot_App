package com.example.gateway.RatesCollector.model.DTO;

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
    @JsonIgnore
    boolean success;
    long timestamp;
    String base;
    LocalDate date;
    Map<String, BigDecimal> rates;
}

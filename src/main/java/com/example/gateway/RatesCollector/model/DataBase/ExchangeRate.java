package com.example.gateway.RatesCollector.model.DataBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = "ratesId")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "exchange_rates")
@Entity

public class ExchangeRate {

    @ManyToOne
    @JoinColumn(name = "request_id")
    @JsonIgnore
    @OnDelete(action = OnDeleteAction.CASCADE)
    RatesResponseData ratesResponseData;

    @Id
    @Column(name = "ratesId")
    @JsonIgnore
    UUID ratesId;

    @Column(name = "currency")
    String currency;

    @Column(name = "rate")
    BigDecimal rate;
}

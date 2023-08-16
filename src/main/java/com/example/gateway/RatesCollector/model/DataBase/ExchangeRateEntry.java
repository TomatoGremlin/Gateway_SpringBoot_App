package com.example.gateway.RatesCollector.model.DataBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)

@Getter
@Setter
@ToString(exclude = "requestId")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rates_data")
@Entity
public class ExchangeRateEntry {

    @Id
    @Column(name = "request_id")
    @JsonIgnore
    UUID requestId;

    @Column(name = "time_stamp")
    long timestamp;

    @Column(name = "base")
    String base;

    @Column(name = "currency")
    String currency;

    @Column(name = "rate", precision = 10, scale = 6)
    BigDecimal rate;

}

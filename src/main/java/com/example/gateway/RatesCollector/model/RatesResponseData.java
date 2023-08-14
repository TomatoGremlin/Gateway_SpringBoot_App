package com.example.gateway.RatesCollector.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.List;
import java.util.UUID;

@EntityListeners(AuditingEntityListener.class)

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rates_data")
@Entity
public class RatesResponseData {

    @Id
    @Column(name = "request_id")
    UUID requestId;

    @Column(name = "time_stamp")
    long timestamp;

    @Column(name = "base")
    String base;

    @CreatedDate
    @Column(name = "date")
    String date;

    @OneToMany(mappedBy = "ratesResponseData", cascade = CascadeType.ALL) // mappedBy = this is the object in the other entity
    List<ExchangeRate> exchangeRateList;
}

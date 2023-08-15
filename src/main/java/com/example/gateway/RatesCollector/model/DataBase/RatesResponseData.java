package com.example.gateway.RatesCollector.model.DataBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@ToString(exclude = "requestId")
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "rates_data")
@Entity
public class RatesResponseData {

    @Id
    @Column(name = "request_id")
    @JsonIgnore
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

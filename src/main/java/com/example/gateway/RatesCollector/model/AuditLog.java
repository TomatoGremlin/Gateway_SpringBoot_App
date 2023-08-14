package com.example.gateway.RatesCollector.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "update_logs")
@Entity
public class AuditLog {

    @Id
    @Column(name = "request_id")
    UUID requestId;
    @LastModifiedDate
    @Column(name = "date_updated")
    LocalDateTime dateUpdated;

    @Column(name = "base")
    String base;

    @Column(name = "currency")
    String currency;

    @Column(name = "rate")
    double rate;

}

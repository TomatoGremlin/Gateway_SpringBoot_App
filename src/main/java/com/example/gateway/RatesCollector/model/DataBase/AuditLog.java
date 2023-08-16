package com.example.gateway.RatesCollector.model.DataBase;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString(exclude = {"base", "requestId"})
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "update_logs")
@Entity
public class AuditLog {

    @Id
    @Column(name = "request_id")
    @JsonIgnore
    UUID requestId;
    @LastModifiedDate
    @Column(name = "date_updated")
    LocalDateTime dateUpdated;

    @Column(name = "operation_type")
    String operationType;

    @Column(name = "base")
    String base;

    @Column(name = "currency")
    String currency;

    @Column(name = "rate", precision = 10, scale = 6)
    BigDecimal rate;

}

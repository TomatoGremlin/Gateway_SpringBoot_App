package com.example.gateway.ratesCollector.controller.dto;

import com.example.gateway.ratesCollector.model.AuditLog;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class HistoryResponse {
    List<AuditLog> historyLogsList;
}

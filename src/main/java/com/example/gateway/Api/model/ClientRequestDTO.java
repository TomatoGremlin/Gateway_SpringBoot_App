package com.example.gateway.Api.model;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientRequestDTO {
    UUID requestId;
    String service = "service 1";
    String timestamp;
    String client;
    String currency;
    long period;
}

package com.example.gateway.JsonApi;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "client_requests")
@Entity
public class ClientRequest {
    @Column(name = "request_id")
    String service;

    @Id
    @Column(name = "request_id")
    UUID requestId;

    @Column(name = "UTC_time")
    String time;

    @Column(name = "end_client")
    String client;
}

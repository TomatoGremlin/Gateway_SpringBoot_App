package com.example.gateway.api.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "client_statistics")
@Entity
public class ClientRequest implements Serializable {

    @Id
    @Column(name = "request_id")
    UUID requestId;

    @Column(name = "service")
    String service;

    @Column(name = "end_client")
    String client;

    @Column(name = "time")
    LocalDateTime time;

}

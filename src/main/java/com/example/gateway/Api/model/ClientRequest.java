package com.example.gateway.Api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;
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

    @Column(name = "time(UTC)")
    String time;

}

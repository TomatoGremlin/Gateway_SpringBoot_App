package com.example.gateway.api.controller.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ClientHistoryRequestDTO extends ClientRequestDTO{
    long period;
}

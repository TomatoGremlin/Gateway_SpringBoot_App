package com.example.gateway.exceptions;

import java.util.UUID;

public class ClientRequestExeption extends Exception {
    private UUID id ;

    public ClientRequestExeption(String message, UUID id) {
        super(message);
        this.id = id;
    }
}

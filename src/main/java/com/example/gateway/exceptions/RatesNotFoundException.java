package com.example.gateway.exceptions;

public class RatesNotFoundException extends Exception {
    private String wrongBaseCurrency;

    public RatesNotFoundException(String message,  String wrongBaseCurrency) {
        super(message);
        this.wrongBaseCurrency = wrongBaseCurrency;
    }
}

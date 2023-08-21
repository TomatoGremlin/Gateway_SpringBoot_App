package com.example.gateway.exceptions;

public class HistoryRatesNotFoundException extends RatesNotFoundException {
    private long wrongPeriod;

    public HistoryRatesNotFoundException(String message, String wrongBaseCurrency, long wrongPeriod) {
        super(message, wrongBaseCurrency);
        this.wrongPeriod = wrongPeriod;
    }
}

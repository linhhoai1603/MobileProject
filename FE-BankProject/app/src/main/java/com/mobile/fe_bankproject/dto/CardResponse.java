package com.mobile.fe_bankproject.dto;

public class CardResponse {
    private String cardNumber;
    private String cardHolder;
    private String status;
    private String defaultPin;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDefaultPin() {
        return defaultPin;
    }

    public void setDefaultPin(String defaultPin) {
        this.defaultPin = defaultPin;
    }
} 
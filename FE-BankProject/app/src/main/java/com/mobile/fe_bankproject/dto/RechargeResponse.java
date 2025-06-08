package com.mobile.fe_bankproject.dto;




public class RechargeResponse {
    private String accountNumber;
    private String phoneNumber;
    private int cardAmount;
    private String status;
    private TelcoProvider telcoProvider;
    private String message;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getCardAmount() {
        return cardAmount;
    }

    public void setCardAmount(int cardAmount) {
        this.cardAmount = cardAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TelcoProvider getTelcoProvider() {
        return telcoProvider;
    }

    public void setTelcoProvider(TelcoProvider telcoProvider) {
        this.telcoProvider = telcoProvider;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

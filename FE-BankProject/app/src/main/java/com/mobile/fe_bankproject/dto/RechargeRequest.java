package com.mobile.fe_bankproject.dto;


public class RechargeRequest {
    private String accountNumber;
    private String pin;
    private String phoneNumber;
    private TelcoProvider telcoProvider;
    private double amount;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public TelcoProvider getTelcoProvider() {
        return telcoProvider;
    }

    public void setTelcoProvider(TelcoProvider telcoProvider) {
        this.telcoProvider = telcoProvider;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
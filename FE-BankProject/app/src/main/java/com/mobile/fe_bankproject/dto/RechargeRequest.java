package com.mobile.fe_bankproject.dto;


public class RechargeRequest {
    private String accountNumber;
    private String pin;
    private String phoneNumber;
    private int idPhoneCard;

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

    public int getIdPhoneCard() {
        return idPhoneCard;
    }

    public void setIdPhoneCard(int idPhoneCard) {
        this.idPhoneCard = idPhoneCard;
    }

}
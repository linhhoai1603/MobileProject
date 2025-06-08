package com.mobile.fe_bankproject.dto;


public class RechargePreviewDTO {
    private String phoneNumber;
    private int amount;
    private TelcoProvider telcoProvider;
    private double balanceBefore;
    private double balanceAfter;

    // Getters & Setters

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public TelcoProvider getTelcoProvider() {
        return telcoProvider;
    }

    public void setTelcoProvider(TelcoProvider telcoProvider) {
        this.telcoProvider = telcoProvider;
    }

    public double getBalanceBefore() {
        return balanceBefore;
    }

    public void setBalanceBefore(double balanceBefore) {
        this.balanceBefore = balanceBefore;
    }

    public double getBalanceAfter() {
        return balanceAfter;
    }

    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
}

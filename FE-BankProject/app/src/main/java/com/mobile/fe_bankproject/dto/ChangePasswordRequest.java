package com.mobile.fe_bankproject.dto;

public class ChangePasswordRequest {
    private String accountNumber;
    private String currentPass;
    private String newPass;

    public ChangePasswordRequest(String accountNumber, String currentPass, String newPass) {
        this.accountNumber = accountNumber;
        this.currentPass = currentPass;
        this.newPass = newPass;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrentPass() {
        return currentPass;
    }

    public void setCurrentPass(String currentPass) {
        this.currentPass = currentPass;
    }

    public String getNewPass() {
        return newPass;
    }

    public void setNewPass(String newPass) {
        this.newPass = newPass;
    }
} 
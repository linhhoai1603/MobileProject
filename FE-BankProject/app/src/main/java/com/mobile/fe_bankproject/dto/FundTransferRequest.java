package com.mobile.fe_bankproject.dto;

public class FundTransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;

    public FundTransferRequest(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        this.fromAccountNumber = fromAccountNumber;
        this.toAccountNumber = toAccountNumber;
        this.amount = amount;
        this.description = description;
    }
}

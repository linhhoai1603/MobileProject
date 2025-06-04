package com.mobile.fe_bankproject.network.model;

public class TransferPreviewRequest {
    private String accountNumber;
    private String accountName;
    private String amount;
    private String transferContent;

    // Constructor
    public TransferPreviewRequest(String accountNumber, String accountName, String amount, String transferContent) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.amount = amount;
        this.transferContent = transferContent;
    }

    // Getters (Optional, but good practice)
    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getAmount() {
        return amount;
    }

    public String getTransferContent() {
        return transferContent;
    }
} 
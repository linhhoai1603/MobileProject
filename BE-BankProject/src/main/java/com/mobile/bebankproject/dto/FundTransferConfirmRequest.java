package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class FundTransferConfirmRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String otp;
} 
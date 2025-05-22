package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class FundTransferRequest {
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;
}

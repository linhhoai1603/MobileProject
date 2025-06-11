package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class LoanResponse {
    private Long id;
    private String accountNumber;
    private double amount;
    private int term;
    private double totalPayable;
    private String note;
    private String status;
}
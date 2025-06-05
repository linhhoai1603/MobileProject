package com.mobile.fe_bankproject.dto;

import lombok.Data;

@Data
public class RechargeRequest {
    private String accountNumber;
    private String pin;
    private String phoneNumber;
    private TelcoProvider telcoProvider;
    private double amount;
} 
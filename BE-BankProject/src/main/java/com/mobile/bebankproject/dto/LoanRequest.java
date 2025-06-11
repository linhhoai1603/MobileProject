package com.mobile.bebankproject.dto;
import lombok.Data;

@Data
public class LoanRequest {
    private String accountNumber;
    private double amount;
    private int term; 
    private String note;
}
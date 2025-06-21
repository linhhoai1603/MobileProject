package com.mobile.bebankproject.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Integer id;              // ← was Long, now Integer
    private String accountNumber;
    private double amount;
    private String status;
    private String description;
    private LocalDateTime transactionDate;
    private Integer loanId;          // if you need to return the loan ref
}
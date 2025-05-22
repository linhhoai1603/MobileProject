package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class FundTransferPreview {
    private String fromAccountNumber;
    private String fromAccountName;
    private String toAccountNumber;
    private String toAccountName;
    private double amount;
    private String description;
    // Có thể thêm các thông tin khác như phí giao dịch
}
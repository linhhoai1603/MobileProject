package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class DataPackagePreview {
    private String accountNumber;
    private String phoneNumber;
    private String packageName;
    private int quantity;
    private int validDate;
    private double price;
} 
package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class DataPackageRequest {
    private String accountNumber;
    private String phoneNumber;
    private int packageId;
    private String PIN;
} 
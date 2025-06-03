package com.mobile.fe_bankproject.dto;

import lombok.Data;

@Data
public class DataPackageRequest {
    private String accountNumber;
    private String phoneNumber;
    private int packageId;
} 
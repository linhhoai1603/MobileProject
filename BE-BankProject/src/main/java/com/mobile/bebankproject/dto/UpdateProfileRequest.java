package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String accountNumber;
    private String fullName;
    private String email;
    private String phone;
    private String pin;
} 
package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.TelcoProvider;
import lombok.Data;

@Data
public class RechargeRequest {
    private String accountNumber;
    private String pin;
    private String phoneNumber;
    private int idPhoneCard;
} 
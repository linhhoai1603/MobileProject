package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.TelcoProvider;
import lombok.Data;

@Data
public class RechargePreviewDTO {
    private String phoneNumber;
    private int amount;
    private TelcoProvider telcoProvider;
    private double balanceBefore;
    private double balanceAfter;

    // Getters & Setters
}

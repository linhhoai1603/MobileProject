package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.TelcoProvider;
import lombok.Data;

@Data
public class RechargeResponse {
    private String accountNumber;
    private String phoneNumber;
    private int cardAmount;
    private String status;
    private TelcoProvider telcoProvider;
    private String message;
}

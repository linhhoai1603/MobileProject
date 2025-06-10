package com.mobile.bebankproject.service;

import com.mobile.bebankproject.dto.RechargePreviewDTO;
import com.mobile.bebankproject.model.TelcoProvider;

public interface RechargesService {
    boolean purchaseRecharge(String accountNumber, String pin, String phoneNumber, int idPhoneCard);

    RechargePreviewDTO previewRecharge(String accountNumber, String phoneNumber, int phoneCardId);
}

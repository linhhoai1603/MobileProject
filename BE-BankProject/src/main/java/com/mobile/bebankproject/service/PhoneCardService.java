package com.mobile.bebankproject.service;

import com.mobile.bebankproject.model.PhoneCard;
import com.mobile.bebankproject.model.TelcoProvider;

import java.util.List;

public interface PhoneCardService {
    PhoneCard getPhoneCardServiceById(int id);
    List<PhoneCard> getAllPhoneCards();
    boolean purchasePhoneCard(int id);
    List<PhoneCard> getPhoneCardsByTelcoProvider(TelcoProvider telcoProvider);
}

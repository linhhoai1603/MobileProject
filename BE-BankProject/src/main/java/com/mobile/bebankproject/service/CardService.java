package com.mobile.bebankproject.service;

import com.mobile.bebankproject.model.Card;
import com.mobile.bebankproject.dto.CardResponse;

public interface CardService {
    Card getCardByNumber(String cardNumber);
    void lockCard(String cardNumber, String pin);
    void unlockCard(String cardNumber, String pin);
    boolean verifyPin(String cardNumber, String pin);
    boolean changePin(String cardNumber, String oldPin, String newPin);
    CardResponse createNewCard(String accountNumber);
}

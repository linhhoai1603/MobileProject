package com.mobile.bebankproject.dto;

import lombok.Data;

@Data
public class CardResponse {
    private String cardNumber;
    private String cardHolder;
    private String status;
    private String defaultPin;

    public static CardResponse fromCard(com.mobile.bebankproject.model.Card card) {
        CardResponse response = new CardResponse();
        response.setCardNumber(card.getCardNumber());
        response.setCardHolder(card.getCardHolder());
        response.setStatus(card.getStatus().toString());
        return response;
    }
} 
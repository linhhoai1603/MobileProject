package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.service.CardService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/change-pin")
    public ResponseEntity<?> changePin(@RequestBody ChangePinRequest request) {
        boolean success = cardService.changePIN(request.getCardNumber(), request.getOldPIN(), request.getNewPIN());

        if (success) {
            return ResponseEntity.ok("PIN changed successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid card number, PIN or card status");
        }
    }

    @Data
    public static class ChangePinRequest {
        private String cardNumber;
        private String oldPIN;
        private String newPIN;
    }
}

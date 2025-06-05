package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.CardResponse;
import com.mobile.bebankproject.model.Card;
import com.mobile.bebankproject.service.CardService;
import com.mobile.bebankproject.service.impl.CardServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cards")
public class CardController {
    @Autowired
    private CardService cardService;

    @GetMapping("/{cardNumber}")
    public ResponseEntity<CardResponse> getCardInfo(@PathVariable String cardNumber) {
        Card card = cardService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(CardResponse.fromCard(card));
    }

    @PostMapping("/lock")
    public ResponseEntity<?> lockCard(@RequestBody Map<String, String> request) {
        String cardNumber = request.get("cardNumber");
        String pin = request.get("pin");
        
        cardService.lockCard(cardNumber, pin);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/unlock")
    public ResponseEntity<?> unlockCard(@RequestBody Map<String, String> request) {
        String cardNumber = request.get("cardNumber");
        String pin = request.get("pin");
        
        cardService.unlockCard(cardNumber, pin);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{cardNumber}/status")
    public ResponseEntity<Map<String, String>> getCardStatus(@PathVariable String cardNumber) {
        Card card = cardService.getCardByNumber(cardNumber);
        return ResponseEntity.ok(Map.of("status", card.getStatus().toString()));
    }

    @PostMapping("/change-pin")
    public ResponseEntity<?> changePin(@RequestBody Map<String, String> request) {
        String cardNumber = request.get("cardNumber");
        String oldPin = request.get("oldPin");
        String newPin = request.get("newPin");
        
        boolean success = cardService.changePin(cardNumber, oldPin, newPin);
        if (success) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.badRequest().body("Không thể đổi PIN. Vui lòng kiểm tra lại thông tin.");
        }
    }

    @PostMapping("/verify-pin")
    public ResponseEntity<Map<String, Boolean>> verifyPin(@RequestBody Map<String, String> request) {
        String cardNumber = request.get("cardNumber");
        String pin = request.get("pin");
        
        boolean isValid = cardService.verifyPin(cardNumber, pin);
        return ResponseEntity.ok(Map.of("isValid", isValid));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createNewCard(@RequestBody Map<String, String> request) {
        String accountNumber = request.get("accountNumber");
        try {
            CardResponse newCard = cardService.createNewCard(accountNumber);
            return ResponseEntity.ok(newCard);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

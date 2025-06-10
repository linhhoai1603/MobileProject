package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.model.PhoneCard;
import com.mobile.bebankproject.model.TelcoProvider;
import com.mobile.bebankproject.service.PhoneCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/phone-card")
public class PhoneCardController {
    @Autowired

    private PhoneCardService phoneCardService;
    @GetMapping("/get/{telcoProvider}")
    public ResponseEntity<List<PhoneCard>> getPhoneCards(@PathVariable String telcoProvider) {
        try {
            TelcoProvider providerEnum = TelcoProvider.valueOf(telcoProvider.toUpperCase());
            List<PhoneCard> cards = phoneCardService.getPhoneCardsByTelcoProvider(providerEnum);
            return ResponseEntity.ok(cards);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}

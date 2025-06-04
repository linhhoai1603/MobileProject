package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.model.TelcoProvider;
import com.mobile.bebankproject.service.RechargesService;
import com.mobile.bebankproject.dto.RechargeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recharges")
public class RechargesController {

    @Autowired
    private RechargesService rechargesService;

    /**
     * Purchase a phone recharge
     * @param request The recharge request containing account number, PIN, phone number, provider and amount
     * @return Success status
     */
    @PostMapping("/purchase")
    public ResponseEntity<String> purchaseRecharge(@RequestBody RechargeRequest request) {
        try {
            boolean success = rechargesService.purchaseRecharge(
                request.getAccountNumber(),
                request.getPin(),
                request.getPhoneNumber(),
                request.getTelcoProvider(),
                request.getAmount()
            );
            if (success) {
                return ResponseEntity.ok("Recharge successful");
            } else {
                return ResponseEntity.badRequest().body("Recharge failed");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
} 
package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.RechargePreviewDTO;
import com.mobile.bebankproject.dto.RechargeResponse;
import com.mobile.bebankproject.model.PhoneCard;
import com.mobile.bebankproject.model.TelcoProvider;
import com.mobile.bebankproject.service.PhoneCardService;
import com.mobile.bebankproject.service.RechargesService;
import com.mobile.bebankproject.dto.RechargeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recharge")
public class RechargesController {

    @Autowired
    private RechargesService rechargesService;
    @Autowired
    private PhoneCardService phoneCardService;

    /**
     * Purchase a phone recharge
     * @param request The recharge request containing account number, PIN, phone number, provider and amount
     * @return Success status
     */
    @PostMapping("/purchase")
    public ResponseEntity<RechargeResponse> purchaseRecharge(@RequestBody RechargeRequest request) {
        RechargeResponse response = new RechargeResponse();
        response.setAccountNumber(request.getAccountNumber());
        response.setPhoneNumber(request.getPhoneNumber());

        try {
            boolean success = rechargesService.purchaseRecharge(
                    request.getAccountNumber(),
                    request.getPin(),
                    request.getPhoneNumber(),
                    request.getIdPhoneCard()
            );

            if (success) {
                // Lấy thông tin card để điền vào response
                PhoneCard phoneCard = phoneCardService.getPhoneCardServiceById(request.getIdPhoneCard());

                response.setCardAmount(phoneCard.getAmount());
                response.setTelcoProvider(phoneCard.getTelcoProvider());
                response.setStatus("SUCCESS");
                response.setMessage("Recharge successful");

                return ResponseEntity.ok(response);
            } else {
                response.setStatus("FAILED");
                response.setMessage("Recharge failed");
                return ResponseEntity.badRequest().body(response);
            }

        } catch (RuntimeException e) {
            response.setStatus("FAILED");
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/preview")
    public ResponseEntity<RechargePreviewDTO> previewRecharge(
            @RequestBody RechargeRequest request) {
        RechargePreviewDTO preview = rechargesService.previewRecharge(request.getAccountNumber(), request.getPhoneNumber(), request.getIdPhoneCard());
        return ResponseEntity.ok(preview);
    }

} 
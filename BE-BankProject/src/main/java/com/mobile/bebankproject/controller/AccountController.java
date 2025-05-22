package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.AccountLogin;
import com.mobile.bebankproject.dto.AccountRegister;
import com.mobile.bebankproject.dto.AccountResponse;
import com.mobile.bebankproject.dto.FundTransferRequest;
import com.mobile.bebankproject.dto.FundTransferConfirmRequest;
import com.mobile.bebankproject.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AccountRegister accountRegister) {
        accountService.createAccount(accountRegister);
        return ResponseEntity.ok("Registration successful. Please check your email for OTP to activate your account.");
    }

    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@RequestBody AccountLogin accountLogin) {
        AccountResponse account = accountService.login(accountLogin.getPhone(), accountLogin.getPassword());
        return ResponseEntity.ok(account);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<Void> sendOTP(@RequestBody String email) {
        accountService.sendOTPToChangePassword(email);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<Boolean> changePassword(@RequestParam String pass1, @RequestParam String pass2) {
        boolean result = accountService.changePassword(pass1, pass2);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/confirm-account")
    public ResponseEntity<String> confirmAccount(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String otp = request.get("otp");
        
        if (email == null || otp == null) {
            return ResponseEntity.badRequest().body("Email and OTP are required");
        }

        boolean result = accountService.confirmAccount(email, otp);
        if (result) {
            return ResponseEntity.ok("Account activated successfully");
        }
        return ResponseEntity.badRequest().body("Invalid OTP or account already activated");
    }

    @PostMapping("/check-otp")
    public ResponseEntity<Boolean> checkOTP(@RequestParam String otp) {
        boolean result = accountService.checkOTPToChangePassword(otp);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/list")
    public ResponseEntity<List<AccountResponse>> getAllAccounts() {
        List<AccountResponse> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @PostMapping("/transfer")
    public ResponseEntity<String> transfer(@RequestBody FundTransferRequest request) {
        boolean result = accountService.transferFund(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            request.getDescription()
        );
        if (result) {
            return ResponseEntity.ok("Transfer successful");
        } else {
            return ResponseEntity.badRequest().body("Transfer failed");
        }
    }

    @PostMapping("/transfer/request")
    public ResponseEntity<String> requestTransfer(@RequestBody FundTransferRequest request) {
        accountService.requestFundTransfer(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            request.getDescription()
        );
        return ResponseEntity.ok("OTP đã được gửi về email. Vui lòng xác nhận để hoàn tất chuyển khoản.");
    }

    @PostMapping("/transfer/confirm")
    public ResponseEntity<String> confirmTransfer(@RequestBody FundTransferConfirmRequest request) {
        boolean result = accountService.confirmFundTransfer(
            request.getFromAccountNumber(),
            request.getToAccountNumber(),
            request.getAmount(),
            request.getOtp()
        );
        if (result) {
            return ResponseEntity.ok("Chuyển khoản thành công!");
        } else {
            return ResponseEntity.badRequest().body("Xác nhận OTP thất bại hoặc giao dịch không hợp lệ.");
        }
    }
}

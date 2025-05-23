package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.AccountLogin;
import com.mobile.bebankproject.dto.AccountRegister;
import com.mobile.bebankproject.dto.AccountResponse;
import com.mobile.bebankproject.dto.ChangePasswordRequest;
import com.mobile.bebankproject.service.AccountService;
import com.mobile.bebankproject.util.PasswordValidator;
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

    @PostMapping( "/change-password-logined")
    public ResponseEntity<?> changePasswordLogined(@RequestBody ChangePasswordRequest request) {
        try {
            // Validate request data
            if (request.getAccountNumber() == null || request.getCurrentPass() == null || request.getNewPass() == null) {
                return ResponseEntity.badRequest().body("All fields are required");
            }

            // Validate new password security requirements
            if (!PasswordValidator.isValidPassword(request.getNewPass())) {
                return ResponseEntity.badRequest().body("New password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character");
            }

            // Validate current password and account status
            boolean isValid = accountService.validateAccountAndPassword(request.getAccountNumber(), request.getCurrentPass());
            if (!isValid) {
                return ResponseEntity.badRequest().body("Current password is incorrect or account is not active");
            }

            // Change password
            boolean passwordChanged = accountService.changePasswordLogined(request.getAccountNumber(), request.getNewPass());
            if (passwordChanged) {
                return ResponseEntity.ok("Password changed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to change password");
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping("/close")
    public ResponseEntity<?> closeAccount(@RequestBody Map<String, String> request) {
        try {
            String accountNumber = request.get("accountNumber");
            String password = request.get("password");

            if (accountNumber == null || password == null) {
                return ResponseEntity.badRequest().body("Account number and password are required");
            }

            boolean result = accountService.closeAccount(accountNumber, password);
            if (result) {
                return ResponseEntity.ok("Account closed successfully");
            } else {
                return ResponseEntity.badRequest().body("Failed to close account");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

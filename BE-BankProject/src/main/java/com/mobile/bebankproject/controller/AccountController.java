package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.AccountLogin;
import com.mobile.bebankproject.model.Account;
import com.mobile.bebankproject.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @PostMapping("/login")
    public ResponseEntity<Account> login(@RequestBody AccountLogin accountLogin) {
        Account account = accountService.login(accountLogin.getPhone(), accountLogin.getPassword());
        return ResponseEntity.ok(account);
    }
    @PostMapping("/send-otp")
    public ResponseEntity sendOTP(@RequestBody String email) {
        accountService.sendOTPToChangePassword(email);
        return ResponseEntity.ok().build();
    }
}

package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.LoanRequest;
import com.mobile.bebankproject.dto.LoanResponse;
import com.mobile.bebankproject.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loan")
public class LoanController {
    @Autowired
    private LoanService loanService;

    @PostMapping("/create")
    public ResponseEntity<LoanResponse> createLoan(@RequestBody LoanRequest request) {
        LoanResponse response = loanService.createLoan(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/list/{accountNumber}")
    public ResponseEntity<List<LoanResponse>> getLoans(@PathVariable String accountNumber) {
        List<LoanResponse> loans = loanService.getLoansByAccount(accountNumber);
        return ResponseEntity.ok(loans);
    }
}
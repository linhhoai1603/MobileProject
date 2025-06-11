package com.mobile.bebankproject.service;
import com.mobile.bebankproject.dto.LoanRequest;
import com.mobile.bebankproject.dto.LoanResponse;

import java.util.List;

public interface LoanService {
    LoanResponse createLoan(LoanRequest request);
    List<LoanResponse> getLoansByAccount(String accountNumber);
}
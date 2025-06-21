package com.mobile.bebankproject.service;

import com.mobile.bebankproject.dto.TransactionResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TransactionService {
  Page<TransactionResponse> getHistory(
    String accountNumber,
    Pageable pageable);
}
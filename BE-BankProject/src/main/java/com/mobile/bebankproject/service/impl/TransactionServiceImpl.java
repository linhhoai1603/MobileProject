package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.dto.TransactionResponse;
import com.mobile.bebankproject.model.TransactionLoan;
import com.mobile.bebankproject.repository.TransactionRepository;
import com.mobile.bebankproject.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.mobile.bebankproject.model.Transaction;
import com.mobile.bebankproject.model.TransactionLoan;


@Service
public class TransactionServiceImpl implements TransactionService {
  @Autowired
  private TransactionRepository txRepo;


  @Override
  public Page<TransactionResponse> getHistory(
      String accountNumber, Pageable pg) {
    return txRepo
      .findByAccount_AccountNumberOrderByTransactionDateDesc(
        accountNumber, pg)
      .map(tx -> {
        TransactionResponse dto = new TransactionResponse();
        dto.setId(tx.getId());
        dto.setAccountNumber(tx.getAccount().getAccountNumber());
        dto.setAmount(tx.getAmount());
        dto.setStatus(tx.getStatus().name());
        dto.setDescription(tx.getDescription());
        dto.setTransactionDate(tx.getTransactionDate());
        if (tx instanceof TransactionLoan) {
          dto.setLoanId(((TransactionLoan) tx).getLoan().getId().intValue());
        }
        return dto;
      });
  }
}
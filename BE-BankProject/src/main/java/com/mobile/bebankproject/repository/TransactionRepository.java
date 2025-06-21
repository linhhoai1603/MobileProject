package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository
  extends JpaRepository<Transaction, Integer> {
  
  // page through transactions for an account, newest first
  Page<Transaction> findByAccount_AccountNumberOrderByTransactionDateDesc(
      String accountNumber, Pageable pageable);
}
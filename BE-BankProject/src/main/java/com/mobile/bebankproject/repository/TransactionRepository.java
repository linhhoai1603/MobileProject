package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.Transaction;
import com.mobile.bebankproject.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByAccount(Account account);
    List<Transaction> findByAccountAndTransactionDateBetween(Account account, LocalDateTime startDate, LocalDateTime endDate);
    List<Transaction> findByTransactionDateBetween(LocalDateTime startDate, LocalDateTime endDate);
} 
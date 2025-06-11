package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.TransactionLoan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionLoanRepository extends JpaRepository<TransactionLoan, Integer> {
}
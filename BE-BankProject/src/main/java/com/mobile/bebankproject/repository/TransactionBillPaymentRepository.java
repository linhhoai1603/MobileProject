package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.TransactionBillPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionBillPaymentRepository extends JpaRepository<TransactionBillPayment, Integer> {
} 
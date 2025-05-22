package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.PendingFundTransfer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PendingFundTransferRepository extends JpaRepository<PendingFundTransfer, Integer> {
    Optional<PendingFundTransfer> findByFromAccountNumberAndToAccountNumberAndAmountAndOtp(String fromAccountNumber, String toAccountNumber, double amount, String otp);
} 
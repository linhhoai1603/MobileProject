package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.TransactionSaving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionSavingRepository extends JpaRepository<TransactionSaving, Integer> {
    List<TransactionSaving> findByMonth(int month);
    List<TransactionSaving> findByYear(int year);
    List<TransactionSaving> findByInterestRate(double interestRate);
    List<TransactionSaving> findByMonthAndYear(int month, int year);
} 
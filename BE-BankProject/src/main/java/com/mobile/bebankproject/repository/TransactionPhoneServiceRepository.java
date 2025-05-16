package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.TransactionPhoneService;
import com.mobile.bebankproject.model.TelcoProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionPhoneServiceRepository extends JpaRepository<TransactionPhoneService, Integer> {
    List<TransactionPhoneService> findByPhoneNumber(String phoneNumber);
    List<TransactionPhoneService> findByTelcoProvider(TelcoProvider telcoProvider);
    List<TransactionPhoneService> findByPhoneNumberAndTelcoProvider(String phoneNumber, TelcoProvider telcoProvider);
} 
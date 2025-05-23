package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.TransactionSaving;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionSavingRepository extends JpaRepository<TransactionSaving, Integer> {

} 
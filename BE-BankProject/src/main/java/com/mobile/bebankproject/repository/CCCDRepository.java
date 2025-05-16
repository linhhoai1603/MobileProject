package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.CCCD;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CCCDRepository extends JpaRepository<CCCD, Integer> {
} 
package com.mobile.bebankproject.repository;

import com.mobile.bebankproject.model.Account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
    Optional<Account> findByPhone(String phone);
    Optional<Account> findByUser_Email(String email);
    Optional<Account> findByOTP(String otp);
    Optional<Account> findByAccountNumber(String accountNumber);
    @Query("SELECT a FROM Account a WHERE a.user.email = :email")
    Optional<Account> findByUserEmail(@Param("email") String email);

}
package com.mobile.fe_bankproject.dto;

import java.io.Serializable;
import lombok.Data;

@Data
public class AccountResponse implements Serializable {
    private int id;
    private String phone;
    private String accountNumber;
    private String accountName;
    private double balance;
    private String accountStatus;
    private UserResponse user;

    @Data
    public static class UserResponse implements Serializable {
        private int id;
        private String fullName;
        private String email;
        private String gender;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getBalance() {
        return balance;
    }

    public UserResponse getUser() {
        return user;
    }

    public String getPhone() {
        return phone;
    }
}
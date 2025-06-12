package com.mobile.fe_bankproject.dto;

import java.io.Serializable;

public class AccountResponse implements Serializable {
    private int id;
    private String phone;
    private String accountNumber;
    private String accountName;
    private double balance;
    private String accountStatus;
    private UserResponse userResponse;

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public double getBalance() {
        return balance;
    }

    public UserResponse getUserResponse() {
        return userResponse;
    }

    public String getPhone() {
        return phone;
    }
}
package com.mobile.fe_bankproject.dto;

import java.io.Serializable;

public class AccountResponse implements Serializable {
    private int id;
    private String phone;
    private String accountNumber;
    private String accountName;
    private double balance;
    private String accountStatus;
    private UserResponse user;


    public static class UserResponse implements Serializable {
        private int id;
        private String fullName;
        private String email;
        private String gender;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
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
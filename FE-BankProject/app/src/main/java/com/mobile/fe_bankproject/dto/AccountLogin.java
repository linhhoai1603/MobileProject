package com.mobile.fe_bankproject.dto;

public class AccountLogin {
    private String phone;
    private String password;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public AccountLogin(String phone, String password) {
        this.phone = phone;
        this.password = password;
    }
}

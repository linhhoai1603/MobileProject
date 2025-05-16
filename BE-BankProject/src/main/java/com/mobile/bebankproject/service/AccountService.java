package com.mobile.bebankproject.service;

import com.mobile.bebankproject.model.Account;

public interface AccountService {
    void logout();
    void autoLogout();
    Account login(String phone, String password);
    boolean confirmAccount(String email);
    void sendOTPToChangePassword(String email);
    boolean checkOTPToChangePassword(String OTP);
    boolean changePassword(String pass1, String pass2);
} 
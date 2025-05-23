package com.mobile.bebankproject.service;

import com.mobile.bebankproject.dto.AccountRegister;
import com.mobile.bebankproject.dto.AccountResponse;
import java.util.List;

public interface AccountService {
    void logout();
    void autoLogout();
    AccountResponse login(String phone, String password);
    boolean confirmAccount(String email, String otp);
    void sendOTPToChangePassword(String email);
    boolean checkOTPToChangePassword(String OTP);
    boolean changePassword(String pass1, String pass2);
    AccountResponse createAccount(AccountRegister accountRegister);
    List<AccountResponse> getAllAccounts();
    
    // New methods for password change functionality
    boolean validateAccountAndPassword(String accountNumber, String currentPass);
    boolean changePasswordLogined(String accountNumber, String newPass);

    // New method for closing account
    boolean closeAccount(String accountNumber, String password);
} 
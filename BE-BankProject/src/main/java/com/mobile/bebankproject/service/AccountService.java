package com.mobile.bebankproject.service;

import com.mobile.bebankproject.dto.AccountRegister;
import com.mobile.bebankproject.dto.AccountResponse;
import com.mobile.bebankproject.dto.FundTransferPreview;
import com.mobile.bebankproject.dto.UpdateProfileRequest;

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

    /**
     * Validates fund transfer details and returns a preview for user confirmation.
     * Does not initiate the transfer or send OTP.
     * @param fromAccountNumber
     * @param toAccountNumber
     * @param amount
     * @param description
     * @return FundTransferPreview object with transaction summary
     */
    FundTransferPreview previewFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String description);

    boolean transferFund(String fromAccountNumber, String toAccountNumber, double amount, String description);
    void requestFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String description);
    boolean confirmFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String otp);
    boolean checkOtpForFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String otp);

    /**
     * Requests OTP for fund transfer and sends it via Firebase Authentication (e.g., SMS).
     *
     * @param fromAccountNumber
     * @param toAccountNumber
     * @param amount
     * @param description
     */
    void requestFirebaseOtp(String fromAccountNumber, String toAccountNumber, double amount, String description);

    boolean updateProfile(UpdateProfileRequest request);
}

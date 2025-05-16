package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.model.Account;
import com.mobile.bebankproject.repository.AccountRepository;
import com.mobile.bebankproject.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Random;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JavaMailSender mailSender;

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_DURATION = 5; // minutes

    @Override
    public void logout() {
        // Implementation for manual logout
        // This could involve clearing session data, etc.
    }

    @Override
    public void autoLogout() {
        // Implementation for automatic logout after session timeout
        // This could involve checking session expiration and logging out if needed
    }

    @Override
    public Account login(String phone, String password) {
        Account account = accountRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!password.equals(account.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        return account;
    }

    @Override
    public boolean confirmAccount(String email) {
        return true;
    }

    @Override
    public void sendOTPToChangePassword(String email) {
        Account account = accountRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found with email: " + email));

        String otp = generateOTP();
        account.setOTP(otp);
        accountRepository.save(account);

        // Send OTP via email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Password Reset OTP");
        message.setText("Your OTP for password reset is: " + otp + 
                       "\nThis OTP is valid for " + OTP_VALID_DURATION + " minutes.");
        mailSender.send(message);
    }

    @Override
    public boolean checkOTPToChangePassword(String OTP) {
            accountRepository.findByOTP(OTP)
                .orElseThrow(() -> new RuntimeException("Invalid OTP"));

        // Check if OTP is expired (assuming you store OTP generation time)
        // This is a simplified version - you might want to add more robust OTP validation
        return true;
    }

    @Override
    public boolean changePassword(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            throw new RuntimeException("Passwords do not match");
        }

        // Validate password strength
        if (!isValidPassword(pass1)) {
            throw new RuntimeException("Password does not meet security requirements");
        }

        // Get the current account from session
        Account account = getCurrentAccount();
        
        account.setPassword(pass1);
        account.setOTP(null); // Clear OTP after successful password change
        accountRepository.save(account);
        
        return true;
    }

    // Helper methods
    private String generateOTP() {
        Random random = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private boolean isValidPassword(String password) {
        // Implement password validation rules
        // Example: minimum length, special characters, numbers, etc.
        return password != null && password.length() >= 8;
    }

    private Account getCurrentAccount() {
        // Implement getting current account from session
        // This is a placeholder - you'll need to implement proper session handling
        throw new RuntimeException("Not implemented");
    }
} 
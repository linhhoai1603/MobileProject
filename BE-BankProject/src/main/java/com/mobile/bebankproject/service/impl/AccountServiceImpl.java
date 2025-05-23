package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.model.*;
import com.mobile.bebankproject.repository.*;
import com.mobile.bebankproject.service.AccountService;
import com.mobile.bebankproject.dto.AccountRegister;
import com.mobile.bebankproject.dto.AccountResponse;
import com.mobile.bebankproject.util.PasswordEncoder;
import com.mobile.bebankproject.util.PasswordValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CCCDRepository cccdRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
    public AccountResponse login(String phone, String password) {
        Account account = accountRepository.findByPhone(phone)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        return AccountResponse.fromAccount(account);
    }

    @Override
    public boolean confirmAccount(String email, String otp) {
        Account account = accountRepository.findByUser_Email(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!otp.equals(account.getOTP())) {
            throw new RuntimeException("Invalid OTP");
        }

        // Check if OTP is expired (5 minutes)
        LocalDateTime otpGenerationTime = account.getOpeningDate();
        if (LocalDateTime.now().isAfter(otpGenerationTime.plusMinutes(OTP_VALID_DURATION))) {
            throw new RuntimeException("OTP has expired");
        }

        // Activate account
        account.setAccountStatus(Account.Status.ACTIVE);
        account.setOTP(null); // Clear OTP after successful confirmation
        accountRepository.save(account);

        // Send welcome email
        sendWelcomeEmail(account);

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
        return true;
    }

    @Override
    public boolean changePassword(String pass1, String pass2) {
        if (!pass1.equals(pass2)) {
            throw new RuntimeException("Passwords do not match");
        }

        if (!PasswordValidator.isValidPassword(pass1)) {
            throw new RuntimeException("Password does not meet security requirements");
        }

        Account account = getCurrentAccount();
        account.setPassword(passwordEncoder.encode(pass1));
        account.setOTP(null);
        accountRepository.save(account);
        
        return true;
    }

    @Override
    @Transactional
    public AccountResponse createAccount(AccountRegister accountRegister) {
        if (accountRepository.findByPhone(accountRegister.getPhone()).isPresent()) {
            throw new RuntimeException("Phone number already registered");
        }
        if (accountRepository.findByUser_Email(accountRegister.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        if (!accountRegister.getPassword1().equals(accountRegister.getPassword2())) {
            throw new RuntimeException("Passwords do not match");
        }
        if (!PasswordValidator.isValidPassword(accountRegister.getPassword1())) {
            throw new RuntimeException("Password does not meet security requirements");
        }

        Address placeOfOrigin = accountRegister.getPlaceOfOrigin();
        Address placeOfResidence = accountRegister.getPlaceOfResidence();
        placeOfOrigin = addressRepository.save(placeOfOrigin);
        placeOfResidence = addressRepository.save(placeOfResidence);

        CCCD cccd = new CCCD();
        cccd.setNumber(accountRegister.getNumber());
        cccd.setPersonalId(accountRegister.getPersonalId());
        cccd.setIssueDate(accountRegister.getIssueDate());
        cccd.setPlaceOfIssue(accountRegister.getPlaceOfIssue());
        cccd.setPlaceOfOrigin(placeOfOrigin);
        cccd.setPlaceOfResidence(placeOfResidence);
        cccd = cccdRepository.save(cccd);

        User user = new User();
        user.setFullName(accountRegister.getFullName());
        user.setDateOfBirth(accountRegister.getDateOfBirth());
        user.setGender(accountRegister.getGender());
        user.setEmail(accountRegister.getEmail());
        user.setCccd(cccd);
        user = userRepository.save(user);

        Account account = new Account();
        account.setPhone(accountRegister.getPhone());
        account.setPassword(passwordEncoder.encode(accountRegister.getPassword1()));
        account.setAccountNumber(generateAccountNumber());
        account.setAccountName(accountRegister.getFullName());
        account.setOpeningDate(LocalDateTime.now());
        account.setBalance(0.0);
        account.setAccountStatus(Account.Status.PENDING);
        account.setUser(user);

        // Generate and set OTP
        String otp = generateOTP();
        account.setOTP(otp);
        account = accountRepository.save(account);

        // Send confirmation email with OTP
        sendAccountConfirmationEmail(account, otp);

        return AccountResponse.fromAccount(account);
    }

    private void sendAccountConfirmationEmail(Account account, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getUser().getEmail());
        message.setSubject("Account Registration - OTP Confirmation");
        message.setText("Dear " + account.getAccountName() + ",\n\n" +
                "Thank you for registering with our bank.\n\n" +
                "Your account details:\n" +
                "Account Number: " + account.getAccountNumber() + "\n" +
                "Phone: " + account.getPhone() + "\n\n" +
                "To activate your account, please use the following OTP:\n" +
                "OTP: " + otp + "\n\n" +
                "This OTP is valid for " + OTP_VALID_DURATION + " minutes.\n\n" +
                "Best regards,\n" +
                "Bank Team");
        mailSender.send(message);
    }

    private void sendWelcomeEmail(Account account) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getUser().getEmail());
        message.setSubject("Welcome to Our Bank");
        message.setText("Dear " + account.getAccountName() + ",\n\n" +
                "Your account has been successfully activated!\n\n" +
                "You can now login to your account using your phone number and password.\n\n" +
                "Best regards,\n" +
                "Bank Team");
        mailSender.send(message);
    }

    private String generateRandomPassword() {
        // Implement proper password generation logic
        return "TEMP" + new Random().nextInt(10000);
    }

    private String generateAccountNumber() {
        // Implement account number generation logic
        return String.format("%010d", new Random().nextInt(1000000000));
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
        if (password == null || password.length() < 8) {
            return false;
        }
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        boolean hasSpecialChar = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true;
            else if (Character.isLowerCase(c)) hasLowerCase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecialChar = true;
        }

        return hasUpperCase && hasLowerCase && hasDigit && hasSpecialChar;
    }

    private Account getCurrentAccount() {
        // Implement getting current account from session
        // This is a placeholder - you'll need to implement proper session handling
        throw new RuntimeException("Not implemented");
    }

    @Override
    public List<AccountResponse> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        return accounts.stream()
                .map(AccountResponse::fromAccount)
                .collect(Collectors.toList());
    }

    @Override
    public boolean validateAccountAndPassword(String accountNumber, String currentPass) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }
        
        return passwordEncoder.matches(currentPass, account.getPassword());
    }

    @Override
    @Transactional
    public boolean changePasswordLogined(String accountNumber, String newPass) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (!PasswordValidator.isValidPassword(newPass)) {
            throw new RuntimeException("New password does not meet security requirements");
        }

        account.setPassword(passwordEncoder.encode(newPass));
        accountRepository.save(account);
        return true;
    }

    @Override
    @Transactional
    public boolean closeAccount(String accountNumber, String password) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Verify password
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Check if account is active
        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Check if account has balance
        if (account.getBalance() > 0) {
            throw new RuntimeException("Cannot close account with remaining balance");
        }

        // Check if account has any active cards
        if (account.getCards() != null && !account.getCards().isEmpty()) {
            throw new RuntimeException("Please cancel all cards before closing the account");
        }

        // Check if account has any pending transactions
        if (account.getListTransactions() != null && 
            account.getListTransactions().stream().anyMatch(t -> t.getStatus() == TransactionStatus.PENDING)) {
            throw new RuntimeException("Please wait for all pending transactions to complete");
        }

        // Close the account
        account.setAccountStatus(Account.Status.CLOSED);
        accountRepository.save(account);

        // Send notification email
        sendAccountClosureEmail(account);

        return true;
    }

    private void sendAccountClosureEmail(Account account) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getUser().getEmail());
        message.setSubject("Account Closure Confirmation");
        message.setText("Dear " + account.getAccountName() + ",\n\n" +
                "Your account has been successfully closed.\n\n" +
                "Account Details:\n" +
                "Account Number: " + account.getAccountNumber() + "\n" +
                "Closure Date: " + LocalDateTime.now() + "\n\n" +
                "Thank you for being our customer.\n\n" +
                "Best regards,\n" +
                "Bank Team");
        mailSender.send(message);
    }
} 
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
import com.mobile.bebankproject.dto.FundTransferPreview;
import com.mobile.bebankproject.dto.UpdateProfileRequest;

import java.time.LocalDateTime;
import java.util.Optional;
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

    @Autowired
    private TransactionFundTransferRepository transactionFundTransferRepository;

    @Autowired
    private PendingFundTransferRepository pendingFundTransferRepository;

    private static final int OTP_LENGTH = 6;
    private static final long OTP_VALID_DURATION = 5; // minutes

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
    public boolean updateAvatar(String accountNumber, String urlAvatar) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.getUser().setUrlAvatar(urlAvatar);
        accountRepository.save(account);
        return  true;
    }

    @Override
    public boolean updateBackground(String accountNumber, String urlBackground) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.getUser().setUrlBackground(urlBackground);
        accountRepository.save(account);
        return true;
    }

    @Override
    public Account getAccountByAccountNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .orElse(null);
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

    @Override
    public AccountResponse getAccount(String accountNumber) {
        Optional<Account> account = accountRepository.findByAccountNumber(accountNumber);
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(account.get().getId());
        accountResponse.setAccountNumber(account.get().getAccountNumber());
        accountResponse.setAccountName(account.get().getAccountName());
        accountResponse.setPhone(account.get().getPhone());
        accountResponse.setBalance(account.get().getBalance());
        accountResponse.setAccountStatus(account.get().getAccountStatus());
        return accountResponse;
    }

    @Override
    public FundTransferPreview previewFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        if (fromAccountNumber == null || fromAccountNumber.trim().isEmpty()) {
            throw new RuntimeException("Sender account number cannot be empty");
        }
        if (toAccountNumber == null || toAccountNumber.trim().isEmpty()) {
            throw new RuntimeException("Receiver account number cannot be empty");
        }
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        if (amount <= 0) {
            throw new RuntimeException("Transfer amount must be greater than 0");
        }

        // Kiểm tra tài khoản tồn tại và trạng thái
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        if (fromAccount.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Sender account is not active");
        }
        if (toAccount.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Receiver account is not active");
        }

        // Kiểm tra số dư
        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        // Tạo đối tượng preview để trả về
        FundTransferPreview preview = new FundTransferPreview();
        preview.setFromAccountNumber(fromAccountNumber);
        preview.setFromAccountName(fromAccount.getAccountName()); // Lấy tên tài khoản
        preview.setToAccountNumber(toAccountNumber);
        preview.setToAccountName(toAccount.getAccountName());   // Lấy tên tài khoản
        preview.setAmount(amount);
        preview.setDescription(description);
        // Có thể tính và set phí giao dịch vào đây nếu có

        return preview;
    }

    @Transactional
    public boolean transferFund(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        if (fromAccountNumber.equals(toAccountNumber)) {
            throw new RuntimeException("Cannot transfer to the same account");
        }
        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));
        if (fromAccount.getAccountStatus() != Account.Status.ACTIVE || toAccount.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("One or both accounts are not active");
        }
        if (fromAccount.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }
        fromAccount.setBalance(fromAccount.getBalance() - amount);
        toAccount.setBalance(toAccount.getBalance() + amount);
        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);
        TransactionFundTransfer transaction = new TransactionFundTransfer();
        transaction.setFromAccount(fromAccountNumber);
        transaction.setToAccount(toAccount);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAccount(fromAccount);
        transactionFundTransferRepository.save(transaction);
        return true;
    }

    @Override
    public void requestFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        // Phương thức này giờ sẽ được gọi SAU KHI user xác nhận giao dịch trên màn hình preview.
        // Cần thực hiện lại một số kiểm tra cơ bản ở đây hoặc đảm bảo rằng frontend gửi lại
        // thông tin đã được validate từ bước preview.

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));
        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        // Sinh OTP
        String otp = generateOTP();
        // Lưu PendingFundTransfer
        PendingFundTransfer pending = new PendingFundTransfer();
        pending.setFromAccountNumber(fromAccountNumber);
        pending.setToAccountNumber(toAccountNumber);
        pending.setAmount(amount);
        pending.setDescription(description);
        pending.setOtp(otp);
        pending.setCreatedAt(LocalDateTime.now());
        pendingFundTransferRepository.save(pending);
        // Gửi OTP về email người chuyển
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(fromAccount.getUser().getEmail());
        message.setSubject("OTP xác nhận chuyển khoản");
        message.setText("Mã OTP của bạn là: " + otp + "\\nSử dụng để xác nhận giao dịch chuyển khoản.");
        mailSender.send(message);
    }

    @Override
    @Transactional
    public boolean confirmFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String otp) {
        PendingFundTransfer pending = pendingFundTransferRepository.findByFromAccountNumberAndToAccountNumberAndAmountAndOtp(
                fromAccountNumber, toAccountNumber, amount, otp
        ).orElseThrow(() -> new RuntimeException("Invalid OTP or transfer info"));
        // Thực hiện chuyển khoản
        boolean result = transferFund(fromAccountNumber, toAccountNumber, amount, pending.getDescription());
        // Xóa pending
        pendingFundTransferRepository.delete(pending);
        return result;
    }

    @Override
    public boolean checkOtpForFundTransfer(String fromAccountNumber, String toAccountNumber, double amount, String otp) {
        Optional<PendingFundTransfer> pendingOpt = pendingFundTransferRepository
                .findByFromAccountNumberAndToAccountNumberAndAmountAndOtp(fromAccountNumber, toAccountNumber, amount, otp);
        if (pendingOpt.isEmpty()) return false;
        PendingFundTransfer pending = pendingOpt.get();
        // Kiểm tra hết hạn OTP (5 phút)
        if (pending.getCreatedAt().plusMinutes(OTP_VALID_DURATION).isBefore(LocalDateTime.now())) {
            return false;
        }
        return true;
    }

    @Override
    public void requestFirebaseOtp(String fromAccountNumber, String toAccountNumber, double amount, String description) {
        // Phương thức này xử lý gửi OTP qua Firebase (SMS)

        Account fromAccount = accountRepository.findByAccountNumber(fromAccountNumber)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        // Sinh OTP
        String otp = generateOTP();

        // Lưu PendingFundTransfer
        PendingFundTransfer pending = new PendingFundTransfer();
        pending.setFromAccountNumber(fromAccountNumber);
        pending.setToAccountNumber(toAccountNumber);
        pending.setAmount(amount);
        pending.setDescription(description);
        pending.setOtp(otp);
        pending.setCreatedAt(LocalDateTime.now());
        pendingFundTransferRepository.save(pending);

        // TODO: Implement Firebase SMS OTP sending
        // For now, just send via email
        sendTransferOtpEmail(fromAccount, otp, previewFundTransfer(fromAccountNumber, toAccountNumber, amount, description));
    }

    private void sendTransferOtpEmail(Account account, String otp, FundTransferPreview preview) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getUser().getEmail());
        message.setSubject("Fund Transfer OTP");
        message.setText("Dear " + account.getAccountName() + ",\n\n" +
                "Your OTP for the fund transfer is: " + otp + "\n\n" +
                "Transfer Details:\n" +
                "From: " + preview.getFromAccountNumber() + " (" + preview.getFromAccountName() + ")\n" +
                "To: " + preview.getToAccountNumber() + " (" + preview.getToAccountName() + ")\n" +
                "Amount: " + preview.getAmount() + "\n" +
                "Description: " + preview.getDescription() + "\n\n" +
                "This OTP is valid for " + OTP_VALID_DURATION + " minutes.\n\n" +
                "Best regards,\n" +
                "Bank Team");
        mailSender.send(message);
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

    @Override
    @Transactional
    public boolean updateProfile(UpdateProfileRequest request) {
        // Find account by account number
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Verify PIN
        if (!account.getPIN().equals(request.getPin())) {
            throw new RuntimeException("Invalid PIN");
        }

        // Check if email is already taken by another account
        if (!account.getUser().getEmail().equals(request.getEmail())) {
            if (accountRepository.findByUser_Email(request.getEmail()).isPresent()) {
                throw new RuntimeException("Email already registered");
            }
        }

        // Check if phone is already taken by another account
        if (!account.getPhone().equals(request.getPhone())) {
            if (accountRepository.findByPhone(request.getPhone()).isPresent()) {
                throw new RuntimeException("Phone number already registered");
            }
        }

        // Update user information
        User user = account.getUser();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        userRepository.save(user);

        // Update account information
        account.setPhone(request.getPhone());
        account.setAccountName(request.getFullName());
        accountRepository.save(account);

        return true;
    }

    @Override
    public String findAccountNameByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getAccountName)
                .orElse(null);
    }
} 
package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.model.*;
import com.mobile.bebankproject.repository.*;
import com.mobile.bebankproject.service.RechargesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;

@Service
public class RechargesServiceImpl implements RechargesService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionPhoneServiceRepository transactionPhoneServiceRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public boolean purchaseRecharge(String accountNumber, String pin, String phoneNumber, TelcoProvider telcoProvider, double amount) {
        // Validate account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Validate PIN
        if (!account.getPIN().equals(pin)) {
            throw new RuntimeException("Invalid PIN");
        }

        // Validate amount
        if (amount <= 0) {
            throw new RuntimeException("Invalid recharge amount");
        }

        // Check balance
        if (account.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        // Create recharge transaction
        RechargeService transaction = new RechargeService();
        transaction.setPhoneNumber(phoneNumber);
        transaction.setTelcoProvider(telcoProvider);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(amount);
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAccount(account);

        // Update account balance
        account.setBalance(account.getBalance() - amount);
        accountRepository.save(account);

        // Save transaction
        transactionPhoneServiceRepository.save(transaction);

        // Send confirmation email
        sendRechargeConfirmationEmail(account, phoneNumber, amount, telcoProvider);

        return true;
    }

    private void sendRechargeConfirmationEmail(Account account, String phoneNumber, double amount, TelcoProvider telcoProvider) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getUser().getEmail());
        message.setSubject("Phone Recharge Confirmation");
        message.setText("Dear " + account.getAccountName() + ",\n\n" +
                "Your phone recharge has been confirmed.\n\n" +
                "Recharge Details:\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Provider: " + telcoProvider + "\n" +
                "Amount: " + amount + "\n" +
                "Date: " + LocalDateTime.now() + "\n\n" +
                "Thank you for using our service.\n\n" +
                "Best regards,\n" +
                "Perfect Bank");
        mailSender.send(message);
    }
} 
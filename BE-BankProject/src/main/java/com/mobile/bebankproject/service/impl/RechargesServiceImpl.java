package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.dto.RechargePreviewDTO;
import com.mobile.bebankproject.model.*;
import com.mobile.bebankproject.repository.*;
import com.mobile.bebankproject.service.PhoneCardService;
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
    private PhoneCardService phoneCardService;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public boolean purchaseRecharge(String accountNumber, String pin, String phoneNumber, int idPhoneCard) {
        // Validate account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // get phone card
        PhoneCard phoneCard = phoneCardService.getPhoneCardServiceById(idPhoneCard);
        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Validate PIN
        if (!account.getPIN().equals(pin)) {
            throw new RuntimeException("Invalid PIN");
        }

        // Check balance
        if (account.getBalance() < phoneCard.getAmount()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Create recharge transaction
        RechargeService transaction = new RechargeService();
        transaction.setPhoneNumber(phoneNumber);
        transaction.setTelcoProvider(phoneCard.getTelcoProvider());
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(phoneCard.getAmount());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAccount(account);

        // Update account balance
        account.setBalance(account.getBalance() - phoneCard.getAmount());
        accountRepository.save(account);

        // Save transaction
        transactionPhoneServiceRepository.save(transaction);

        // trừ số lượng
        phoneCardService.purchasePhoneCard(phoneCard.getId());

        // Send confirmation email
//        sendRechargeConfirmationEmail(account, phoneNumber, phoneCard.getAmount(), phoneCard.getTelcoProvider());

        return true;
    }


//    private void sendRechargeConfirmationEmail(Account account, String phoneNumber, double amount, TelcoProvider telcoProvider) {
//        SimpleMailMessage message = new SimpleMailMessage();
//        message.setTo(account.getUser().getEmail());
//        message.setSubject("Phone Recharge Confirmation");
//        message.setText("Dear " + account.getAccountName() + ",\n\n" +
//                "Your phone recharge has been confirmed.\n\n" +
//                "Recharge Details:\n" +
//                "Phone Number: " + phoneNumber + "\n" +
//                "Provider: " + telcoProvider + "\n" +
//                "Amount: " + amount + "\n" +
//                "Date: " + LocalDateTime.now() + "\n\n" +
//                "Thank you for using our service.\n\n" +
//                "Best regards,\n" +
//                "Perfect Bank");
//        mailSender.send(message);
//    }
    @Override
    public RechargePreviewDTO previewRecharge(String accountNumber, String phoneNumber, int idPhoneCard) {
        // Validate account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // get phone card
        PhoneCard phoneCard = phoneCardService.getPhoneCardServiceById(idPhoneCard);

        // Prepare preview DTO
        RechargePreviewDTO preview = new RechargePreviewDTO();
        preview.setPhoneNumber(phoneNumber);
        preview.setAmount(phoneCard.getAmount());
        preview.setTelcoProvider(phoneCard.getTelcoProvider());
        preview.setBalanceBefore(account.getBalance());
        preview.setBalanceAfter(account.getBalance() - phoneCard.getAmount());

        return preview;
    }

} 
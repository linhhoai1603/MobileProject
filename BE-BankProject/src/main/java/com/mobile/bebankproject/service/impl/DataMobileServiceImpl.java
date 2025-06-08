package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.model.*;
import com.mobile.bebankproject.repository.*;
import com.mobile.bebankproject.service.DataMobileService;
import com.mobile.bebankproject.dto.DataPackagePreview;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DataMobileServiceImpl implements DataMobileService {

    @Autowired
    private DataMobileRepository dataMobileRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionPhoneServiceRepository transactionPhoneServiceRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    @Transactional
    public boolean purchaseDataPackage(String accountNumber, String phoneNumber, int packageId) {
        // Validate account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Get data package
        DataMobile dataPackage = dataMobileRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Data package not found"));

        if (dataPackage.getInStock() <= 0) {
            throw new RuntimeException("Data package is out of stock");
        }

        // Check balance
        if (account.getBalance() < dataPackage.getPrice()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Create transaction
        com.mobile.bebankproject.model.DataMobileService transaction = new com.mobile.bebankproject.model.DataMobileService();
        transaction.setPhoneNumber(phoneNumber);
        transaction.setTelcoProvider(dataPackage.getTelcoProvider());
        transaction.setDataMobile(dataPackage);
        transaction.setTransactionDate(LocalDateTime.now());
        transaction.setAmount(dataPackage.getPrice());
        transaction.setStatus(TransactionStatus.SUCCESS);
        transaction.setAccount(account);

        // Update account balance
        account.setBalance(account.getBalance() - dataPackage.getPrice());
        accountRepository.save(account);

        // Update package stock
        dataPackage.setInStock(dataPackage.getInStock() - 1);
        dataMobileRepository.save(dataPackage);

        // Save transaction
        transactionPhoneServiceRepository.save(transaction);

        // Send confirmation email
//        sendPurchaseConfirmationEmail(account, dataPackage, phoneNumber);

        return true;
    }

    @Override
    public DataPackagePreview previewPurchase(String accountNumber, String phoneNumber, int packageId) {
        // Validate account
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getAccountStatus() != Account.Status.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        // Get data package
        DataMobile dataPackage = dataMobileRepository.findById(packageId)
                .orElseThrow(() -> new RuntimeException("Data package not found"));

        if (dataPackage.getInStock() <= 0) {
            throw new RuntimeException("Data package is out of stock");
        }

        // Check balance
        if (account.getBalance() < dataPackage.getPrice()) {
            throw new RuntimeException("Insufficient balance");
        }

        // Create preview object
        DataPackagePreview preview = new DataPackagePreview();
        preview.setAccountNumber(accountNumber);
        preview.setPhoneNumber(phoneNumber);
        preview.setPackageName(dataPackage.getPackageName());
        preview.setQuantity(dataPackage.getQuantity());
        preview.setValidDate(dataPackage.getValidDate());
        preview.setPrice(dataPackage.getPrice());

        return preview;
    }


    @Override
    public List<DataMobile> getFilteredPackages(TelcoProvider telcoProvider, Integer validDate, Integer quantity) {
        return dataMobileRepository.findAll().stream()
                .filter(pkg -> (telcoProvider == null || pkg.getTelcoProvider() == telcoProvider)
                        && (validDate == null || pkg.getValidDate() == validDate)
                        && (quantity == null || pkg.getQuantity() == quantity)
                        && pkg.getInStock() > 0)
                .toList();
    }

    @Override
    public DataMobile getDataPackageById(int id) {
        return dataMobileRepository.getDataMobileById(id);
    }

    private void sendPurchaseConfirmationEmail(Account account, DataMobile dataPackage, String phoneNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(account.getUser().getEmail());
        message.setSubject("Data Package Purchase Confirmation");
        message.setText("Dear " + account.getAccountName() + ",\n\n" +
                "Your data package purchase has been confirmed.\n\n" +
                "Purchase Details:\n" +
                "Phone Number: " + phoneNumber + "\n" +
                "Package: " + dataPackage.getPackageName() + "\n" +
                "Data: " + dataPackage.getQuantity() + " MB\n" +
                "Validity: " + dataPackage.getValidDate() + " days\n" +
                "Amount: " + dataPackage.getPrice() + "\n\n" +
                "Thank you for using our service.\n\n" +
                "Best regards,\n" +
                "Perfect Bank");
        mailSender.send(message);
    }
}

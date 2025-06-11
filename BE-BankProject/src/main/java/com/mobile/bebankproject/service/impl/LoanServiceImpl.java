package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.dto.LoanRequest;
import com.mobile.bebankproject.dto.LoanResponse;
import com.mobile.bebankproject.model.Account;
import com.mobile.bebankproject.model.Loan;
import com.mobile.bebankproject.model.TransactionLoan;
import com.mobile.bebankproject.model.TransactionStatus;
import com.mobile.bebankproject.repository.AccountRepository;
import com.mobile.bebankproject.repository.LoanRepository;
import com.mobile.bebankproject.repository.TransactionRepository;
import com.mobile.bebankproject.service.LoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {
    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public LoanResponse createLoan(LoanRequest request) {
        // 1. Tạo khoản vay
        Loan loan = new Loan();
        loan.setAccountNumber(request.getAccountNumber());
        loan.setAmount(request.getAmount());
        loan.setTerm(request.getTerm());
        loan.setNote(request.getNote());
        double totalPayable = request.getAmount() * Math.pow(1 + 0.2, request.getTerm());
        loan.setTotalPayable(totalPayable);
        loan.setStatus(Loan.Status.APPROVED); // hoặc PENDING nếu cần duyệt
        loan = loanRepository.save(loan);

        // 2. Cộng tiền vào tài khoản
        Account account = accountRepository.findByAccountNumber(request.getAccountNumber())
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance() + request.getAmount());
        accountRepository.save(account);

        // 3. Ghi nhận TransactionLoan
        TransactionLoan transactionLoan = new TransactionLoan();
        transactionLoan.setAccount(account);
        transactionLoan.setAmount(request.getAmount());
        transactionLoan.setStatus(TransactionStatus.SUCCESS);
        transactionLoan.setDescription("Giải ngân khoản vay #" + loan.getId());
        transactionLoan.setLoan(loan);
        transactionLoan.setTransactionDate(java.time.LocalDateTime.now());
        transactionRepository.save(transactionLoan);

        // 4. Trả về response
        LoanResponse response = new LoanResponse();
        response.setId(loan.getId());
        response.setAccountNumber(loan.getAccountNumber());
        response.setAmount(loan.getAmount());
        response.setTerm(loan.getTerm());
        response.setTotalPayable(loan.getTotalPayable());
        response.setNote(loan.getNote());
        response.setStatus(loan.getStatus().name());
        return response;
    }

    @Override
    public List<LoanResponse> getLoansByAccount(String accountNumber) {
        return loanRepository.findByAccountNumber(accountNumber)
                .stream()
                .map(loan -> {
                    LoanResponse res = new LoanResponse();
                    res.setId(loan.getId());
                    res.setAccountNumber(loan.getAccountNumber());
                    res.setAmount(loan.getAmount());
                    res.setTerm(loan.getTerm());
                    res.setTotalPayable(loan.getTotalPayable());
                    res.setNote(loan.getNote());
                    res.setStatus(loan.getStatus().name());
                    return res;
                })
                .collect(Collectors.toList());
    }
}
package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.model.Card;
import com.mobile.bebankproject.model.CardStatus;
import com.mobile.bebankproject.model.Account;
import com.mobile.bebankproject.repository.CardRepository;
import com.mobile.bebankproject.repository.AccountRepository;
import com.mobile.bebankproject.dto.CardResponse;
import com.mobile.bebankproject.exception.CardNotFoundException;
import com.mobile.bebankproject.exception.InvalidPinException;

import com.mobile.bebankproject.service.CardService;
import com.mobile.bebankproject.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Random;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public CardServiceImpl(CardRepository cardRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Card getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
            .orElseThrow(() -> new CardNotFoundException("Thẻ không tồn tại"));
    }

    @Override
    @Transactional
    public void lockCard(String cardNumber, String pin) {
        Card card = getCardByNumber(cardNumber);
        if (!passwordEncoder.matches(pin, card.getPin())) {
            throw new InvalidPinException("Mã PIN không đúng");
        }
        card.setStatus(CardStatus.LOCKED);
        cardRepository.save(card);
    }

    @Override
    @Transactional
    public void unlockCard(String cardNumber, String pin) {
        Card card = getCardByNumber(cardNumber);
        if (!passwordEncoder.matches(pin, card.getPin())) {
            throw new InvalidPinException("Mã PIN không đúng");
        }
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    @Override
    public boolean verifyPin(String cardNumber, String pin) {
        Card card = getCardByNumber(cardNumber);
        return passwordEncoder.matches(pin, card.getPin());
    }

    @Override
    @Transactional
    public boolean changePin(String cardNumber, String oldPin, String newPin) {
        Card card = getCardByNumber(cardNumber);

        // Kiểm tra trạng thái thẻ
        if (card.getStatus() != CardStatus.ACTIVE) {
            return false;  // chỉ cho đổi PIN khi thẻ active
        }

        // Kiểm tra PIN cũ đúng không
        if (!passwordEncoder.matches(oldPin, card.getPin())) {
            return false;
        }

        // Mã hóa PIN mới rồi lưu
        String encodedNewPin = passwordEncoder.encode(newPin);
        card.setPin(encodedNewPin);

        cardRepository.save(card);
        return true;
    }

    @Override
    @Transactional
    public CardResponse createNewCard(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
            .orElseThrow(() -> new IllegalStateException("Tài khoản không tồn tại"));

        // Kiểm tra xem tài khoản đã có thẻ active chưa
        boolean hasActiveCard = account.getCards().stream()
            .anyMatch(card -> card.getStatus() == CardStatus.ACTIVE);

        if (hasActiveCard) {
            throw new IllegalStateException("Tài khoản đã có thẻ được kích hoạt");
        }

        // Tạo thẻ mới
        Card newCard = new Card();
        newCard.setAccount(account);
        newCard.setCardHolder(account.getAccountName());
        newCard.setStatus(CardStatus.ACTIVE);
        
        // Tạo số thẻ ngẫu nhiên 16 số
        String cardNumber = generateUniqueCardNumber();
        newCard.setCardNumber(cardNumber);

        // Tạo PIN mặc định (6 số) và mã hóa
        String defaultPin = generateDefaultPin();
        newCard.setPin(passwordEncoder.encode(defaultPin));

        Card savedCard = cardRepository.save(newCard);
        
        // Trả về CardResponse kèm theo PIN mặc định chưa mã hóa
        CardResponse response = CardResponse.fromCard(savedCard);
        response.setDefaultPin(defaultPin); // Thêm trường này vào CardResponse
        
        return response;
    }


    private String generateUniqueCardNumber() {
        Random random = new Random();
        String cardNumber;
        do {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 16; i++) {
                sb.append(random.nextInt(10));
            }
            cardNumber = sb.toString();
        } while (cardRepository.findByCardNumber(cardNumber).isPresent());
        
        return cardNumber;
    }

    private String generateDefaultPin() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}

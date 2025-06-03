package com.mobile.bebankproject.service;

import com.mobile.bebankproject.model.Card;
import com.mobile.bebankproject.model.CardStatus;
import com.mobile.bebankproject.repository.CardRepository;
import com.mobile.bebankproject.exception.CardNotFoundException;
import com.mobile.bebankproject.exception.InvalidPinException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final PasswordEncoder passwordEncoder;

    public CardService(CardRepository cardRepository, PasswordEncoder passwordEncoder) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Card getCardByNumber(String cardNumber) {
        return cardRepository.findByCardNumber(cardNumber)
            .orElseThrow(() -> new CardNotFoundException("Thẻ không tồn tại"));
    }

    @Transactional
    public void lockCard(String cardNumber, String pin) {
        Card card = getCardByNumber(cardNumber);
        if (!passwordEncoder.matches(pin, card.getPin())) {
            throw new InvalidPinException("Mã PIN không đúng");
        }
        card.setStatus(CardStatus.LOCKED);
        cardRepository.save(card);
    }

    @Transactional
    public void unlockCard(String cardNumber, String pin) {
        Card card = getCardByNumber(cardNumber);
        if (!passwordEncoder.matches(pin, card.getPin())) {
            throw new InvalidPinException("Mã PIN không đúng");
        }
        card.setStatus(CardStatus.ACTIVE);
        cardRepository.save(card);
    }

    public boolean verifyPin(String cardNumber, String pin) {
        Card card = getCardByNumber(cardNumber);
        return passwordEncoder.matches(pin, card.getPin());
    }

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
}

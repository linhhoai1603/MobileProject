package com.mobile.bebankproject.service;

import com.mobile.bebankproject.model.Card;
import com.mobile.bebankproject.model.CardStatus;
import com.mobile.bebankproject.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;  // hoặc dùng mã hóa khác
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardService {

    private final CardRepository cardRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Tìm thẻ theo số thẻ
    public Optional<Card> findByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    // Kiểm tra PIN có đúng không (so sánh với mã hóa)
    public boolean checkPIN(Card card, String rawPIN) {
        return passwordEncoder.matches(rawPIN, card.getPIN());
    }

    // Đổi PIN (mã hóa PIN mới rồi lưu)
    public boolean changePIN(String cardNumber, String oldPIN, String newPIN) {
        Optional<Card> optCard = cardRepository.findByNumber(cardNumber);
        if (optCard.isEmpty()) return false;

        Card card = optCard.get();

        // Kiểm tra trạng thái thẻ
        if (card.getCardStatus() != CardStatus.ACTIVE) {
            return false;  // chỉ cho đổi PIN khi thẻ active
        }

        // Kiểm tra PIN cũ đúng không
        if (!checkPIN(card, oldPIN)) {
            return false;
        }

        // Mã hóa PIN mới rồi lưu
        String encodedNewPIN = passwordEncoder.encode(newPIN);
        card.setPIN(encodedNewPIN);

        cardRepository.save(card);
        return true;
    }
}

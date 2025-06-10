package com.mobile.bebankproject.service.impl;

import com.mobile.bebankproject.model.PhoneCard;
import com.mobile.bebankproject.model.TelcoProvider;
import com.mobile.bebankproject.repository.PhoneCardRepository;
import com.mobile.bebankproject.service.PhoneCardService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PhoneCardImpl implements PhoneCardService {
    @Autowired
    private PhoneCardRepository phoneCardRepository;

    // method get Phone card by ID
    @Override
    public PhoneCard getPhoneCardServiceById(int id) {
        return phoneCardRepository.findById(id).orElse(null);
    }

    @Override
    public List<PhoneCard> getAllPhoneCards() {
        return phoneCardRepository.findAll();
    }

    @Override
    @Transactional
    public boolean purchasePhoneCard(int id) {
        try {
            PhoneCard phoneCard = phoneCardRepository.getReferenceById(id);

            // Kiểm tra số lượng
            if (phoneCard.getQuantity() <= 0) {
                throw new IllegalStateException("Không đủ thẻ để bán. Số lượng còn lại: " + phoneCard.getQuantity());
            }

            // Giảm số lượng và lưu lại
            phoneCard.setQuantity(phoneCard.getQuantity() - 1);
            phoneCardRepository.save(phoneCard);

            return true;

        } catch (EntityNotFoundException ex) {
            // Xử lý khi không tìm thấy PhoneCard
            throw new EntityNotFoundException("Không tìm thấy thẻ điện thoại với ID: " + id);
        }
    }

    @Override
    public List<PhoneCard> getPhoneCardsByTelcoProvider(TelcoProvider telcoProvider) {
        return phoneCardRepository.findByTelcoProvider(telcoProvider);
    }
}

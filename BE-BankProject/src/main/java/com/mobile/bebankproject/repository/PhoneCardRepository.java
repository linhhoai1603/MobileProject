package com.mobile.bebankproject.repository;


import com.mobile.bebankproject.model.PhoneCard;
import com.mobile.bebankproject.model.TelcoProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhoneCardRepository extends JpaRepository<PhoneCard, Integer> {

    List<PhoneCard> findByTelcoProvider(TelcoProvider telcoProvider);
}

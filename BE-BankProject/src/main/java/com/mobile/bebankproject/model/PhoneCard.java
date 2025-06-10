package com.mobile.bebankproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;


@Entity
@Getter
@Setter
public class PhoneCard {
    @Id
    private int id;
    @Enumerated(EnumType.STRING)
    private TelcoProvider telcoProvider;
    private int amount;
    private int quantity;
}

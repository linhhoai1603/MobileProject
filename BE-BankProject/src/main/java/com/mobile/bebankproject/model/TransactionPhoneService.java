package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class TransactionPhoneService extends Transaction {
    private String phoneNumber;
    @Enumerated(EnumType.STRING)
    private TelcoProvider telcoProvider;
}

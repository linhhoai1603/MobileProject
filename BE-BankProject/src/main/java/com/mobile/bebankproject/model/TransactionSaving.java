package com.mobile.bebankproject.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TransactionSaving extends Transaction {
    private int month;
    private int year;
    private double interestRate;
}

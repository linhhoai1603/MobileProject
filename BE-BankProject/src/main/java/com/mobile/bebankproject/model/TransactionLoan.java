package com.mobile.bebankproject.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TransactionLoan extends Transaction {
    @OneToOne
    private Loan loan;
}
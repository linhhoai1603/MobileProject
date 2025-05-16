package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "transactions")
@Getter
@Setter
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    String fromAccount;
    LocalDateTime transactionDate;
    String description;
    double amount;
    @Enumerated(EnumType.STRING)
    TransactionStatus status;
    @ManyToOne @JoinColumn(name = "account_id")
    Account account;

}

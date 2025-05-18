package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@Setter
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String phone;
    private String password;
    private String accountNumber;
    private String accountName;

    private LocalDateTime openingDate;
    private double balance;

    @Enumerated(EnumType.STRING)
    @Column(name = "account_status", length = 20)
    private Status accountStatus;

    private String PIN;
    private String OTP;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transaction> listTransactions;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Card> cards;

    public enum Status {
        ACTIVE,
        INACTIVE,
        BLOCKED,
        PENDING,
        LOCKED,
        CLOSED
    }

    // Getters and Setters
}


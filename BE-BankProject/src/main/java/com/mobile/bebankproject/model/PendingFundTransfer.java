package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class PendingFundTransfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private double amount;
    private String description;
    private String otp;
    private LocalDateTime createdAt;
} 
package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "cccd")
@Getter
@Setter
public class CCCD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String number;
    String personalId;
    LocalDate issueDate;
    String placeOfIssue;
    @OneToOne(fetch = FetchType.EAGER)
    Address placeOfOrigin;
    @OneToOne(fetch = FetchType.EAGER)
    Address placeOfResidence;
}

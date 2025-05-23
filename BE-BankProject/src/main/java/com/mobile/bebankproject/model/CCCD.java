package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "cccd")
@Getter
@Setter
public class CCCD {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String number;
    private String personalId;
    private Date issueDate;
    private String placeOfIssue;
    @OneToOne(fetch = FetchType.EAGER)
    private Address placeOfOrigin;
    @OneToOne(fetch = FetchType.EAGER)
    private Address placeOfResidence;
}

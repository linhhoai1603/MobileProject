package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String fullName;
    private LocalDate dateOfBirth;
    @OneToOne(cascade = CascadeType.ALL)
    private CCCD cccd;
    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String email;
    private String urlAvatar;
    private String urlBackground;
}

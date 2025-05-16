package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Card {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String number;
    String type;
    CardStatus cardStatus;
    String PIN;
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;;


}

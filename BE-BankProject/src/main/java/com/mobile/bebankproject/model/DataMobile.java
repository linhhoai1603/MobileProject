package com.mobile.bebankproject.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DataMobile {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String packageName;
    int quantity; // GB
    int validDate; // days
    double price;
    int inStock;
    @Enumerated(EnumType.STRING)
    TelcoProvider telcoProvider;
}

package com.mobile.bebankproject.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class DataMobileService extends TransactionPhoneService {
    @OneToOne(cascade = CascadeType.ALL)
    private DataMobile dataMobile;
}

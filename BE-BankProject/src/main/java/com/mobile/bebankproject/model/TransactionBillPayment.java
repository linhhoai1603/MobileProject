package com.mobile.bebankproject.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class TransactionBillPayment extends Transaction {
    private String billCode;
    private BillType billType;
}

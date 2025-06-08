package com.mobile.fe_bankproject.dto;

public class PhoneCardDTO {
    private int id;
    private TelcoProvider telcoProvider;
    private int amount;
    private int quantity;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TelcoProvider getTelcoProvider() {
        return telcoProvider;
    }

    public void setTelcoProvider(TelcoProvider telcoProvider) {
        this.telcoProvider = telcoProvider;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}

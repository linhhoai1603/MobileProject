package com.mobile.fe_bankproject.dto;


public class DataMobile {
    int id;
    String packageName;
    int quantity; // mb
    int validDate; // days
    double price;
    int inStock;
    TelcoProvider telcoProvider;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getValidDate() {
        return validDate;
    }

    public void setValidDate(int validDate) {
        this.validDate = validDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public TelcoProvider getTelcoProvider() {
        return telcoProvider;
    }

    public void setTelcoProvider(TelcoProvider telcoProvider) {
        this.telcoProvider = telcoProvider;
    }
}

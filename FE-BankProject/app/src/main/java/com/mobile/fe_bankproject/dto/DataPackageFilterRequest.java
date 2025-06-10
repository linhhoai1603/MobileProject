package com.mobile.fe_bankproject.dto;


public class DataPackageFilterRequest {
    private TelcoProvider provider;
    private Integer validDate;
    private Integer quantity;

    public TelcoProvider getProvider() {
        return provider;
    }

    public void setProvider(TelcoProvider provider) {
        this.provider = provider;
    }

    public Integer getValidDate() {
        return validDate;
    }

    public void setValidDate(Integer validDate) {
        this.validDate = validDate;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
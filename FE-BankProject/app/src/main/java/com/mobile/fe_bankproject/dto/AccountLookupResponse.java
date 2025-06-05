package com.mobile.fe_bankproject.dto;

import com.google.gson.annotations.SerializedName;

public class AccountLookupResponse {
    @SerializedName("accountName") // Tên trường JSON trả về từ backend
    private String accountName;

    // Constructor
    public AccountLookupResponse(String accountName) {
        this.accountName = accountName;
    }

    // Getters và setters
    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    // Thêm constructor mặc định nếu cần cho Retrofit/Gson
    public AccountLookupResponse() {
    }
} 
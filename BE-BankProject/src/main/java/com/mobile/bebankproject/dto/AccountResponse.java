package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.Account;
import lombok.Data;

@Data
public class AccountResponse {
    private int id;
    private String phone;
    private String accountNumber;
    private String accountName;
    private double balance;
    private Account.Status accountStatus;
    private UserResponse userResponse;

    @Data
    public static class UserResponse {
        private int id;
        private String fullName;
        private String email;
        private String gender;
        private String urlAvatar;
        private String urlBackground;
    }

    public static AccountResponse fromAccount(Account account) {
        AccountResponse response = new AccountResponse();
        response.setId(account.getId());
        response.setPhone(account.getPhone());
        response.setAccountNumber(account.getAccountNumber());
        response.setAccountName(account.getAccountName());
        response.setBalance(account.getBalance());
        response.setAccountStatus(account.getAccountStatus());

        if (account.getUser() != null) {
            UserResponse userResponse = new UserResponse();
            userResponse.setId(account.getUser().getId());
            userResponse.setFullName(account.getUser().getFullName());
            userResponse.setEmail(account.getUser().getEmail());
            userResponse.setGender(account.getUser().getGender().toString());
            userResponse.setUrlAvatar(account.getUser().getUrlAvatar());
            userResponse.setUrlBackground(account.getUser().getUrlBackground());
            response.setUserResponse(userResponse);
        }
        return response;
    }
} 
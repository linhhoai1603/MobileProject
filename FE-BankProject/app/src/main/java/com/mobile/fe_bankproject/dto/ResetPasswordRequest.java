package com.mobile.fe_bankproject.dto;

public class ResetPasswordRequest {
    private String phoneNumber;
    private String otp;
    private String newPassword;

    public ResetPasswordRequest(String phoneNumber, String otp, String newPassword) {
        this.phoneNumber = phoneNumber;
        this.otp = otp;
        this.newPassword = newPassword;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
} 
package com.mobile.fe_bankproject.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class OTPVerifyRequest implements Serializable {
    private String email;
    private String otp;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
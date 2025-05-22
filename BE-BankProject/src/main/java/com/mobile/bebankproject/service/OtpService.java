package com.mobile.bebankproject.service;

public interface OtpService {
    void sendOtp(String emailOrPhone);
    // Có thể bổ sung verifyOtp nếu cần
}

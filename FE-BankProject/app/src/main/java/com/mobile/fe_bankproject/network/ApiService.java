package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.AccountLogin;
import com.mobile.fe_bankproject.dto.AccountRegister;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.dto.ChangePasswordRequest;
import com.mobile.fe_bankproject.dto.OTPVerifyRequest;
import com.mobile.fe_bankproject.dto.UpdateProfileRequest;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    @POST("account/login")
    Call<AccountResponse> login(@Body AccountLogin loginRequest);

    @POST("account/register")
    Call<Void> register(@Body AccountRegister registerRequest);

    @POST("account/confirm-account")
    Call<Void> confirmAccount(@Body Map<String, String> request);

    @POST("account/send-otp")
    Call<Void> resendOTP(@Body String email);

    @POST("account/change-password-logined")
    Call<Void> changePassword(@Body ChangePasswordRequest request);

    @POST("account/send-otp-change-password")
    Call<Void> sendOtpChangePassword(@Body String accountNumber);
    
    @POST("account/close")
    Call<Void> closeAccount(@Body Map<String, String> request);

    @PUT("account/update-profile")
    Call<Void> updateProfile(@Body UpdateProfileRequest request);

    @POST("api/cards/change-pin")
    Call<Void> changePin(@Body Map<String, String> request);

    @POST("api/cards/forgot-pin/send-otp")
    Call<Void> sendOtpForPin(@Body Map<String, String> request);

    @POST("api/cards/forgot-pin/verify-otp")
    Call<Void> verifyOtpForPin(@Body Map<String, String> request);

    @POST("api/cards/forgot-pin/reset-pin")
    Call<Void> resetPin(@Body Map<String, String> request);
}
package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.AccountLogin;
import com.mobile.fe_bankproject.dto.AccountRegister;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.dto.OTPVerifyRequest;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("account/login")
    Call<AccountResponse> login(@Body AccountLogin loginRequest);

    @POST("account/register")
    Call<Void> register(@Body AccountRegister registerRequest);

    @POST("account/confirm-account")
    Call<Void> confirmAccount(@Body Map<String, String> request);

    @POST("account/send-otp")
    Call<Void> resendOTP(@Body String email);
} 
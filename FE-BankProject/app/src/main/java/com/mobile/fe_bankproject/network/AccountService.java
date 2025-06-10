package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.AccountLogin;
import com.mobile.fe_bankproject.dto.AccountRegister;
import com.mobile.fe_bankproject.dto.AccountResponse;
import com.mobile.fe_bankproject.dto.ChangePasswordRequest;
import com.mobile.fe_bankproject.dto.UpdateProfileRequest;
import com.mobile.fe_bankproject.dto.ImageUploadResponse;

import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface AccountService {
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

    @Multipart
    @POST("api/avatar/{accountNumber}")
    Call<ImageUploadResponse> uploadAvatar(
            @Path("accountNumber") String accountNumber,
            @Part MultipartBody.Part body
    );

    @Multipart
    @POST("api/background/{accountNumber}")
    Call<ImageUploadResponse> uploadBackground(@Path("accountNumber") String accountNumber,@Part MultipartBody.Part body);

    @GET("account/balance/{accountNumber}")
    Call<AccountResponse> updateBalance(@Path("accountNumber") String accountNumber);

    @GET("account/{accountNumber}")
    Call<AccountResponse> getAccountInfo(@Path("accountNumber") String accountNumber);
}
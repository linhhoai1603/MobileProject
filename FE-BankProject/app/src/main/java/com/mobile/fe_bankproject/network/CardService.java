package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.CardResponse;
import java.util.Map;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CardService {
    @POST("cards/create")
    Call<CardResponse> createCard(@Body RequestBody requestBody);

    @POST("cards/lock")
    Call<ResponseBody> lockCard(@Body RequestBody body);

    @POST("cards/unlock")
    Call<ResponseBody> unlockCard(@Body RequestBody body);

    @GET("cards/{cardNumber}")
    Call<CardResponse> getCardInfo(@Path("cardNumber") String cardNumber);

    @GET("cards/{cardNumber}/status")
    Call<ResponseBody> getCardStatus(@Path("cardNumber") String cardNumber);

    @POST("cards/verify-pin")
    Call<Map<String, Boolean>> verifyPin(@Body Map<String, String> request);

    @POST("cards/change-pin")
    Call<Void> changePin(@Body Map<String, String> request);

    @POST("cards/forgot-pin/send-otp")
    Call<Void> sendOtpForPin(@Body Map<String, String> request);

    @POST("cards/forgot-pin/verify-otp")
    Call<Void> verifyOtpForPin(@Body Map<String, String> request);

    @POST("cards/forgot-pin/reset-pin")
    Call<Void> resetPin(@Body Map<String, String> request);
} 
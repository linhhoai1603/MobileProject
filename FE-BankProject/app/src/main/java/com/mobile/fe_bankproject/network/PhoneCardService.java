package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.PhoneCardDTO;
import com.mobile.fe_bankproject.dto.RechargePreviewDTO;
import com.mobile.fe_bankproject.dto.RechargeRequest;
import com.mobile.fe_bankproject.dto.RechargeResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface PhoneCardService {
    @GET("phone-card/get/{telcoProvider}")
    Call<List<PhoneCardDTO>> getPhoneCardByTelcoProvider(@Path("telcoProvider") String telcoProvider);

    @POST("recharge/preview")
    Call<RechargePreviewDTO> previewPhoneCard(@Body RechargeRequest request);

    @POST("recharge/purchase")
    Call<RechargeResponse> purchasePhoneCard(@Body RechargeRequest request);
}

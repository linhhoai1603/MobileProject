package com.mobile.fe_bankproject.network.api;

import com.mobile.fe_bankproject.network.model.TransferPreviewRequest;
import com.mobile.fe_bankproject.network.model.TransferPreviewResponse;
// TODO: Import SendOtpRequest and SendOtpResponse
// import com.mobile.fe_bankproject.network.model.SendOtpRequest;
// import com.mobile.fe_bankproject.network.model.SendOtpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TransferApi {

    @POST("/api/account/transfer/preview")
    Call<TransferPreviewResponse> previewTransfer(@Body TransferPreviewRequest request);

    // TODO: Define the endpoint for sending OTP. Adjust the Request and Response models as needed.
    // @POST("/api/account/transfer/request/email")
    // Call<SendOtpResponse> sendOtpRequest(@Body SendOtpRequest request);

} 
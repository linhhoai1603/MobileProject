package com.mobile.fe_bankproject.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Body;
import retrofit2.http.Query;

import com.mobile.fe_bankproject.dto.AccountLookupResponse;
import com.mobile.fe_bankproject.dto.FundTransferConfirmRequest;
import com.mobile.fe_bankproject.dto.FundTransferRequest;
import com.mobile.fe_bankproject.dto.FundTransferPreview;
public interface ApiService {

    @GET("/api/account/lookup-name")
    Call<AccountLookupResponse> lookupAccountName(@Query("accountNumber") String accountNumber);

    // phương thức gọi API preview chuyển tiền
    @POST("/api/account/transfer/preview")
    Call<FundTransferPreview> transferPreview(@Body FundTransferRequest request);

    @POST("/api/account/transfer/request/email")
    Call<String> requestEmailTransfer(@Body FundTransferRequest request);

    @POST("/api/account/transfer/confirm")
    Call<String> confirmTransfer(@Body FundTransferConfirmRequest request);

}
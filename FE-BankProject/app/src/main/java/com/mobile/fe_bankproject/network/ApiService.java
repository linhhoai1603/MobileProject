package com.mobile.fe_bankproject.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import com.mobile.fe_bankproject.dto.AccountLookupResponse;

public interface ApiService {

    @GET("/api/account/lookup-name")
    Call<AccountLookupResponse> lookupAccountName(@Query("accountNumber") String accountNumber);

    // Thêm các API endpoints khác của bạn vào đây

} 
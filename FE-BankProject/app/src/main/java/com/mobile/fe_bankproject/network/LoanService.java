package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.LoanRequest;
import com.mobile.fe_bankproject.dto.LoanResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoanService {
    @POST("loan/create")
    Call<LoanResponse> createLoan(@Body LoanRequest request);
}

package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.BillResponse;
import com.mobile.fe_bankproject.dto.BillRequest;
import com.mobile.fe_bankproject.dto.BillStatus;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface BillService {
    @GET("bill/list")
    Call<List<BillResponse>> getBillList();

    @GET("bill/user/{userId}")
    Call<List<BillResponse>> getBillsByUserId(@Path("userId") Integer userId);

    @GET("bill/user/{userId}/status/{status}")
    Call<List<BillResponse>> getBillsByStatusAndUser(
            @Path("userId") int userId,
            @Path("status") BillStatus status);

    @GET("bill/code/{billCode}")
    Call<BillResponse> getBillByCode(@Path("billCode") String billCode);

    @POST("bill/payment")
    Call<BillResponse> payment(@Body BillRequest request);
}

package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.TransactionResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TransactionService {
  @GET("accounts/{acct}/transactions")
  Call<List<TransactionResponse>> getHistory(
    @Path("acct") String accountNumber,
    @Query("page") int page,
    @Query("size") int size
  );
}
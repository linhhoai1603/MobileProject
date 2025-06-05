package com.mobile.fe_bankproject.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    // TODO: Thay thế bằng Base URL thực tế của backend Spring Boot của bạn
    // Ví dụ: "http://192.168.1.100:8080/"
    private static final String BASE_URL = "http://10.0.138.12:8080/api/";

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    // Phương thức tiện ích để lấy ApiService
    public static ApiService getApiService() {
        return getClient().create(ApiService.class);
    }
} 
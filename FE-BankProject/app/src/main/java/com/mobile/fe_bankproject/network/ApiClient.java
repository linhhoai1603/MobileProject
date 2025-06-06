package com.mobile.fe_bankproject.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory; // Import ScalarsConverterFactory

public class ApiClient {

    // TODO: Thay thế bằng Base URL thực tế của backend Spring Boot của bạn
    // Ví dụ: "http://192.168.1.100:8080/"
    private static final String BASE_URL = "http://10.0.146.235:8080/api/";  // Hoang IPv4 Address

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    // Thêm ScalarsConverterFactory để xử lý các phản hồi dạng String
                    .addConverterFactory(ScalarsConverterFactory.create())
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
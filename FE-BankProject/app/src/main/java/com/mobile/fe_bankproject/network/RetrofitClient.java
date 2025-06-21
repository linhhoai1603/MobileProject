package com.mobile.fe_bankproject.network;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class RetrofitClient {
//    private static final String API_URL = "http://192.168.1.5:8080/api/"; // Hoài
//    private static final String API_URL = "http://10.0.146.235:8080/api/"; // Hoang IPv4 Address
//    private static final String API_URL = "http://10.0.2.2:8080/api/";
private static final String API_URL = "https://be-mobile-production-1f49.up.railway.app/api/";  // railway
    private static RetrofitClient instance;
    private final Retrofit retrofit;

    private RetrofitClient() {
        // Create logging interceptor
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Create OkHttpClient with timeout and logging
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        // Configure Gson to be lenient
        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(API_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public AddressService getAddressService() {
        return retrofit.create(AddressService.class);
    }

    public AccountService getAccountService() {
        return retrofit.create(AccountService.class);
    }
    public ApiService getApiService(){
        return retrofit.create(ApiService.class);
    }
    public PhoneCardService getPhoneCardService(){
        return retrofit.create(PhoneCardService.class);
    }
    public DataMobileService getDataMobileService(){
        return retrofit.create(DataMobileService.class);
    }
    public CardService getCardService(){
        return retrofit.create(CardService.class);
    }
    public LoanService getLoanService() {
        return retrofit.create(LoanService.class);
    }
    public BillService getBillService(){
        return retrofit.create(BillService.class);
    }
    public TransactionService getTransactionService() {
        return retrofit.create(TransactionService.class);
    }
} 
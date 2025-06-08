package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.DataMobile;
import com.mobile.fe_bankproject.dto.DataPackageFilterRequest;
import com.mobile.fe_bankproject.dto.DataPackagePreview;
import com.mobile.fe_bankproject.dto.DataPackageRequest;
import com.mobile.fe_bankproject.dto.DataPackageResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DataMobileService {
    @POST("data-mobile/packages")
    Call<List<DataMobile>> getFilteredPackages(@Body DataPackageFilterRequest request);

    @POST("data-mobile/preview")
    Call<DataPackagePreview> preview(@Body DataPackageRequest request);

    @POST("data-mobile/purchase")
    Call<DataPackageResponse> purchase(@Body DataPackageRequest request);
}

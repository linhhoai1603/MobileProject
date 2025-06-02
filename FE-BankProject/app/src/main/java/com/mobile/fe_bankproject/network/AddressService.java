package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.Province;
import com.mobile.fe_bankproject.dto.District;
import com.mobile.fe_bankproject.dto.Ward;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AddressService {
    @GET("v1/partner/areas/province")
    Call<ProvinceResponse> getProvinces();

    @GET("v1/partner/areas/district/{provinceCode}")
    Call<List<District>> getDistricts(@Path("provinceCode") String provinceCode);

    @GET("v1/partner/areas/ward/{districtCode}")
    Call<List<Ward>> getWards(@Path("districtCode") String districtCode);
} 
package com.mobile.fe_bankproject.network;

import com.mobile.fe_bankproject.dto.DistrictDTO;
import com.mobile.fe_bankproject.dto.ProvinceDTO;
import com.mobile.fe_bankproject.dto.WardDTO;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface AddressService {
    @GET("address/provinces")
    Call<List<ProvinceDTO>> getProvinces();

    @GET("address/districts/{codeProvince}")
    Call<List<DistrictDTO>> getDistricts(@Path("codeProvince") String provinceCode);

    @GET("address/wards/{codeDistrict}")
    Call<List<WardDTO>> getWards(@Path("codeDistrict") String districtCode);
} 
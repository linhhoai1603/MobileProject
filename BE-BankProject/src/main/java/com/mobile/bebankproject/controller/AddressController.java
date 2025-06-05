package com.mobile.bebankproject.controller;

import com.mobile.bebankproject.dto.DistrictDTO;
import com.mobile.bebankproject.dto.ProvinceDTO;
import com.mobile.bebankproject.dto.WardDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("api/address")
public class AddressController {
    
    private final String BASE_URL = "https://provinces.open-api.vn/api";
    private final RestTemplate restTemplate;

    public AddressController() {
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/provinces")
    public ResponseEntity<List<ProvinceDTO>> getProvinces() {
        try {
            ProvinceDTO[] provinces = restTemplate.getForObject(BASE_URL + "/p/", ProvinceDTO[].class);
            return ResponseEntity.ok(Arrays.asList(provinces));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/districts/{codeProvince}")
    public ResponseEntity<List<DistrictDTO>> getDistricts(@PathVariable String codeProvince) {
        try {
            ProvinceDTO province = restTemplate.getForObject(
                BASE_URL + "/p/" + codeProvince + "?depth=2",
                ProvinceDTO.class
            );
            
            if (province == null || province.getDistricts() == null) {
                return ResponseEntity.badRequest().build();
            }

            List<DistrictDTO> districts = province.getDistricts();

            return ResponseEntity.ok(districts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    @GetMapping("/wards/{codeDistrict}")
    public ResponseEntity<List<WardDTO>> getWards(@PathVariable String codeDistrict) {
        try {
            DistrictDTO districtDTO = restTemplate.getForObject(
                    BASE_URL + "/d/" + codeDistrict + "?depth=2",
                    DistrictDTO.class
            );

            if (districtDTO == null || districtDTO.getWards() == null) {
                return ResponseEntity.badRequest().build();
            }

            List<WardDTO> wards = districtDTO.getWards();

            return ResponseEntity.ok(wards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}

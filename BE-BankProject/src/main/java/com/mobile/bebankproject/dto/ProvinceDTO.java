package com.mobile.bebankproject.dto;

import lombok.Data;
import java.util.List;

@Data
public class ProvinceDTO {
    private int code;
    private String name;
    private String division_type;
    private String codename;
    private int phone_code;
    private List<DistrictDTO> districts;
}


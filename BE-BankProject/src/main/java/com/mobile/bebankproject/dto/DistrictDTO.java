package com.mobile.bebankproject.dto;

import lombok.Data;
import java.util.List;

@Data
public class DistrictDTO {
    private int code;
    private String name;
    private String division_type;
    private String codename;
    private List<WardDTO> wards;
}

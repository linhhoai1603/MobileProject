package com.mobile.fe_bankproject.dto;

import lombok.Data;

@Data
public class DataPackageFilterRequest {
    private TelcoProvider provider;
    private Integer validDate;
    private Integer quantity;
} 
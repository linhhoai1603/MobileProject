package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.TelcoProvider;
import lombok.Data;

@Data
public class DataPackageFilterRequest {
    private TelcoProvider provider;
    private Integer validDate;
    private Integer quantity;
} 
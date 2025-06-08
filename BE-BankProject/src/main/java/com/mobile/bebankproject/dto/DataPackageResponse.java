package com.mobile.bebankproject.dto;

import com.mobile.bebankproject.model.TelcoProvider;
import lombok.Data;

@Data
public class DataPackageResponse {
    private String accountNumber;
    private String phoneNumber;
    private int packageId;
    private String packageName;
    private int amount; // mb
    private int validDate;
    private String status;
    private TelcoProvider telcoProvider;
    private String message;
    private String time;
}

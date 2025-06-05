package com.mobile.fe_bankproject.dto;

import com.google.gson.annotations.SerializedName;

public class ImageUploadResponse {
    @SerializedName("status")
    private String status;

    @SerializedName("message")
    private String message;

    @SerializedName("url")
    private String url;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getUrl() {
        return url;
    }
} 
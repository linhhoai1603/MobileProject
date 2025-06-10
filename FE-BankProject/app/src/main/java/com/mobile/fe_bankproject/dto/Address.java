package com.mobile.fe_bankproject.dto;

import java.io.Serializable;

public class Address implements Serializable {
    private String vilage;
    private String commune;
    private String district;
    private String province;

    public String getVilage() {
        return vilage;
    }

    public void setVilage(String vilage) {
        this.vilage = vilage;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
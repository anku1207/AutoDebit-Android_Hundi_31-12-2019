package com.uav.autodebit.vo;

import java.io.Serializable;

public class CityVO extends BaseVO implements Serializable {



    private Integer cityId;
    private String cityName;
    private StateVO stateRegion;



    public CityVO() {
    }

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public StateVO getStateRegion() {
        return stateRegion;
    }

    public void setStateRegion(StateVO stateRegion) {
        this.stateRegion = stateRegion;
    }
}

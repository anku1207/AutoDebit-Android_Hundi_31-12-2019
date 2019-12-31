package com.uav.autodebit.vo;

import java.io.Serializable;

public class StateVO implements Serializable {

    private Integer stateRegionId;
    private String stateRegionName;

    public StateVO() {
    }

    public Integer getStateRegionId() {
        return stateRegionId;
    }

    public void setStateRegionId(Integer stateRegionId) {
        this.stateRegionId = stateRegionId;
    }

    public String getStateRegionName() {
        return stateRegionName;
    }

    public void setStateRegionName(String stateRegionName) {
        this.stateRegionName = stateRegionName;
    }
}

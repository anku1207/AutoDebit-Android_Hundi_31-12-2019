package com.uav.autodebit.vo;

import java.io.Serializable;

public class AuthStatusVO extends BaseVO implements Serializable {

    private Integer authStatusId;
    private String statusName;


    public AuthStatusVO() {
    }


    public Integer getAuthStatusId() {
        return authStatusId;
    }

    public void setAuthStatusId(Integer authStatusId) {
        this.authStatusId = authStatusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}

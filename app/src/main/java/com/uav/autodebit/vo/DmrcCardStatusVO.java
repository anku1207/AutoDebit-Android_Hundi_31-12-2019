package com.uav.autodebit.vo;

import java.io.Serializable;

public class DmrcCardStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer statusId;
    private String  statusName;

    public Integer getStatusId() {
        return statusId;
    }

    public void setStatusId(Integer statusId) {
        this.statusId = statusId;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
}
package com.uav.autodebit.vo;

import java.io.Serializable;

public class DmrcCardStatusVO implements Serializable {


    public static Integer APPLIED=1;
    public static Integer ACTIVE=2;
    public static Integer BLOCKED=3;
    public static Integer DELIVERY=4;
    public static Integer APPROVE=5;
    public static Integer CREATE=6;
    public static Integer DELIVERED=7;

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
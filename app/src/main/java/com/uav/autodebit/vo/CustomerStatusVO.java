package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerStatusVO implements Serializable {

    public static final Integer CREATED= 1;
    public static final Integer  BLACKLIST =2;
    public static final Integer  SIGNUP_MOBILE_OTP_VERIFIED =3;
    public static final Integer  CUSTOMER_VERFIED =4;


    public CustomerStatusVO(){

    }

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

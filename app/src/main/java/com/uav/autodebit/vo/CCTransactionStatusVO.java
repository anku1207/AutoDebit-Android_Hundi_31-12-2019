package com.uav.autodebit.vo;

import java.io.Serializable;

public class CCTransactionStatusVO  extends BaseVO  implements Serializable {

    public static final int SUCCESS =1;
    public static final int  PENDING=2;
    public static final int FAILURE=3;

    private Integer statusId;
    private String statusName;


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

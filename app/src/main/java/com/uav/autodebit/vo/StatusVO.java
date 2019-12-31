package com.uav.autodebit.vo;

public class StatusVO extends BaseVO {

    public static Integer ACTIVE=1;
    public static Integer INACTIVE=2;
    public static Integer CREATED=3;
    public static Integer COMPLETED=4;
    public static Integer INITIATIED=5;
    public static Integer FAIELD=6;
    public static Integer SUCCESSFULL=7;
    public static Integer PENDING=8;


    private Integer statusId;
    private String  statusName;

    public StatusVO() {
    }

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

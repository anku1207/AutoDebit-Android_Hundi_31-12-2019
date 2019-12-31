package com.uav.autodebit.vo;

import java.io.Serializable;

public class LableVO implements Serializable {

    public static final Integer CUSTOMER_VERIFIED = 1;
    public static final Integer PAN_VERIFIED = 2;
    public static final Integer CREDIT_SCORE = 3;
    public static final Integer BANK_VERIFIED = 4;
    public static final Integer ENACH= 5;
    public static final Integer SI= 6;


    private Integer lableId;
    private String  name;

    public Integer getLableId() {
        return lableId;
    }

    public void setLableId(Integer lableId) {
        this.lableId = lableId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

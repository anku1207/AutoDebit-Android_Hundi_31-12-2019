package com.uav.autodebit.vo;

import java.io.Serializable;

public class AuthServiceProviderVO extends BaseVO  implements Serializable {

    public static int PAYNIMOSI=0; //currently service suspended
    public static int FIRSTDATA =1;
    public static int EQUIFAX =2;
    public static int ENACHIDFC=3;
    public static int CCAVENUE=4;
    public static int PAYU=5;
    public static int AUTOPE_PG=6;
    public static int AUTOPE_PG_UPI=7;


    private Integer providerId;
    private String providerName;


    public AuthServiceProviderVO() {

    }

    public Integer getProviderId() {
        return providerId;
    }

    public void setProviderId(Integer providerId) {
        this.providerId = providerId;
    }

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }
}

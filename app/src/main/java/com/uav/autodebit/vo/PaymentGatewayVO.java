package com.uav.autodebit.vo;

import java.io.Serializable;

public class PaymentGatewayVO extends BaseVO  implements Serializable {
    public static final int PAYUINDIA=1;
    public static final int IRCTCCOD=2;
    public static final int IRCTCCODMOB=3;
    public static final int HDFC=4;

    private Integer gatewayId;
    private String gatewayName;

    public Integer getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(Integer gatewayId) {
        this.gatewayId = gatewayId;
    }

    public String getGatewayName() {
        return gatewayName;
    }

    public void setGatewayName(String gatewayName) {
        this.gatewayName = gatewayName;
    }
}

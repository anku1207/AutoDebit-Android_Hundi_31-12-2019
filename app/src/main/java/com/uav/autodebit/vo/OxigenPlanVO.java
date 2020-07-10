package com.uav.autodebit.vo;

import java.io.Serializable;

public class OxigenPlanVO extends BaseVO  implements Serializable {


    private Integer planId;

    private ServiceTypeVO serviceType;
    private String operatorAlias;
    private String stateAlias;
    private String plan;
    private String tariff;
    private Long statusUpdateAt;




    public OxigenPlanVO() {
    }


    public Integer getPlanId() {
        return planId;
    }

    public void setPlanId(Integer planId) {
        this.planId = planId;
    }

    public ServiceTypeVO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeVO serviceType) {
        this.serviceType = serviceType;
    }

    public String getOperatorAlias() {
        return operatorAlias;
    }

    public void setOperatorAlias(String operatorAlias) {
        this.operatorAlias = operatorAlias;
    }

    public String getStateAlias() {
        return stateAlias;
    }

    public void setStateAlias(String stateAlias) {
        this.stateAlias = stateAlias;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public String getTariff() {
        return tariff;
    }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public Long getStatusUpdateAt() {
        return statusUpdateAt;
    }

    public void setStatusUpdateAt(Long statusUpdateAt) {
        this.statusUpdateAt = statusUpdateAt;
    }
}

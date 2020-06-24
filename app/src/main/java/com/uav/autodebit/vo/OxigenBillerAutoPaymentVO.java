package com.uav.autodebit.vo;

import java.io.Serializable;

public class OxigenBillerAutoPaymentVO extends BaseVO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer typeId;
    private CustomerVO customer;
    private ServiceTypeVO serviceType;
    private String operaterName;
    private String searchBillValue;
    private Double amount;

    public OxigenBillerAutoPaymentVO() {

    }

    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public ServiceTypeVO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeVO serviceType) {
        this.serviceType = serviceType;
    }

    public String getOperaterName() {
        return operaterName;
    }

    public void setOperaterName(String operaterName) {
        this.operaterName = operaterName;
    }

    public String getSearchBillValue() {
        return searchBillValue;
    }

    public void setSearchBillValue(String searchBillValue) {
        this.searchBillValue = searchBillValue;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

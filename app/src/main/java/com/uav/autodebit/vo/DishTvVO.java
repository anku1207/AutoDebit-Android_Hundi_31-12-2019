package com.uav.autodebit.vo;

import java.io.Serializable;

public class DishTvVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;


    private Integer dishtvId;
    private CustomerVO customer;
    private String transactionId;
    private Integer rechargeAmt ;
    private String request;
    private String vcDetails;
    private String SMSId;
    private String jsonData;
    private String vcNo;
    private Double lastRechargeAmt;
    private Long switchOffDate;
    private Long lastUpdateDate;
    private StatusVO status;
    private String subscriberName;

    //Transient
    private Integer enachMandateAmount;
    private String enachMandateId;

    public DishTvVO() {
    }

    public Integer getDishtvId() {
        return dishtvId;
    }

    public void setDishtvId(Integer dishtvId) {
        this.dishtvId = dishtvId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public Integer getRechargeAmt() {
        return rechargeAmt;
    }

    public void setRechargeAmt(Integer rechargeAmt) {
        this.rechargeAmt = rechargeAmt;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getVcDetails() {
        return vcDetails;
    }

    public void setVcDetails(String vcDetails) {
        this.vcDetails = vcDetails;
    }

    public String getSMSId() {
        return SMSId;
    }

    public void setSMSId(String SMSId) {
        this.SMSId = SMSId;
    }

    public String getJsonData() {
        return jsonData;
    }

    public void setJsonData(String jsonData) {
        this.jsonData = jsonData;
    }

    public String getVcNo() {
        return vcNo;
    }

    public void setVcNo(String vcNo) {
        this.vcNo = vcNo;
    }

    public Double getLastRechargeAmt() {
        return lastRechargeAmt;
    }

    public void setLastRechargeAmt(Double lastRechargeAmt) {
        this.lastRechargeAmt = lastRechargeAmt;
    }

    public Long getSwitchOffDate() {
        return switchOffDate;
    }

    public void setSwitchOffDate(Long switchOffDate) {
        this.switchOffDate = switchOffDate;
    }

    public Long getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Long lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public StatusVO getStatus() {
        return status;
    }

    public void setStatus(StatusVO status) {
        this.status = status;
    }

    public String getSubscriberName() {
        return subscriberName;
    }

    public void setSubscriberName(String subscriberName) {
        this.subscriberName = subscriberName;
    }

    public Integer getEnachMandateAmount() {
        return enachMandateAmount;
    }

    public void setEnachMandateAmount(Integer enachMandateAmount) {
        this.enachMandateAmount = enachMandateAmount;
    }

    public String getEnachMandateId() {
        return enachMandateId;
    }

    public void setEnachMandateId(String enachMandateId) {
        this.enachMandateId = enachMandateId;
    }
}

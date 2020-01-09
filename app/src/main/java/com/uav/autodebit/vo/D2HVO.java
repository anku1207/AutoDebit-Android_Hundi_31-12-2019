package com.uav.autodebit.vo;

import java.io.Serializable;

public class D2HVO   extends BaseVO implements Serializable {

    private Integer d2hId;
    private CustomerVO customer;
    private String transactionId;
    private Double rechargeAmt ;
    private String request;
    private String vcdetails;
    private String SMSId;
    private String jsonData;
    private String vcNo;
    private Double lastRechargeAmt;
    private Long switchOffDate;
    private Long lastUpdateDate;
    private StatusVO status;
    private String subscriberName;

    private String enachMandateId;
    private Integer enachMandateAmount;


    public Integer getD2hId() {
        return d2hId;
    }

    public void setD2hId(Integer d2hId) {
        this.d2hId = d2hId;
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

    public Double getRechargeAmt() {
        return rechargeAmt;
    }

    public void setRechargeAmt(Double rechargeAmt) {
        this.rechargeAmt = rechargeAmt;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getVcdetails() {
        return vcdetails;
    }

    public void setVcdetails(String vcdetails) {
        this.vcdetails = vcdetails;
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

    public String getEnachMandateId() {
        return enachMandateId;
    }

    public void setEnachMandateId(String enachMandateId) {
        this.enachMandateId = enachMandateId;
    }

    public Integer getEnachMandateAmount() {
        return enachMandateAmount;
    }

    public void setEnachMandateAmount(Integer enachMandateAmount) {
        this.enachMandateAmount = enachMandateAmount;
    }
}

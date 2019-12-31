package com.uav.autodebit.vo;

public class ServiceChargesVO extends BaseVO{


    private Integer serviceChargesId;
    private ScoreBucketVO scoreBucketId;
    private ServiceTypeVO serviceTypeId;
    private Double canvince;
    private Double amount;
    private Double gst;
    private Double firstTimePay;
    private Double recurring;
    private Integer frequencyDay;
    private Long   wefDate;

    public ServiceChargesVO() {
    }

    public Integer getServiceChargesId() {
        return serviceChargesId;
    }

    public void setServiceChargesId(Integer serviceChargesId) {
        this.serviceChargesId = serviceChargesId;
    }

    public ScoreBucketVO getScoreBucketId() {
        return scoreBucketId;
    }

    public void setScoreBucketId(ScoreBucketVO scoreBucketId) {
        this.scoreBucketId = scoreBucketId;
    }

    public ServiceTypeVO getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(ServiceTypeVO serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public Double getCanvince() {
        return canvince;
    }

    public void setCanvince(Double canvince) {
        this.canvince = canvince;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getGst() {
        return gst;
    }

    public void setGst(Double gst) {
        this.gst = gst;
    }

    public Double getFirstTimePay() {
        return firstTimePay;
    }

    public void setFirstTimePay(Double firstTimePay) {
        this.firstTimePay = firstTimePay;
    }

    public Double getRecurring() {
        return recurring;
    }

    public void setRecurring(Double recurring) {
        this.recurring = recurring;
    }

    public Integer getFrequencyDay() {
        return frequencyDay;
    }

    public void setFrequencyDay(Integer frequencyDay) {
        this.frequencyDay = frequencyDay;
    }

    public Long getWefDate() {
        return wefDate;
    }

    public void setWefDate(Long wefDate) {
        this.wefDate = wefDate;
    }
}

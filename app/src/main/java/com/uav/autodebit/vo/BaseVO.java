package com.uav.autodebit.vo;

import java.util.List;

public class BaseVO {

    private String statusCode;
    private List<String> errorMsgs;
    private String anonymousString;
    private Integer otpExpiredMobile;
    private Integer otpExpiredEmail;
    private String actionname;
    private String image;
    private Integer anonymousInteger;
    private String localCache;
    private String enachDetails;
    private Integer serviceId;
    private Double serviceAdopteBMA;
    private boolean eventIs;






    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(List<String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

    public String getAnonymousString() {
        return anonymousString;
    }

    public void setAnonymousString(String anonymousString) {
        this.anonymousString = anonymousString;
    }

    public Integer getOtpExpiredMobile() {
        return otpExpiredMobile;
    }

    public void setOtpExpiredMobile(Integer otpExpiredMobile) {
        this.otpExpiredMobile = otpExpiredMobile;
    }

    public Integer getOtpExpiredEmail() {
        return otpExpiredEmail;
    }

    public void setOtpExpiredEmail(Integer otpExpiredEmail) {
        this.otpExpiredEmail = otpExpiredEmail;
    }

    public String getActionname() {
        return actionname;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getAnonymousInteger() {
        return anonymousInteger;
    }

    public void setAnonymousInteger(Integer anonymousInteger) {
        this.anonymousInteger = anonymousInteger;
    }

    public String getLocalCache() {
        return localCache;
    }

    public void setLocalCache(String localCache) {
        this.localCache = localCache;
    }

    public String getEnachDetails() {
        return enachDetails;
    }

    public void setEnachDetails(String enachDetails) {
        this.enachDetails = enachDetails;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }


    public Double getServiceAdopteBMA() {
        return serviceAdopteBMA;
    }

    public void setServiceAdopteBMA(Double serviceAdopteBMA) {
        this.serviceAdopteBMA = serviceAdopteBMA;
    }

    public boolean isEventIs() {
        return eventIs;
    }

    public void setEventIs(boolean eventIs) {
        this.eventIs = eventIs;
    }
}

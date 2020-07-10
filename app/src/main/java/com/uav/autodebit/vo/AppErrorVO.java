package com.uav.autodebit.vo;

import java.io.Serializable;

public class AppErrorVO extends BaseVO  implements Serializable {

    private Integer appErrorId;
    private String customerId;
    private String mobileName;
    private String appVersion;
    private String errorMessage;
    private String deviceInfo;
    private String mobileVersion;
    private Long createdAt;

    public AppErrorVO() {
    }

    public Integer getAppErrorId() {
        return appErrorId;
    }

    public void setAppErrorId(Integer appErrorId) {
        this.appErrorId = appErrorId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getMobileName() {
        return mobileName;
    }

    public void setMobileName(String mobileName) {
        this.mobileName = mobileName;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getDeviceInfo() {
        return deviceInfo;
    }

    public void setDeviceInfo(String deviceInfo) {
        this.deviceInfo = deviceInfo;
    }

    public String getMobileVersion() {
        return mobileVersion;
    }

    public void setMobileVersion(String mobileVersion) {
        this.mobileVersion = mobileVersion;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }
}

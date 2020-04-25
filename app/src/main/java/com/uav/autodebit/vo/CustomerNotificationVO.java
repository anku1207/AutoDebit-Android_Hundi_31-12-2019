package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerNotificationVO  extends BaseVO implements Serializable {

    private Integer cnId;
    private CustomerVO customer;
    private String body;
    private String title;
    private String serviceIcon;
    private String bigImage;
    private String notificationType;
    private String activityName;
    private String message;
    private String moveActivity;
    private String createdAt;
    private int storeData;


    public CustomerNotificationVO() {
    }

    public Integer getCnId() {
        return cnId;
    }

    public void setCnId(Integer cnId) {
        this.cnId = cnId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getServiceIcon() {
        return serviceIcon;
    }

    public void setServiceIcon(String serviceIcon) {
        this.serviceIcon = serviceIcon;
    }

    public String getBigImage() {
        return bigImage;
    }

    public void setBigImage(String bigImage) {
        this.bigImage = bigImage;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getMoveActivity() {
        return moveActivity;
    }

    public void setMoveActivity(String moveActivity) {
        this.moveActivity = moveActivity;
    }

    public int getStoreData() {
        return storeData;
    }

    public void setStoreData(int storeData) {
        this.storeData = storeData;
    }
}

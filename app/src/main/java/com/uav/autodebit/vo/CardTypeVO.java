package com.uav.autodebit.vo;

import java.io.Serializable;

public class CardTypeVO extends  BaseVO implements Serializable {


    private Integer cardTypeId;
    private String cardTypeName;
    private Integer sortNo ;
    private StatusVO statusVO;
    private ServiceTypeVO serviceTypeVO;
    private String description;
    private String imageURL;
    private Integer personalization;
    private String activityName;
    private String cardFees;

    public CardTypeVO() {

    }

    public Integer getCardTypeId() {
        return cardTypeId;
    }

    public void setCardTypeId(Integer cardTypeId) {
        this.cardTypeId = cardTypeId;
    }

    public String getCardTypeName() {
        return cardTypeName;
    }

    public void setCardTypeName(String cardTypeName) {
        this.cardTypeName = cardTypeName;
    }

    public Integer getSortNo() {
        return sortNo;
    }

    public void setSortNo(Integer sortNo) {
        this.sortNo = sortNo;
    }

    public StatusVO getStatusVO() {
        return statusVO;
    }

    public void setStatusVO(StatusVO statusVO) {
        this.statusVO = statusVO;
    }

    public ServiceTypeVO getServiceTypeVO() {
        return serviceTypeVO;
    }

    public void setServiceTypeVO(ServiceTypeVO serviceTypeVO) {
        this.serviceTypeVO = serviceTypeVO;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public Integer getPersonalization() {
        return personalization;
    }

    public void setPersonalization(Integer personalization) {
        this.personalization = personalization;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getCardFees() {
        return cardFees;
    }

    public void setCardFees(String cardFees) {
        this.cardFees = cardFees;
    }
}

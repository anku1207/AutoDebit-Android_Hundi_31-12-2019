package com.uav.autodebit.vo;

import java.io.Serializable;

public class ServiceTypeVO  extends BaseVO implements Serializable {

    private Integer serviceTypeId;
    private String  code;
    private String  name;
    private Integer archived;

    private LevelVO level;
    private String  skipLevel;
    private String  appIcon;
    private String  uiImage;
    private String  title;
    private Integer utility;
    private Integer serialNumber;
    private Integer adopted;
    private String banners;

    private Double mandateAmount;

    private String operatorRegionList;
    private String message;
    private Integer mandateRequired;

    public ServiceTypeVO(){

    }


    public Integer getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(Integer serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getArchived() {
        return archived;
    }

    public void setArchived(Integer archived) {
        this.archived = archived;
    }

    public LevelVO getLevel() {
        return level;
    }

    public void setLevel(LevelVO level) {
        this.level = level;
    }

    public String getSkipLevel() {
        return skipLevel;
    }

    public void setSkipLevel(String skipLevel) {
        this.skipLevel = skipLevel;
    }

    public String getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(String appIcon) {
        this.appIcon = appIcon;
    }

    public String getUiImage() {
        return uiImage;
    }

    public void setUiImage(String uiImage) {
        this.uiImage = uiImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getUtility() {
        return utility;
    }

    public void setUtility(Integer utility) {
        this.utility = utility;
    }

    public Integer getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(Integer serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getOperatorRegionList() {
        return operatorRegionList;
    }

    public void setOperatorRegionList(String operatorRegionList) {
        this.operatorRegionList = operatorRegionList;
    }

    public Integer getAdopted() {
        return adopted;
    }

    public void setAdopted(Integer adopted) {
        this.adopted = adopted;
    }

    public String getBanners() {
        return banners;
    }

    public void setBanners(String banners) {
        this.banners = banners;
    }

    public Double getMandateAmount() {
        return mandateAmount;
    }

    public void setMandateAmount(Double mandateAmount) {
        this.mandateAmount = mandateAmount;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getMandateRequired() {
        return mandateRequired;
    }

    public void setMandateRequired(Integer mandateRequired) {
        this.mandateRequired = mandateRequired;
    }
}

package com.uav.autodebit.vo;

import java.io.Serializable;
import java.util.List;

public class LocalCacheVO implements Serializable {

    private List<BannerVO> banners;
    private List<ServiceTypeVO> serives;
    private List<ServiceTypeVO> utilityBills;

    private String mobileOperator;
    private String mobileStateRegion;
    private String dthOperator;
    private String dthStateRegion;

    private String postPaid;
    private String water;
    private String gas;
    private String electricity;
    private String landline;
    private String boradBand;
    private String uitiyServiceHomePage;
    private String dmrc;

    private String eNachReturnURL;
    public LocalCacheVO(){

    }


    public List<BannerVO> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerVO> banners) {
        this.banners = banners;
    }

    public List<ServiceTypeVO> getSerives() {
        return serives;
    }

    public void setSerives(List<ServiceTypeVO> serives) {
        this.serives = serives;
    }

    public List<ServiceTypeVO> getUtilityBills() {
        return utilityBills;
    }

    public void setUtilityBills(List<ServiceTypeVO> utilityBills) {
        this.utilityBills = utilityBills;
    }

    public String geteNachReturnURL() {
        return eNachReturnURL;
    }

    public void seteNachReturnURL(String eNachReturnURL) {
        this.eNachReturnURL = eNachReturnURL;
    }

    public String getMobileOperator() {
        return mobileOperator;
    }

    public void setMobileOperator(String mobileOperator) {
        this.mobileOperator = mobileOperator;
    }

    public String getMobileStateRegion() {
        return mobileStateRegion;
    }

    public void setMobileStateRegion(String mobileStateRegion) {
        this.mobileStateRegion = mobileStateRegion;
    }

    public String getDthOperator() {
        return dthOperator;
    }

    public void setDthOperator(String dthOperator) {
        this.dthOperator = dthOperator;
    }

    public String getDthStateRegion() {
        return dthStateRegion;
    }

    public void setDthStateRegion(String dthStateRegion) {
        this.dthStateRegion = dthStateRegion;
    }

    public String getPostPaid() {
        return postPaid;
    }

    public void setPostPaid(String postPaid) {
        this.postPaid = postPaid;
    }

    public String getWater() {
        return water;
    }

    public void setWater(String water) {
        this.water = water;
    }

    public String getGas() {
        return gas;
    }

    public void setGas(String gas) {
        this.gas = gas;
    }

    public String getElectricity() {
        return electricity;
    }

    public void setElectricity(String electricity) {
        this.electricity = electricity;
    }

    public String getLandline() {
        return landline;
    }

    public void setLandline(String landline) {
        this.landline = landline;
    }

    public String getBoradBand() {
        return boradBand;
    }

    public void setBoradBand(String boradBand) {
        this.boradBand = boradBand;
    }

    public String getUitiyServiceHomePage() {
        return uitiyServiceHomePage;
    }

    public void setUitiyServiceHomePage(String uitiyServiceHomePage) {
        this.uitiyServiceHomePage = uitiyServiceHomePage;
    }

    public String getDmrc() {
        return dmrc;
    }

    public void setDmrc(String dmrc) {
        this.dmrc = dmrc;
    }
}

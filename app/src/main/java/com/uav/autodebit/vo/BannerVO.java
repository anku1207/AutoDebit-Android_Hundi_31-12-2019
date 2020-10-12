package com.uav.autodebit.vo;

import java.io.Serializable;
import java.util.List;

public class BannerVO extends BaseVO implements Serializable {


    private Integer bannerId;
    private String  bannerName;
    private String  bannerImage;
    private List<BannerVO> banners;
    private Long updateAt;
    private String description;
    private ServiceTypeVO serviceType;
    private Integer count;
    private String    webview;
    private Integer executive;

    public BannerVO (){

    }

    public  BannerVO(String bannerImage){
        this.bannerImage = bannerImage;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }


    public Integer getBannerId() {
        return bannerId;
    }

    public void setBannerId(Integer bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerName() {
        return bannerName;
    }

    public void setBannerName(String bannerName) {
        this.bannerName = bannerName;
    }

    public List<BannerVO> getBanners() {
        return banners;
    }

    public void setBanners(List<BannerVO> banners) {
        this.banners = banners;
    }

    public Long getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Long updateAt) {
        this.updateAt = updateAt;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ServiceTypeVO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeVO serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getWebview() {
        return webview;
    }

    public void setWebview(String webview) {
        this.webview = webview;
    }

    public Integer getExecutive() {
        return executive;
    }

    public void setExecutive(Integer executive) {
        this.executive = executive;
    }
}

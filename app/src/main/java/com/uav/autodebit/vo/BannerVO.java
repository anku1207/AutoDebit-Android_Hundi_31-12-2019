package com.uav.autodebit.vo;

import java.io.Serializable;
import java.util.List;

public class BannerVO extends BaseVO implements Serializable {


    private Integer bannerId;
    private String  bannerName;
    private String  bannerImage;
    private List<BannerVO> banners;
    private Long updateAt;

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

}

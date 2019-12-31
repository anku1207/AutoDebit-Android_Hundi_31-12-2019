package com.uav.autodebit.vo;

import android.graphics.Bitmap;
import android.net.Uri;

import java.io.Serializable;

public class ContactModelVO implements Serializable {
    private String id;
    private String name;
    private Bitmap image;
    private Uri url;
    private String mobileNumber;

    public ContactModelVO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Uri getUrl() {
        return url;
    }

    public void setUrl(Uri url) {
        this.url = url;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}

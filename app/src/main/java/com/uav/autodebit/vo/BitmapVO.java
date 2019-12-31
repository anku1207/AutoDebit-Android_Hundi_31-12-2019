package com.uav.autodebit.vo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

import java.io.Serializable;

public class BitmapVO implements Serializable {

    private String URL;
    private String fileName;
    private ImageView imageView;
    private String localCacheFolder;
    private Integer localCacheFolderSize;
    private Bitmap.CompressFormat imageFormat;
    private Integer imageQuality;
    private  Bitmap bitmap;

    public BitmapVO(){

    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public ImageView getImageView() {
        return imageView;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }

    public String getLocalCacheFolder() {
        return localCacheFolder;
    }

    public void setLocalCacheFolder(String localCacheFolder) {
        this.localCacheFolder = localCacheFolder;
    }

    public Integer getLocalCacheFolderSize() {
        return localCacheFolderSize;
    }

    public void setLocalCacheFolderSize(Integer localCacheFolderSize) {
        this.localCacheFolderSize = localCacheFolderSize;
    }


    public Bitmap.CompressFormat getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(Bitmap.CompressFormat imageFormat) {
        this.imageFormat = imageFormat;
    }

    public Integer getImageQuality() {
        return imageQuality;
    }

    public void setImageQuality(Integer imageQuality) {
        this.imageQuality = imageQuality;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}

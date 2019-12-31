package com.uav.autodebit.vo;

import java.io.Serializable;
import java.util.Map;

public class ConnectionVO implements Serializable {


    public final static int REQUEST_GET=0;
    public final static int REQUEST_POST=1;

    private String methodName;
    private Integer requestType;
    private Map<String, Object> params;
    private String searchKeyName;
    private String title;
    private String sharedPreferenceKey;
    private String entityTextKey;
    private String entityIdKey;

    private Boolean isLoaderAvoided;

    public ConnectionVO(){

    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }


    public Integer getRequestType() {
        return requestType;
    }

    public void setRequestType(Integer requestType) {
        this.requestType = requestType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getSearchKeyName() {
        return searchKeyName;
    }

    public void setSearchKeyName(String searchKeyName) {
        this.searchKeyName = searchKeyName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSharedPreferenceKey() {
        return sharedPreferenceKey;
    }

    public void setSharedPreferenceKey(String sharedPreferenceKey) {
        this.sharedPreferenceKey = sharedPreferenceKey;
    }

    public String getEntityTextKey() {
        return entityTextKey;
    }

    public void setEntityTextKey(String entityTextKey) {
        this.entityTextKey = entityTextKey;
    }

    public String getEntityIdKey() {
        return entityIdKey;
    }

    public void setEntityIdKey(String entityIdKey) {
        this.entityIdKey = entityIdKey;
    }

    public Boolean getLoaderAvoided() {
        return isLoaderAvoided;
    }

    public void setLoaderAvoided(Boolean loaderAvoided) {
        isLoaderAvoided = loaderAvoided;
    }
}

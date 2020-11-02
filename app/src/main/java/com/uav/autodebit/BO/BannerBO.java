package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class BannerBO implements Serializable {

    public static ConnectionVO getLocalCache() {

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getLocalCache");
        connectionVO.setLoaderAvoided(true);
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);

        return connectionVO;
    }


    public static ConnectionVO saveCustomerService() {

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveCustomerService");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getBannerExecutiveDetail() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getBannerExecutiveDetail");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO setAddCampaignResponse() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setAddCampaignResponse");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }




}

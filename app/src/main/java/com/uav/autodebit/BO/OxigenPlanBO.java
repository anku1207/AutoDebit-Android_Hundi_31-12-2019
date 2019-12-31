package com.uav.autodebit.BO;

import com.google.gson.Gson;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.OxigenPlanVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.io.Serializable;
import java.util.HashMap;

public class OxigenPlanBO implements Serializable {

    public static ConnectionVO getPlan() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getPlan");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO oxiMobileRecharge() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("oxiMobileRecharge");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO oxiDTHRechargeTransaction() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("oxiDTHRechargeTransaction");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

}

package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class DishTVBO implements Serializable {

    public static ConnectionVO getDishTvPlanDetail() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getDishTvPlanDetailForApp");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO getDishTvPostMandate() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getDishTvPostMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }



}

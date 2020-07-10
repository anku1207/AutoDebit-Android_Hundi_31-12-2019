package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class D2HBO implements Serializable {

    public static ConnectionVO getD2HPlanDetail() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getD2HPlanDetailForApp");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO mandateAmountOverrideByServiceId() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("mandateAmountOverrideByServiceId");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getD2HTvPostMandate() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getD2HTvPostMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

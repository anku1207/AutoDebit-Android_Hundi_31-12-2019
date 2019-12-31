package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class ServiceBO implements Serializable {

    public static ConnectionVO addBankForService() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("aadAdoptedService");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO setBankForService() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setBankForService");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


}
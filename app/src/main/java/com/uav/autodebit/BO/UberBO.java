package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class UberBO implements Serializable {


    public static ConnectionVO saveUberCustomerDetails() {

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveUberCustomerDetails");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getUberDetails() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getUberDetails");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

}

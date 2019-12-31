package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class MetroBO implements Serializable {

    public static ConnectionVO getDmrcCustomerList() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getMyDmrcCards");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO saveDmarcCards() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveDmarcCards");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

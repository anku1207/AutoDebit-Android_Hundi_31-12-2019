package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class UPIBO implements Serializable {
    public static ConnectionVO getAutopeUPIRecurringUrl() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getAutopeUPIRecurringUrl");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO proceedAutoPeUPIRecurringResponse() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("proceedAutoPeUPIRecurringResponse");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

public class ExceptionsBO {
    public static ConnectionVO sendErrorOnServer() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("sendErrorOnServer");
        connectionVO.setLoaderAvoided(true);
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

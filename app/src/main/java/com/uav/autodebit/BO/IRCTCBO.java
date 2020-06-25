package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class IRCTCBO implements Serializable {
    public static ConnectionVO getPaymentOpationForService() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getPaymentOpationForService");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

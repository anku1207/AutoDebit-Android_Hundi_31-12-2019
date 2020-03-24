package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;
import java.util.HashMap;

public class PaymentGateWayBO implements Serializable {

    public static ConnectionVO getPaymentGateWayUrl() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getPaymentGateWayUrl");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO validatePayUResponse() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("validatePayUResponse");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

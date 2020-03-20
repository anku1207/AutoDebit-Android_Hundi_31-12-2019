package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class PaymentGateWayBO implements Serializable {

    public static ConnectionVO getPaymentGateWayUrl() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getPaymentGateWayUrl");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

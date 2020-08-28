package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class TextReadBO implements Serializable {


    public static ConnectionVO verifyExistingCardNumber() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("verifyExistingCardNumber");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

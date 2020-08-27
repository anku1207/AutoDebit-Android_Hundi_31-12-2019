package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class TextReadBO implements Serializable {


    public static ConnectionVO getDmrcCardNumberByImageText() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getDmrcCardNumberByImageText");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

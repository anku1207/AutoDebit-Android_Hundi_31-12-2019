package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class ContactUsBO  implements Serializable {

    public static ConnectionVO saveContactRequest() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveContactRequest");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

}

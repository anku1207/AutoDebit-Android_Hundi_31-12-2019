package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class NotificationBO implements Serializable {
    public static ConnectionVO getNotificationByCNId() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getNotificationByCNId");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

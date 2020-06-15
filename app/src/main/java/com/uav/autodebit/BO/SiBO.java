package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class SiBO implements Serializable {

    public static ConnectionVO getSIMandateProperties() {
        ConnectionVO connectionVO = new ConnectionVO();
        /*connectionVO.setMethodName("getSIMandateProperties");*/
        connectionVO.setMethodName("getCCAvenueUrl");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO validateSIMandate() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("validateSIMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO proceedCCAvenueResponse() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("proceedCCAvenueResponse");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO proceedAutoPePgResponse() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("proceedAutoPePgResponse");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO sIMandateDmrc() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("sIMandateDmrc");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }





}

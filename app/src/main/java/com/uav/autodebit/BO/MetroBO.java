package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class MetroBO implements Serializable {

    public static ConnectionVO getDmrcCustomerList() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getMyDmrcCards");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO saveDmarcExitingCards() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveDmarcExitingCards");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }




    public static ConnectionVO saveDmarcCards() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveDmarcCards");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO allotDmrcCard(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("allotDmrcCard");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO dmrcCustomerCardSecurityDeposti(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("dmrcCustomerCardSecurityDeposti");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO dmrcCardTypes(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("dmrcCardTypes");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getCardTypeList(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getCardTypeList");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


}

package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;
import java.util.HashMap;

public class MandateBO implements Serializable {


    public static ConnectionVO enachMandate() {

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("enachMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);

        return connectionVO;
    }

    public static ConnectionVO enachBankList() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("enachBankList");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

   /* public static ConnectionVO enachBankList(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("enachBankList");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setSearchKeyName("name");
        connectionVO.setTitle("Customer");

        return connectionVO;

    }*/

    public static ConnectionVO setEnachMandateId(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setEnachMandateId");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;

    }



}

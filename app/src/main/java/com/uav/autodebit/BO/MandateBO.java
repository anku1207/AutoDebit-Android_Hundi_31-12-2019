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

    public static ConnectionVO getBankListAndAccountType(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getBankListAndAccountType");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;

    }

    public static ConnectionVO setEnachMandateId(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setEnachMandateId");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;

    }

    public static ConnectionVO checkValidateAdoptedServies(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("checkValidateAdoptedServies");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO setBankForServiceList(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setBankForServiceList");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getMandateRevokeDetail(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getMandateRevokeDetail");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO revokeMandate(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("revokeMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getServiceMandateList(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getServiceMandateList");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }





}

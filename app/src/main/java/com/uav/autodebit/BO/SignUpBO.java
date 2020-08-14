package com.uav.autodebit.BO;

import com.google.gson.Gson;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OTPVO;

import java.io.Serializable;
import java.util.HashMap;

public class SignUpBO implements Serializable {

    public static ConnectionVO signuser() {

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("customerSignUp");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO signotpverify() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("verifySignUp");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO resendOTP() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("resendOTP");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO signInOTP() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("signInViaOTP");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO setCustomerPassword() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO loginViaPassword(String loginid,String pass,String Type,String tokenId,String hashValue ) {
        HashMap<String, Object> params = new HashMap<String, Object>();

        CustomerVO customerVO=new CustomerVO();
        if(Type.equals("mobile")){
            customerVO.setMobileNumber(loginid);
        }else if(Type.equals("email")){
            customerVO.setEmailId(loginid);
        }

        customerVO.setAppHash(hashValue);
        customerVO.setTokenId(tokenId);
        customerVO.setPassword(pass);
        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);


        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("loginViaPassword");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setParams(params);
        return connectionVO;
    }
    public static ConnectionVO forgotPasswordOTP() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("forgotPasswordOTP");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
    public static ConnectionVO loginByFigerprint(String loginid ,String Type,String tokenId ) {

        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = new ConnectionVO();
        CustomerVO customerVO=new CustomerVO();
        if(Type.equals("mobile")){
            customerVO.setMobileNumber(loginid);
        }else if(Type.equals("email")){
            customerVO.setEmailId(loginid);
        }
        customerVO.setTokenId(tokenId);
        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setMethodName("loginByFigerprint");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setParams(params);
        return connectionVO;
    }
}
package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;
import java.util.HashMap;

public class PinCodeBO implements Serializable {


    public static ConnectionVO getCityByPincode(String pincode) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pincode", pincode);
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getCityByPincode");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setParams(params);

        return connectionVO;
    }

    public static ConnectionVO getCityByPincodeForDMRC(String pincode) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("pincode", pincode);
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getCityByPincodeForDMRC");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setParams(params);

        return connectionVO;
    }




}
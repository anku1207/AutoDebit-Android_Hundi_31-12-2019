package com.uav.autodebit.BO;

import com.google.gson.Gson;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;

import java.io.Serializable;
import java.util.HashMap;

public class PanCardBO implements Serializable {

    public static ConnectionVO panCardVerification() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("panCardVerification");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getCreditScore(Integer id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(id);
        Gson gson=new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getCreditScore");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setParams(params);
        return connectionVO;
    }


    public static ConnectionVO updatedetails() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("updatePanInfromationCreditReport");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }
}

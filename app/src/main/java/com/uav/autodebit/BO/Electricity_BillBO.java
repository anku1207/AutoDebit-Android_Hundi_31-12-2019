package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class Electricity_BillBO implements Serializable {


    public static ConnectionVO oxiFetchBill() {

        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("oxiBillFetch");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);

        return connectionVO;
    }



}

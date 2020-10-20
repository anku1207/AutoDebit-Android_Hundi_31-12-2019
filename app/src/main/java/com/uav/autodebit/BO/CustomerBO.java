package com.uav.autodebit.BO;

import com.uav.autodebit.vo.ConnectionVO;

import java.io.Serializable;

public class CustomerBO implements Serializable {

    public static ConnectionVO getProfileData() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getProfileData");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO setCustomerProfileImage() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setCustomerProfileImage");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO cutomerBankDetails() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("cutomerBankDetails");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }



    public static ConnectionVO setCustomerBucket() {
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("setCustomerBucket");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getCustomerhistory(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getCustomerhistory");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getHistorySumarryById(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getHistorySumarryById");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO saveAdditionalService(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("saveAdditionalService");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getServiceOperatorList(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getMandateServiceListByCustomerIdOrServiceTypeId");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO removeProfileImage(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("removeProfileImage");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }

    public static ConnectionVO getCustomerNotification(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("getCustomerNotification");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


    public static ConnectionVO updateMandateRevoked(){
        ConnectionVO connectionVO = new ConnectionVO();
        connectionVO.setMethodName("revokeMandate");
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        return connectionVO;
    }


}

package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CheckMandateAndShowDialog {
  public static void allotBnakForService(Context context, Integer serviceId, Integer bankId , VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.setBankForService();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        customerVO.setServiceId(serviceId);
        customerVO.setAnonymousInteger(bankId);
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("setBankForService",params.toString());
        VolleyUtils.makeJsonObjectRequest(context,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);

                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,"Alert",sb.toString(),false);
                }else {
                    //set session customer or local cache
                    String json = new Gson().toJson(customerVO);
                    Session.set_Data_Sharedprefence(context,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(context, Session.LOCAL_CACHE,customerVO.getLocalCache());

                    volleyResponse.onSuccess(customerVO);

                }
            }
        });
    }
}

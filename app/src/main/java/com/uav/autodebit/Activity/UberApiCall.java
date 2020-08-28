package com.uav.autodebit.Activity;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.uav.autodebit.BO.UberBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.UberVO;
import com.uav.autodebit.vo.UberVoucherVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UberApiCall {

    static void CheckUberVaucherByCustomerId(Context context , VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = UberBO.CheckUberVaucherByCustomerId();

        UberVoucherVO uberVoucherVO =new UberVoucherVO();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        uberVoucherVO.setCustomer(customerVO);

        String json = new Gson().toJson(uberVoucherVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("CheckUberVaucherBy",params.toString());
        VolleyUtils.makeJsonObjectRequest(context,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                UberVoucherVO uberVoucherVO= gson.fromJson(response.toString(), UberVoucherVO.class);

                if(uberVoucherVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) uberVoucherVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,uberVoucherVO.getDialogTitle(),sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(uberVoucherVO);
                }
            }
        });
    }




    static void getUberVoucher(Context context , VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = UberBO.getUberVoucher();

        UberVoucherVO uberVoucherVO =new UberVoucherVO();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        uberVoucherVO.setCustomer(customerVO);

        String json = new Gson().toJson(uberVoucherVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("getUberVoucher",params.toString());
        VolleyUtils.makeJsonObjectRequest(context,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                UberVoucherVO uberVoucherVO= gson.fromJson(response.toString(), UberVoucherVO.class);

                if(uberVoucherVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) uberVoucherVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,uberVoucherVO.getDialogTitle(),sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(uberVoucherVO);
                }
            }
        });
    }
}

package com.uav.autodebit.DMRC;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.uav.autodebit.Activity.Dmrc_Card_Request;
import com.uav.autodebit.BO.PinCodeBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DMRCApi {
    public static void pinCodebycity(Context context, String pincode, VolleyResponse volleyResponse){
        VolleyUtils.makeJsonObjectRequest(context, PinCodeBO.getCityByPincode(pincode), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CityVO cityVO = gson.fromJson(response.toString(), CityVO.class);
                if(cityVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) cityVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    volleyResponse.onError(sb.toString());
                }else {

                    volleyResponse.onSuccess(cityVO);
                }
            }
        });
    }

    public static void getCityByPincodeForDMRC(Context context, String pincode, VolleyResponse volleyResponse){
        VolleyUtils.makeJsonObjectRequest(context, PinCodeBO.getCityByPincodeForDMRC(pincode), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CityVO cityVO = gson.fromJson(response.toString(), CityVO.class);
                if(cityVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) cityVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    volleyResponse.onError(sb.toString());
                }else {

                    volleyResponse.onSuccess(cityVO);
                }
            }
        });

    }
}

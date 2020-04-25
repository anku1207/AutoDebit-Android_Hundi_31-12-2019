package com.uav.autodebit.exceptions;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;


import com.google.gson.Gson;
import com.uav.autodebit.Activity.Notifications;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.BO.ExceptionsBO;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AppErrorVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerNotificationVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ExceptionsNotification {


    public static void ExceptionHandling(Context context, String e){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = ExceptionsBO.sendErrorOnServer();

            AppErrorVO appErrorVO =new AppErrorVO();
            appErrorVO.setCustomerId(Session.getCustomerIdOnExceptionTime(context));
            appErrorVO.setMobileName(Build.BRAND);
            appErrorVO.setAppVersion(Utility.getVersioncode(context).toString());
            appErrorVO.setErrorMessage(e);
            appErrorVO.setMobileVersion(String.valueOf(Build.VERSION.SDK_INT));
            appErrorVO.setDeviceInfo(Utility.getDeviceDetail().toString());

            Gson gson =new Gson();
            String json = gson.toJson(appErrorVO);
            Log.w("request",json);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    if(response.get("status").equals("fail")){

                    }else {

                    }

                }
            });
        }catch (Exception e1){
            Toast.makeText(context, ""+e1.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

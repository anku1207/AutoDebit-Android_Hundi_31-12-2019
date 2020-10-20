package com.uav.autodebit.Activity;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.uav.autodebit.BO.BannerBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BannerWebviewAPI {
    public static void getBannerClickDetails(Context context, int bannerId, VolleyResponse volleyResponse){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = BannerBO.getBannerExecutiveDetail();
            BannerVO bannerVO =new BannerVO();
            bannerVO.setBannerId(bannerId);
            bannerVO.setAnonymousInteger(Integer.parseInt(Session.getCustomerId(context)));
            Gson gson =new Gson();
            String json = gson.toJson(bannerVO);
            params.put("volley", json);
            Log.w("getBannerClickDetails",json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    BannerVO bannerVOresp = gson.fromJson(response.toString(), BannerVO.class);
                    if(bannerVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) bannerVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        //pd.dismiss();
                        Utility.showSingleButtonDialog(context,bannerVOresp.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(bannerVOresp);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
}

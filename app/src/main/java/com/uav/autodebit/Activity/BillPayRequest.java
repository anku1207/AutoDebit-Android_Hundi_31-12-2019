package com.uav.autodebit.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class BillPayRequest {




    public static void proceedFetchBill(OxigenTransactionVO oxigenTransactionVO,Context context,VolleyResponse volleyResponse) throws Exception{

        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = Electricity_BillBO.oxiFetchBill();

            params.put("volley",gson.toJson(oxigenTransactionVO));

            Log.w("proceedFetchBill",gson.toJson(oxigenTransactionVO));
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Log.w("respele",response.toString());

                    OxigenTransactionVO oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                    if(oxigenTransactionVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) oxigenTransactionVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,"Error !",sb.toString(),false);
                        volleyResponse.onError(null);
                    }else if(oxigenTransactionVOresp.getStatusCode().equals("01")){
                        Utility.showSingleButtonDialogconfirmation(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                        }),"Alert",oxigenTransactionVOresp.getAnonymousString());
                        volleyResponse.onError(null);
                    }else {
                        volleyResponse.onSuccess(oxigenTransactionVOresp);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    public static void proceedBillPayment(OxigenTransactionVO oxigenTransactionVO, Context context, VolleyResponse volleyResponse) {

        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();

            ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
            serviceTypeVO.setServiceTypeId(ApplicationConstant.MobilePostpaid);

            CustomerVO customerVO =new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));
            ConnectionVO connectionVO = OxigenPlanBO.oxiBillPayment();
            oxigenTransactionVO.setCustomer(customerVO);
            oxigenTransactionVO.setServiceType(serviceTypeVO);
            params.put("volley",gson.toJson(oxigenTransactionVO));
            connectionVO.setParams(params);

            Log.w("requestData",gson.toJson(oxigenTransactionVO));

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;

                    OxigenTransactionVO oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                    if(oxigenTransactionVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) oxigenTransactionVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,"Error !",sb.toString(),false);
                        volleyResponse.onError(null);
                    }else {
                       volleyResponse.onSuccess(oxigenTransactionVOresp);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

}

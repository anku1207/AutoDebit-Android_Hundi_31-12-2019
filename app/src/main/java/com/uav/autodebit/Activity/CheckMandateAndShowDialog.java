package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class CheckMandateAndShowDialog {

    public static void oxiServiceMandateCheck(Context context,Integer serivceId ,int providerId ,Integer actionId,VolleyResponse volleyResponse) {
        // isrecharge is true is before recharge  and fleas alter recharge
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = OxigenPlanBO.oxiServiceMandateCheck();
        OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        oxigenTransactionVO.setCustomer(customerVO);

        ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
        serviceTypeVO.setServiceTypeId(serivceId);
        oxigenTransactionVO.setServiceType(serviceTypeVO);

        AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(providerId);
        oxigenTransactionVO.setProvider(authServiceProviderVO);
        oxigenTransactionVO.setAnonymousInteger(actionId);

        Gson gson=new Gson();
        String json = gson.toJson(oxigenTransactionVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("oxiServiceMandateCheck",params.toString());

        VolleyUtils.makeJsonObjectRequest(context, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {

            }
            @Override
            public void onResponse(Object resp) throws JSONException {

                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                OxigenTransactionVO checkMandateResponse = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if (checkMandateResponse.getStatusCode().equals("400")) {
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < checkMandateResponse.getErrorMsgs().size(); i++) {
                        stringBuffer.append(checkMandateResponse.getErrorMsgs().get(i));
                    }
                    Utility.showSingleButtonDialog(context, checkMandateResponse.getDialogTitle(), stringBuffer.toString(), false);
                } else {
                    volleyResponse.onSuccess(checkMandateResponse);
                }
            }
        });
    }



    public static void setManuallyServiceSchedule(Context context ,OxigenTransactionVO oxigenTransactionVO,JSONObject alertDialogDate, VolleyResponse volleyResponse) throws Exception {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.setBankForServiceManuallyService();
        OxigenTransactionVO oxigenTransactionVORequ=new OxigenTransactionVO();

        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        oxigenTransactionVORequ.setAnonymounsDay(Integer.parseInt(alertDialogDate.getString("day")));
        oxigenTransactionVORequ.setAnonymousAmount(Double.parseDouble(alertDialogDate.getString("amount")));
        oxigenTransactionVORequ.setTypeId(oxigenTransactionVO.getTypeId());
        oxigenTransactionVORequ.setCustomer(customerVO);

        Gson gson =new Gson();
        String json = gson.toJson(oxigenTransactionVORequ);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("request",params.toString());
        VolleyUtils.makeJsonObjectRequest(context,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                OxigenTransactionVO transactionVO = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if(transactionVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) transactionVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,transactionVO.getDialogTitle(),sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(transactionVO);
                }
            }
        });

    }



    public static void beforeRechargeMandateSchedule(Context context ,OxigenTransactionVO oxigenTransactionVO,JSONObject alertDialogDate, VolleyResponse volleyResponse) throws Exception {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.beforeRechargeMandateSchedule();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        customerVO.setServiceId(oxigenTransactionVO.getServiceId());
        customerVO.setAnonymounsDay(Integer.parseInt(alertDialogDate.getString("day")));
        customerVO.setAnonymousAmount(Double.parseDouble(alertDialogDate.getString("amount")));
        customerVO.setAnonymousInteger(oxigenTransactionVO.getTypeId());

        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("request",params.toString());
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
                    Utility.showSingleButtonDialog(context,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(customerVO);
                }
            }
        });
    }


    public static void checkMandateforService(Context context ,OxigenTransactionVO oxigenTransactionVO, VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.checkMandateforService();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        customerVO.setServiceId(oxigenTransactionVO.getServiceId());

        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("checkMandateforService",params.toString());
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
                    Utility.showSingleButtonDialog(context,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(customerVO);
                }
            }
        });

    }

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
                    Utility.showSingleButtonDialog(context,customerVO.getDialogTitle(),sb.toString(),false);
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


    public static void showMandateSchedulerBeforeRecharge(Context context ,String htmlUrl ,VolleyResponse volleyResponse ){
        // ask to customer bank mandate after recharge
        MyDialog.showWebviewConditionalAlertDialog(context,htmlUrl,true,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
            //((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
            HashMap<String,Object> objectHashMap = (HashMap<String, Object>) ok;
            //add Dialog object in list
            GlobalApplication.dialog_List.add(((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))));
            //((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))).dismiss();
            try {
                JSONObject alertDialogDate =new JSONObject((String) objectHashMap.get("data"));
                volleyResponse.onSuccess(alertDialogDate);
            }catch (Exception e){
                ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
            }
        },(ConfirmationGetObjet.OnCancel)(cancel)->{
            Utility.dismissDialog(context,((Dialog)cancel));
            volleyResponse.onSuccess(null);

        }));
    }




    public static void afterRechargeGetRechargeDetails(Context context  ,Integer historyId , String customerServiceOperator,
                                                       VolleyResponse volleyResponse ){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.afterRechargeGetRechargeDetails();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        customerVO.setCustoemrHistoryId(historyId);
        customerVO.setAnonymousInteger(Integer.parseInt(customerServiceOperator));
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("After Recharge",params.toString());
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
                    Utility.showSingleButtonDialog(context,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(customerVO);
                }
            }
        });
    }
}

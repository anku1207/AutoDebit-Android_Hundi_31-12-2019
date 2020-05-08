package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class CheckMandateAndShowDialog {

    public static void oxiServiceMandateCheck(Context context,Integer serivceId ,boolean isRecharge ,OxigenTransactionVO oxigenValidateResponce) {
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

        Gson gson=new Gson();
        String json = gson.toJson(oxigenTransactionVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("request",params.toString());

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
                    Utility.showSingleButtonDialog(context, "Error !", stringBuffer.toString(), false);
                } else if(checkMandateResponse.getStatusCode().equals("ap102")){
                    // 12/04/2020
                    if(isRecharge){
                        ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serivceId))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                    }else{
                        ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serivceId))), ApplicationConstant.REQ_ENACH_MANDATE);
                    }
                }else if(checkMandateResponse.getStatusCode().equals("ap103")) {
                    // 12/04/2020
                    String[] buttons = {"New Bank", "Existing Bank"};
                    Utility.showDoubleButtonDialogConfirmation(new com.uav.autodebit.util.DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            try {
                                JSONArray arryjson = new JSONArray(checkMandateResponse.getAnonymousString());
                                ArrayList<CustomerAuthServiceVO> customerAuthServiceArry = new ArrayList<>();
                                for (int i = 0; i < arryjson.length(); i++) {
                                    JSONObject jsonObject = arryjson.getJSONObject(i);
                                    CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                                    customerAuthServiceVO.setBankName(jsonObject.getString("bankName"));
                                    customerAuthServiceVO.setProviderTokenId(jsonObject.getString("mandateId"));
                                    customerAuthServiceVO.setCustomerAuthId(jsonObject.getInt("id"));
                                    customerAuthServiceVO.setAnonymousString(jsonObject.getString("status"));
                                    customerAuthServiceArry.add(customerAuthServiceVO);
                                }
                                CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                                customerAuthServiceVO.setBankName(null);
                                customerAuthServiceVO.setProviderTokenId("Add New Mandate");
                                customerAuthServiceVO.setCustomerAuthId(0);
                                customerAuthServiceVO.setAnonymousString(null);
                                customerAuthServiceArry.add(customerAuthServiceVO);

                                Utility.alertselectdialog(context, "Choose from existing Bank", customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (bankId) -> {
                                    if (!bankId.equals("0")) {
                                        CheckMandateAndShowDialog.allotBnakForService(context,serivceId,Integer.parseInt(bankId),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                                            if(isRecharge){
                                                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                                                authServiceProviderVO.setProviderId(AuthServiceProviderVO.ENACHIDFC);
                                                OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
                                                responseOxigenTransactionVO.setTypeId(oxigenValidateResponce.getTypeId());
                                                responseOxigenTransactionVO.setAnonymousString(bankId);
                                                responseOxigenTransactionVO.setProvider(authServiceProviderVO);
                                                //recharge for after enach mandate
                                                BillPayRequest.proceedBillPayment(responseOxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                                                    BillPayRequest.handelRechargeSuccess(context,(OxigenTransactionVO)s);
                                                },(VolleyResponse.OnError)(e)->{
                                                }));
                                            }else {
                                                // move to history activity
                                                ((Activity)context).startActivity(new Intent(context,HistorySummary.class).putExtra("historyId",oxigenValidateResponce.getAnonymousInteger().toString()));
                                                ((Activity)context).finish();
                                            }
                                        }));
                                    } else {
                                        if(isRecharge){
                                            ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serivceId))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                                        }else {
                                            ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serivceId))), ApplicationConstant.REQ_ENACH_MANDATE);
                                        }
                                    }
                                }));
                            } catch (Exception e) {
                                e.printStackTrace();
                                Utility.exceptionAlertDialog(context, "Alert!", "Something went wrong, Please try again!", "Report", Utility.getStackTrace(e));
                            }
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                            if(isRecharge){
                                ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serivceId))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                            }else {
                                ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serivceId))), ApplicationConstant.REQ_ENACH_MANDATE);
                            }
                        }
                    }, context, checkMandateResponse.getErrorMsgs().get(0), "", buttons);
                }
            }
        });
    }



    public static void setManuallyServiceSchedule(Context context ,OxigenTransactionVO oxigenTransactionVO,JSONObject alertDialogDate, VolleyResponse volleyResponse) throws Exception {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.setBankForServiceManuallyService();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
        customerVO.setServiceId(oxigenTransactionVO.getServiceId());
        customerVO.setAnonymounsDay(Integer.parseInt(alertDialogDate.getString("day")));
        customerVO.setAnonymousAmount(Double.parseDouble(alertDialogDate.getString("amount")));
        customerVO.setCustoemrHistoryId(oxigenTransactionVO.getAnonymousInteger());

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
                    Utility.showSingleButtonDialog(context,"Alert",sb.toString(),false);
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

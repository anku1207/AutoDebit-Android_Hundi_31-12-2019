package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.content.res.ResourcesCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.PaymentGatewayResponse;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

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

    public static void proceedBillPayment(OxigenTransactionVO oxigenTransactionVO, Context context,VolleyResponse volleyResponse) {

        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = OxigenPlanBO.oxiBillPayment();
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


    static JSONObject getQuestionLabelData(Activity activity, EditText operator, EditText amount, boolean fetchBill, boolean isFetchBill, List<OxigenQuestionsVO> questionsVOS,int minamt) throws  JSONException {

        amount.setError(null);
        operator.setError(null);

        boolean valid=true;

        if(operator.getText().toString().equals("")){
            operator.setError("this filed is required");
            valid=false;
        }
        JSONObject jsonObject =new JSONObject();
        for(OxigenQuestionsVO oxigenQuestionsVO:questionsVOS){
            EditText editText =(EditText) activity.findViewById(oxigenQuestionsVO.getElementId());
            editText.clearFocus();
            editText.setError(null);
            if(editText.getText().toString().equals("")){
                editText.setError(  Utility.getErrorSpannableStringDynamicEditText(activity, "this field is required"));
                valid=false;
            }else if(oxigenQuestionsVO.getMinLength()!=null && (editText.getText().toString().length() < Integer.parseInt(oxigenQuestionsVO.getMinLength()))){
                editText.setError(oxigenQuestionsVO.getMinLength());
                valid=false;
            }else if(oxigenQuestionsVO.getMaxLength()!=null && (editText.getText().toString().length() > Integer.parseInt(oxigenQuestionsVO.getMaxLength()))){
                editText.setError(oxigenQuestionsVO.getMaxLength());
                valid=false;
            }
            jsonObject.put(oxigenQuestionsVO.getQuestionLabel(),editText.getText().toString());
            //oxigenQuestionsVO.getJsonKey();
            //editText.getText().toString();
        }
        if(fetchBill && !isFetchBill && valid){
            if(amount.getText().toString().equals("")){
                amount.setError("this filed is required");
                valid=false;
            }else if(!amount.getText().toString().equals("") && Integer.parseInt(amount.getText().toString())<minamt){
                amount.setError("Amount Must be greater then " +activity.getString(R.string.Rs)+" "+minamt);
                valid=false;
            }
        }else if(fetchBill && isFetchBill && valid){
            if(amount.getText().toString().equals("")){
                Utility.showSingleButtonDialogconfirmation(activity,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) Dialog::dismiss),"Alert","Bill Amount is null ");
                valid=false;
            }else if(!amount.getText().toString().equals("") && Integer.parseInt(amount.getText().toString())<minamt){
                Utility.showSingleButtonDialogconfirmation(activity,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) Dialog::dismiss),"Alert","Amount Must be greater then " +activity.getString(R.string.Rs)+" "+minamt);
                valid=false;
            }
        }
        return valid ?jsonObject:null;
    }




    static  void confirmationDialogBillPay(Context context, EditText operator, EditText amount , JSONObject dataarray,ConfirmationDialogInterface confirmationDialogInterface){
        try {

                String btn[]={"Cancel","Ok"};
                JSONArray jsonArray =new JSONArray();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key","Operator");
                jsonObject.put("value",operator.getText().toString());
                jsonArray.put(jsonObject);


                jsonObject = new JSONObject();
                jsonObject.put("key","Amount");
                jsonObject.put("value",amount.getText().toString());
                jsonArray.put(jsonObject);

                Iterator<String> keys= dataarray.keys();
                while (keys.hasNext()){
                    String keyValue = (String)keys.next();
                    String valueString = dataarray.getString(keyValue);
                    jsonObject = new JSONObject();
                    jsonObject.put("key",keyValue);
                    jsonObject.put("value",valueString);
                    jsonArray.put(jsonObject);
                }
                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                        try {
                            confirmationDialogInterface.onOk(null);
                        }catch (Exception e){
                            e.printStackTrace();
                            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                        }
                    }
                    @Override
                    public void modify(Dialog dialog) {
                        dialog.dismiss();
                    }
                },context,jsonArray,null,"Confirmation",btn);

        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }
    }

    static   void proceedRecharge(Context context,boolean isFetchBill,OxigenTransactionVO oxigenTransactionVO ,int serviceId){
        if(oxigenTransactionVO==null){
            Utility.showSingleButtonDialogconfirmation(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ok.dismiss();
            }),"Alert","Empty Filed not Allow!");
        }else if(isFetchBill && oxigenTransactionVO.getTypeId()==null){
            Utility.showSingleButtonDialogconfirmation(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ok.dismiss();
            }),"Alert","Bill Fetch Is required!");
        }else {
           BillPayRequest.oxiBillPaymentValdated(context,oxigenTransactionVO,new PaymentGatewayResponse((PaymentGatewayResponse.OnPg)(pg)->{
               ((Activity)context).startActivityForResult(new Intent(context,PaymentGateWay.class).putExtra("oxigenTypeId",((OxigenTransactionVO)pg).getTypeId().toString()),200);
           },(PaymentGatewayResponse.OnEnach)(onEnach)->{
               BillPayRequest.proceedBillPayment((OxigenTransactionVO)onEnach,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                   ((Activity)context).startActivity(new Intent(context,History.class));
                   ((Activity)context).finish();
               },(VolleyResponse.OnError)(e)->{
               }));
           }));
        }
    }


    private static void oxiBillPaymentValdated(Context context, OxigenTransactionVO oxigenTransactionVO, PaymentGatewayResponse paymentGatewayResponse) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =OxigenPlanBO.oxiBillPaymentValdated();

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
                OxigenTransactionVO oxigenPlanresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if(oxigenPlanresp.getStatusCode().equals("400")){
                    StringBuffer stringBuffer= new StringBuffer();
                    for(int i=0;i<oxigenPlanresp.getErrorMsgs().size();i++){
                        stringBuffer.append(oxigenPlanresp.getErrorMsgs().get(i));
                    }
                    Utility.showSingleButtonDialog(context,"Error !",stringBuffer.toString(),false);
                }else {
                    if(oxigenPlanresp.getTypeId()==null){
                        Utility.showSingleButtonDialog(context,"Error !","Something went wrong, Please try again!",false);
                        return;
                    }


                    if(oxigenPlanresp.isEventIs()){

                        Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                            @Override
                            public void confirm(Dialog dialog) {
                                dialog.dismiss();

                                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                                authServiceProviderVO.setProviderId(AuthServiceProviderVO.ENACHIDFC);

                                OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
                                responseOxigenTransactionVO.setTypeId(oxigenPlanresp.getTypeId());
                                responseOxigenTransactionVO.setAnonymousString(oxigenPlanresp.getAnonymousInteger().toString());
                                responseOxigenTransactionVO.setProvider(authServiceProviderVO);
                                paymentGatewayResponse.onPg(responseOxigenTransactionVO);
                            }
                            @Override
                            public void modify(Dialog dialog) {
                                dialog.dismiss();
                                paymentGatewayResponse.onEnach(oxigenPlanresp);
                            }
                        },context,oxigenPlanresp.getAnonymousString(),"");
                    }else {
                        paymentGatewayResponse.onEnach(oxigenPlanresp);

                    }
                }
            }
        });
    }


    public static void onActivityResult(Context context,OxigenTransactionVO oxigenTransactionVO){
        BillPayRequest.proceedBillPayment(oxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            ((Activity)context).startActivity(new Intent(context,History.class));
            ((Activity)context).finish();
        },(VolleyResponse.OnError)(e)->{
        }));
    }





}

package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.CustomDialog.BeforeRecharge;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.MandateAndRechargeInterface;
import com.uav.autodebit.Interface.PaymentGatewayResponse;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.CCTransactionStatusVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class BillPayRequest {

    private static OxigenTransactionVO oxigenValidateResponce;

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
                        Utility.showSingleButtonDialog(context,oxigenTransactionVOresp.getDialogTitle(),sb.toString(),false);
                        volleyResponse.onError(null);
                    }else if(oxigenTransactionVOresp.getStatusCode().equals("01")){
                        String btn[]={"Cancel","Ok"};
                        JSONArray jsonArray =new JSONArray();

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("key","Operator");
                        jsonObject.put("value",oxigenTransactionVO.getOperateName());
                        jsonArray.put(jsonObject);

                        JSONObject dataJson=new JSONObject(oxigenTransactionVO.getAnonymousString());

                        Iterator<String> keys = dataJson.keys();

                        while(keys.hasNext()) {
                            String key = keys.next();
                            jsonObject = new JSONObject();
                            jsonObject.put("key",key);
                            jsonObject.put("value",dataJson.getString(key));
                            jsonArray.put(jsonObject);
                        }

                        Utility.confirmationDialog(new DialogInterface() {
                            @Override
                            public void confirm(Dialog dialog) {
                                dialog.dismiss();
                                try {
                                    Utility.showSelectPaymentTypeDialog(context,"Payment Type",oxigenTransactionVOresp.getPaymentTypeObject(),new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess)(position)->{
                                        int selectPosition=Integer.parseInt(position);
                                        if(selectPosition== ApplicationConstant.BankMandatePayment ){
                                            //bank
                                            showBankMandateOrSiMandateInfo(context,oxigenTransactionVOresp.getBankMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                                oxigenTransactionVOresp.setProvider(getAuthServiceProvider(AuthServiceProviderVO.ENACHIDFC));
                                                BillFetchFailAddMandate(context,oxigenTransactionVOresp);
                                            }));
                                        }else if(selectPosition==ApplicationConstant.SIMandatePayment){
                                            showBankMandateOrSiMandateInfo(context,oxigenTransactionVOresp.getSiMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                                oxigenTransactionVOresp.setProvider(getAuthServiceProvider(AuthServiceProviderVO.AUTOPE_PG));
                                                BillFetchFailAddMandate(context,oxigenTransactionVOresp);
                                            }));
                                        }
                                    }));
                                }catch (Exception e){
                                    ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                                }
                            }
                            @Override
                            public void modify(Dialog dialog) {
                                dialog.dismiss();
                            }
                        },context,jsonArray,null,"Do you want to proceed ?",btn);

                        volleyResponse.onError(null);
                    }else {
                        volleyResponse.onSuccess(oxigenTransactionVOresp);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
    static void proceedBillPayment(OxigenTransactionVO oxigenTransactionVO, Context context, VolleyResponse volleyResponse) {
        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = OxigenPlanBO.oxiBillPayment();
            params.put("volley",gson.toJson(oxigenTransactionVO));
            connectionVO.setParams(params);
            Log.w("proceedBillPayment",gson.toJson(oxigenTransactionVO));
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
                        Utility.showSingleButtonDialog(context,oxigenTransactionVOresp.getDialogTitle(),sb.toString(),false);
                        volleyResponse.onError(null);
                    }else {
                       volleyResponse.onSuccess(oxigenTransactionVOresp);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }


    // fetchbill key is showing bill fetch bill is required or not required and isFetchBill key is showing use bill fetch or not fetch
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
            }else if(!amount.getText().toString().equals("") && Double.parseDouble(amount.getText().toString())<minamt){
                amount.setError("Amount Must be greater then " +activity.getString(R.string.Rs)+" "+minamt);
                valid=false;
            }
        }else if(fetchBill && isFetchBill && valid){
            if(amount.getText().toString().equals("")){
                Utility.showSingleButtonDialogconfirmation(activity,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) Dialog::dismiss),"Alert","Bill Amount is null ");
                valid=false;
            }else if(!amount.getText().toString().equals("") && (Double.parseDouble(amount.getText().toString()))<minamt){
                Utility.showSingleButtonDialogconfirmation(activity,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) Dialog::dismiss),"Alert","Amount Must be greater then " +activity.getString(R.string.Rs)+" "+minamt);
                valid=false;
            }
        }
        return valid ?jsonObject:null;
    }




    // fetchbill key is showing bill fetch bill is required or not required and isFetchBill key is showing use bill fetch or not fetch
    static JSONObject getNewTypeQuestionLabelData(Activity activity, EditText operator, String amount, boolean fetchBill, boolean isFetchBill, List<OxigenQuestionsVO> questionsVOS,int minamt) throws  JSONException {
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
        }
        if(fetchBill && valid){
            if(amount==null){
                Utility.showSingleButtonDialogconfirmation(activity,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) Dialog::dismiss),"Alert","Bill Amount is null ");
                valid=false;
            }else if(amount!=null && Double.parseDouble(amount)< minamt){
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
                            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                        }
                    }
                    @Override
                    public void modify(Dialog dialog) {
                        dialog.dismiss();
                    }
                },context,jsonArray,null,"Confirmation",btn);

        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));

        }
    }

    static   void proceedRecharge(Context context,boolean isFetchBill,OxigenTransactionVO oxigenTransactionVO ){
        oxigenValidateResponce=new OxigenTransactionVO();

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
               OxigenTransactionVO oxigenPGResp=(OxigenTransactionVO) pg;
               startSIActivity(context,oxigenPGResp,ApplicationConstant.PG_PAYMENT);
           },(PaymentGatewayResponse.OnEnach)(onEnach)->{
               OxigenTransactionVO oxigenPlanresp=(OxigenTransactionVO) onEnach;
               if(oxigenPlanresp.isEventIs()){
                   //if bank mandate is true
                   //proceedRechargeOnMandate(context,oxigenPlanresp);
               }else {
                   // 30/06/2020
                   //oxi bill validate tym  set provider id
                   setBankMandateOrRecharge(context,oxigenPlanresp);
               }
                 
           },(PaymentGatewayResponse.OnEnachScheduler)(scheduler)->{
               Toast.makeText(context, "Scheduler Next Recharge", Toast.LENGTH_SHORT).show();
           },(PaymentGatewayResponse.OnSiMandate)(siClickResp)->{
               OxigenTransactionVO oxigenPlanresp=(OxigenTransactionVO) siClickResp;
               // 30/06/2020
               //oxi bill validate tym  set provider id
               int serviceTypeId=oxigenPlanresp.getServiceId();
               if(serviceTypeId==ApplicationConstant.DTH || serviceTypeId==ApplicationConstant.DISHTV || serviceTypeId==ApplicationConstant.D2h){
                   oxigenPlanresp.setSiMandateType(ApplicationConstant.PG_MANDATE_AND_RECHARGE);
               }else {
                   oxigenPlanresp.setSiMandateType(ApplicationConstant.PG_PAYMENT);
               }
               setBankMandateOrRecharge(context,oxigenPlanresp);

           },(PaymentGatewayResponse.OnUPIMandate)(upimandate)->{
               OxigenTransactionVO upimandateOxigenresp=(OxigenTransactionVO) upimandate;
               upimandateOxigenresp.setSiMandateType(ApplicationConstant.PG_MANDATE);
               setBankMandateOrRecharge(context,upimandateOxigenresp);
           }));
        }
    }

    public static void setBankMandateOrRecharge(Context context , OxigenTransactionVO oxigenTransactionVO){
        BeforeRecharge.beforeRechargeAddMandate(context,oxigenTransactionVO,new MandateAndRechargeInterface
                ((MandateAndRechargeInterface.OnRecharge)(recharge)->{
            oxigenTransactionVO.setAnonymousInteger(Integer.parseInt((String) recharge));
            proceedRechargeOnMandate(context,oxigenTransactionVO);
        }, (MandateAndRechargeInterface.OnMandate)(mandate)->{
            if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                startSIActivity(context,oxigenTransactionVO,oxigenTransactionVO.getSiMandateType());
            }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                startBankMandateActivity(context,oxigenTransactionVO);
            }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG_UPI){
                startUPIActivity(context,oxigenTransactionVO,oxigenTransactionVO.getSiMandateType());
            }
        }));

    }



    public static AuthServiceProviderVO getAuthServiceProvider(int providerId){
        // 30/06/2020
        AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(providerId);
        return authServiceProviderVO;
    }

    public static void startBankMandateActivity(Context context , OxigenTransactionVO  oxigenTransactionVO){
        try {
            Intent intent = new Intent(context,Enach_Mandate.class);
            intent.putExtra("forresutl",true);
            intent.putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVO.getServiceId())));
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }

    public static void startSIActivity(Context context , OxigenTransactionVO  oxigenTransactionVO , String paymentType){
        try {
            Intent intent = new Intent(context,SI_First_Data.class);
            intent.putExtra("id",oxigenTransactionVO.getTypeId());
            intent.putExtra("amount",oxigenTransactionVO.getNetAmount());
            intent.putExtra("serviceId",oxigenTransactionVO.getServiceId()+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_MANDATE);
        }catch (Exception e ){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }

    public static void startUPIActivity(Context context , OxigenTransactionVO  oxigenTransactionVO , String paymentType){
        try {
            Intent intent = new Intent(context,UPI_Mandate.class);
            intent.putExtra("id",oxigenTransactionVO.getTypeId());
            intent.putExtra("amount",ApplicationConstant.SI_UPI_MANDATE_AMOUNT);
            intent.putExtra("serviceId",oxigenTransactionVO.getServiceId()+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_UPI_FOR_MANDATE);
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }

    }



    public static void BillFetchFailAddMandate(Context context , OxigenTransactionVO oxigenPlanresp){
        BeforeRecharge.beforeRechargeAddMandate(context,oxigenPlanresp,new MandateAndRechargeInterface((MandateAndRechargeInterface.OnRecharge)(recharge)->{
            oxigenPlanresp.setAnonymousInteger(Integer.parseInt((String) recharge));
            updateMandateAgainstOpeator(context,Integer.parseInt((String) recharge),oxigenPlanresp.getTypeId(),oxigenPlanresp.getProvider().getProviderId(),false);
        }, (MandateAndRechargeInterface.OnMandate)(mandate)->{
            failFetchBillDetailStartActivity(context,oxigenPlanresp);
        }));
    }

    public static void failFetchBillDetailStartActivity(Context context , OxigenTransactionVO oxigenTransactionVO){
        if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
            Intent intent = new Intent(context,SI_First_Data.class);
            intent.putExtra("id",oxigenTransactionVO.getTypeId());
            intent.putExtra("amount",oxigenTransactionVO.getNetAmount());
            intent.putExtra("serviceId",oxigenTransactionVO.getServiceId()+"");
            intent.putExtra("paymentType",ApplicationConstant.PG_MANDATE);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_FOR_BILL_FETCH_ERROR);
        }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
            Intent intent = new Intent(context,Enach_Mandate.class);
            intent.putExtra("selectservice",new ArrayList<Integer>(Arrays.asList(oxigenTransactionVO.getServiceId())));
            intent.putExtra("id", oxigenTransactionVO.getTypeId());
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_MANDATE_FOR_BILL_FETCH_ERROR);
        }
    }



    /*public static void beforeRechargeAddMandate(Context context , OxigenTransactionVO oxigenPlanresp){
        CheckMandateAndShowDialog.oxiServiceMandateCheck(context,oxigenPlanresp.getServiceId(),oxigenPlanresp.getProvider().getProviderId(),new VolleyResponse((VolleyResponse.OnSuccess)(mandatecheckresp)->{
            OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) mandatecheckresp;
            if(oxigenTransactionVO!=null){
                if(oxigenTransactionVO.getStatusCode().equals("ap102")) {
                    // 12/04/2020
                    MyDialog.showWebviewAlertDialog(context, oxigenTransactionVO.getHtmlString(),true,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(d)->{
                        d.dismiss();
                        if(oxigenPlanresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                            startSIActivity(context,oxigenPlanresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                        }else if(oxigenPlanresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                            ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenPlanresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                        }
                    },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                        cancel.dismiss();
                    }));
                }else if(oxigenTransactionVO.getStatusCode().equals("ap103")){
                    // 12/04/2020
                    MyDialog.showWebviewAlertDialog(context, oxigenTransactionVO.getHtmlString(),true,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(d)->{
                        d.dismiss();

                        String[] buttons = {"New Mandate", "Existing Mandate"};
                        Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                            @Override
                            public void confirm(Dialog dialog) {
                                dialog.dismiss();
                                createBankListInDialog(context,oxigenPlanresp.getServiceId(),oxigenTransactionVO,new CallBackInterface((CallBackInterface.OnSuccess)(onclick)->{
                                    String bankId = (String) onclick;
                                    if(!bankId.equals("0")){
                                        oxigenPlanresp.setAnonymousInteger(Integer.parseInt(bankId));
                                        proceedRechargeOnMandate(context,oxigenPlanresp);
                                    }else {
                                        if(oxigenPlanresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                                            startSIActivity(context,oxigenPlanresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                                        }else if(oxigenPlanresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                                            ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenPlanresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                                        }
                                    }
                                }));
                            }
                            @Override
                            public void modify(Dialog dialog) {
                                dialog.dismiss();
                                if(oxigenPlanresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                                    startSIActivity(context,oxigenPlanresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                                }else if(oxigenPlanresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                                    ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenPlanresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                                }
                            }
                        }, context, oxigenTransactionVO.getErrorMsgs().get(0), "", buttons);
                    },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                        cancel.dismiss();
                    }));
                }
            }
        }));
    }
    public static void createBankListInDialog(Context context, Integer serivceId, OxigenTransactionVO checkMandateResponse , CallBackInterface callBackInterface){
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

            *//* if (!bankId.equals("0")) {
                    CheckMandateAndShowDialog.allotBnakForService(context,serivceId,Integer.parseInt(bankId),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                        callBackInterface.onSuccess(bankId);
                    }));
                } else {
                    callBackInterface.onSuccess("0");
                }*//*

            // 30/06/2020
            Utility.alertselectdialog(context, "Choose from existing Bank", customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) callBackInterface::onSuccess));
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }*/



    public static void proceedRechargeOnMandate(Context context, OxigenTransactionVO oxigenTransactionVO){
        OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
        responseOxigenTransactionVO.setTypeId(oxigenTransactionVO.getTypeId());
        responseOxigenTransactionVO.setAnonymousString(oxigenTransactionVO.getAnonymousInteger().toString());

        if(oxigenTransactionVO.getProvider().getProviderId()==null){
            AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
            authServiceProviderVO.setProviderId(AuthServiceProviderVO.ENACHIDFC);
            responseOxigenTransactionVO.setProvider(authServiceProviderVO);
        }else{
            responseOxigenTransactionVO.setProvider(oxigenTransactionVO.getProvider());
        }

        BillPayRequest.proceedBillPayment(responseOxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            try{
                handelRechargeSuccess(context,(OxigenTransactionVO)s);
            }catch (Exception e){
                ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
            }
        },(VolleyResponse.OnError)(e)->{
        }));
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
                oxigenValidateResponce = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if(oxigenValidateResponce.getStatusCode().equals("400")){
                    StringBuffer stringBuffer= new StringBuffer();
                    for(int i=0;i<oxigenValidateResponce.getErrorMsgs().size();i++){
                        stringBuffer.append(oxigenValidateResponce.getErrorMsgs().get(i));
                    }
                    Utility.showSingleButtonDialog(context,oxigenValidateResponce.getDialogTitle(),stringBuffer.toString(),false);
                }else {
                    if(oxigenValidateResponce.getTypeId()==null){
                        Utility.showSingleButtonDialog(context,"Error !","Something went wrong, Please try again!",false);
                    }else{
                        //if due date > 2 paybill date show payment dialog select recharge mode
                        if(oxigenValidateResponce.getPaymentDialogShowMandate()){
                            Utility.showSelectPaymentTypeDialog(context,"Payment Type",oxigenValidateResponce.getPaymentTypeObject(),new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess)(position)->{
                                int selectPosition=Integer.parseInt(position);
                                if(selectPosition==ApplicationConstant.BankMandatePayment ){
                                    // 07/05/2020
                                    showBankMandateOrSiMandateInfo(context,oxigenValidateResponce.getBankMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                        if(oxigenValidateResponce.getShowDialog()){
                                            // 07/05/2020
                                            MyDialog.showWebviewConditionalAlertDialog(context,oxigenValidateResponce.getClinkingOnBankMandate(),true,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(rechargenow)->{
                                                HashMap<String,Object> objectHashMap = (HashMap<String, Object>) rechargenow;
                                                ((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))).dismiss();
                                                if(String.valueOf(objectHashMap.get("data")).equalsIgnoreCase("ok")){
                                                    paymentGatewayResponse.onPg(oxigenValidateResponce);
                                                }
                                            },(ConfirmationGetObjet.OnCancel)(cancel)->{
                                                ((Dialog)cancel).dismiss();
                                                oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.ENACHIDFC));
                                                paymentGatewayResponse.onEnach(oxigenValidateResponce);
                                            }));
                                        }else {
                                            oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.ENACHIDFC));
                                            paymentGatewayResponse.onEnach(oxigenValidateResponce);
                                        }
                                    }));
                                    // if service id is dish show mandate dialog
                                }else if(selectPosition==ApplicationConstant.SIMandatePayment){
                                    showBankMandateOrSiMandateInfo(context,oxigenValidateResponce.getSiMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                        oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.AUTOPE_PG));
                                        paymentGatewayResponse.onSiMandate(oxigenValidateResponce);
                                    }));
                                }else if(selectPosition==ApplicationConstant.UPIMandatePayment) {
                                    showBankMandateOrSiMandateInfo(context,oxigenValidateResponce.getUpiMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                        oxigenValidateResponce.setProvider(getAuthServiceProvider(AuthServiceProviderVO.AUTOPE_PG_UPI));
                                        paymentGatewayResponse.onUPIMandate(oxigenValidateResponce);
                                    }));

                                }
                            }));
                        }else {
                            //if due date <  2 paybill date not show payment dialog and move to SI Mandate automatic
                            paymentGatewayResponse.onPg(oxigenValidateResponce);
                        }
                    }
                }
            }
        });
    }
    public static void showBankMandateOrSiMandateInfo(Context context ,String htmlUrl, ConfirmationDialogInterface confirmationDialogInterface){
        try {
            MyDialog.showWebviewConditionalAlertDialog(context,htmlUrl,true,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
                HashMap<String,Object> objectHashMapMandateDialog = (HashMap<String, Object>) ok;
                ((Dialog) Objects.requireNonNull(objectHashMapMandateDialog.get("dialog"))).dismiss();
                if(String.valueOf(objectHashMapMandateDialog.get("data")).equalsIgnoreCase("ok")){
                    confirmationDialogInterface.onOk(null);
                }
            },(ConfirmationGetObjet.OnCancel)(cancel)->{
                ((Dialog)cancel).dismiss();
            }));
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
    public static void handelRechargeSuccess(Context context,OxigenTransactionVO oxigenTransactionVOSuccess) {
        // ask to customer for bank mandate
        dismissDialogCurrentActivity();
        //replace oxigenValidateResponce change on oxigenTransactionVOresp
        oxigenValidateResponce=oxigenTransactionVOSuccess;
        if(oxigenTransactionVOSuccess.isEventIs()){
            MyDialog.showSingleButtonBigContentDialog(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ok.dismiss();

                afterRechargeMoveHistorySummaryActivity(context,false,oxigenTransactionVOSuccess);
            }),oxigenTransactionVOSuccess.getDialogTitle(),oxigenTransactionVOSuccess.getAnonymousString());

        }else {
            MyDialog.showWebviewConditionalAlertDialog(context,oxigenTransactionVOSuccess.getHtmlString(),false,new ConfirmationGetObjet((ConfirmationGetObjet.OnOk)(ok)->{
                //((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                HashMap<String,Object> objectHashMap = (HashMap<String, Object>) ok;
                // ((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))).dismiss();
                GlobalApplication.dialog_List.add(((Dialog) Objects.requireNonNull(objectHashMap.get("dialog"))));

                if(String.valueOf(objectHashMap.get("data")).equalsIgnoreCase("ok")){
                    //afterRechargeAddMandate(context,oxigenTransactionVOSuccess);
                }else{
                    try {
                        JSONObject alertDialogDate =new JSONObject((String) objectHashMap.get("data"));
                        CheckMandateAndShowDialog.setManuallyServiceSchedule(context,oxigenTransactionVOSuccess,alertDialogDate,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                            OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) success;
                            afterRechargeMoveHistorySummaryActivity(context,true,oxigenTransactionVO);
                        }));
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                    }
                }
            },(ConfirmationGetObjet.OnCancel)(cancel)->{
                ((Dialog)cancel).dismiss();
                 afterRechargeMoveHistorySummaryActivity(context,false,oxigenTransactionVOSuccess);
            }));

        }
    }
   /* public static void afterRechargeAddMandate(Context context , OxigenTransactionVO oxigenTransactionVOresp){
      CheckMandateAndShowDialog.oxiServiceMandateCheck(context,oxigenTransactionVOresp.getServiceId(),3,new VolleyResponse((VolleyResponse.OnSuccess)(mandatecheckresp)->{
            OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) mandatecheckresp;
            if(oxigenTransactionVO!=null){
                if(oxigenTransactionVO.getStatusCode().equals("ap102")) {
                    ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                }else if(oxigenTransactionVO.getStatusCode().equals("ap103")){
                    String[] buttons = {"New Bank", "Existing Bank"};
                    Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            createBankListInDialog(context,oxigenTransactionVOresp.getServiceId(),oxigenTransactionVO,new CallBackInterface((CallBackInterface.OnSuccess)(onclick)->{
                                String bankId = (String) onclick;
                                if(!bankId.equals("0")){
                                    afterRechargeMoveHistorySummaryActivity(context,true,oxigenTransactionVOresp);
                                }else {
                                    ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                                }
                            }));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                            ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                        }
                    }, context, oxigenTransactionVO.getErrorMsgs().get(0), "", buttons);
                }
            }
        }));
    }*/
    public static void dismissDialogCurrentActivity(){
        if(GlobalApplication.dialog_List.size()>0){
            for (Dialog dialog : GlobalApplication.dialog_List){
                dialog.dismiss();
            }
        }
    }
    public static void afterRechargeMoveHistorySummaryActivity(Context context , boolean getHistoryDetails , OxigenTransactionVO oxigenTransactionVO){
        dismissDialogCurrentActivity();

        try {
            if(getHistoryDetails){
                CheckMandateAndShowDialog.afterRechargeGetRechargeDetails(context,oxigenTransactionVO.getCustoemrHistoryId(),oxigenTransactionVO.getAnonymousString(),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                    CustomerVO customerVO = (CustomerVO) success;
                    MyDialog.showSingleButtonBigContentDialog(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        ok.dismiss();
                        if(customerVO.getCustoemrHistoryId()!=null){
                            ((Activity)context).startActivity(new Intent(context,HistorySummary.class).putExtra("historyId",customerVO.getCustoemrHistoryId().toString()));
                            ((Activity)context).finish();
                        }else{
                            ((Activity)context).finish();
                        }

                    }),customerVO.getDialogTitle(),customerVO.getAnonymousString());
                }));
            }else {
                if(oxigenTransactionVO.getCustoemrHistoryId()!=null){
                    ((Activity)context).startActivity(new Intent(context,HistorySummary.class).putExtra("historyId",oxigenTransactionVO.getCustoemrHistoryId().toString()));
                    ((Activity)context).finish();
                }else{
                    ((Activity)context).finish();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
    public static void onActivityResult(Context context,Intent data,int requestCode){
        if(requestCode==200){
            if(data.getIntExtra("status",0)== CCTransactionStatusVO.SUCCESS){
                OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                oxigenTransactionVO.setTypeId(Integer.parseInt(data.getStringExtra("oxigenTypeId")));
                oxigenTransactionVO.setAnonymousString(data.getStringExtra("tnxid"));
                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                authServiceProviderVO.setProviderId(AuthServiceProviderVO.PAYU);
                oxigenTransactionVO.setProvider(authServiceProviderVO);

                BillPayRequest.proceedBillPayment(oxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                    handelRechargeSuccess(context,(OxigenTransactionVO)s);
                },(VolleyResponse.OnError)(e)->{
                }));
            }else if(data.getIntExtra("status",0)== CCTransactionStatusVO.FAILURE){
                Utility.showSingleButtonDialogconfirmation(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) Dialog::dismiss),"",data.getStringExtra("message"));
            }
        }else if(requestCode== ApplicationConstant.REQ_ENACH_MANDATE){
            boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
            if(enachMandateStatus){
                afterRechargeMoveHistorySummaryActivity(context,true,oxigenValidateResponce);
            }else{
                Utility.showSingleButtonDialog(context,"Alert",data.getStringExtra("msg"),false);
            }
        }else if(requestCode==ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE){
            boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
            if(enachMandateStatus){
                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                authServiceProviderVO.setProviderId(AuthServiceProviderVO.ENACHIDFC);
                OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
                responseOxigenTransactionVO.setTypeId(oxigenValidateResponce.getTypeId());
                responseOxigenTransactionVO.setAnonymousString(data.getStringExtra("bankMandateId"));
                responseOxigenTransactionVO.setProvider(authServiceProviderVO);
                // 01-08-2020
                responseOxigenTransactionVO.setEventIs(true);
                //recharge for after enach mandate
                BillPayRequest.proceedBillPayment(responseOxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                    handelRechargeSuccess(context,(OxigenTransactionVO)s);
                },(VolleyResponse.OnError)(e)->{
                }));
            }else{
                Utility.showSingleButtonDialog(context,"Alert",data.getStringExtra("msg"),false);
            }
        }else if(requestCode==ApplicationConstant.REQ_SI_MANDATE){
            int SIMandateId=data.getIntExtra("mandateId",0);
            if(SIMandateId!=0){
                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                authServiceProviderVO.setProviderId(AuthServiceProviderVO.AUTOPE_PG);
                OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
                responseOxigenTransactionVO.setTypeId(oxigenValidateResponce.getTypeId());
                responseOxigenTransactionVO.setAnonymousString(String.valueOf(SIMandateId));
                responseOxigenTransactionVO.setProvider(authServiceProviderVO);
                responseOxigenTransactionVO.setEventIs(true);

                //recharge for after enach mandate
                BillPayRequest.proceedBillPayment(responseOxigenTransactionVO,context,
                        new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                    handelRechargeSuccess(context,(OxigenTransactionVO)s);
                },(VolleyResponse.OnError)(e)->{
                }));
            }else{
                Utility.showSingleButtonDialog(context,"Error !", Content_Message.error_message,false);
            }
        }else if(requestCode==ApplicationConstant.REQ_MANDATE_FOR_BILL_FETCH_ERROR){
            boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
            String mandateId=data.getStringExtra("bankMandateId");
            int actionId=data.getIntExtra("actionId",0);
            if(enachMandateStatus && actionId!=0 && mandateId!=null){
                //responseOxigenTransactionVO.setAnonymousString(data.getStringExtra("bankMandateId"));
                updateMandateAgainstOpeator(context,Integer.parseInt(mandateId),actionId,AuthServiceProviderVO.ENACHIDFC,true);
            }else{
                Utility.showSingleButtonDialog(context,"Alert",data.getStringExtra("msg"),false);
            }
        }else if(requestCode==ApplicationConstant.REQ_SI_FOR_BILL_FETCH_ERROR){
            int SIMandateId=data.getIntExtra("mandateId",0);
            int actionId=data.getIntExtra("actionId",0);
            if(SIMandateId!=0 && actionId!=0){
                updateMandateAgainstOpeator(context,SIMandateId,actionId,AuthServiceProviderVO.AUTOPE_PG,true);
            }else{
                Utility.showSingleButtonDialog(context,"Error !", Content_Message.error_message,false);
            }
        }else if(requestCode == ApplicationConstant.REQ_UPI_FOR_MANDATE){

            int UPIMandateId=data.getIntExtra("mandateId",0);
            if(UPIMandateId!=0){
                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                authServiceProviderVO.setProviderId(AuthServiceProviderVO.AUTOPE_PG_UPI);
                OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
                responseOxigenTransactionVO.setTypeId(oxigenValidateResponce.getTypeId());
                responseOxigenTransactionVO.setAnonymousString(String.valueOf(UPIMandateId));
                responseOxigenTransactionVO.setProvider(authServiceProviderVO);
                responseOxigenTransactionVO.setEventIs(true);
                //recharge for after enach mandate
                BillPayRequest.proceedBillPayment(responseOxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                    handelRechargeSuccess(context,(OxigenTransactionVO)s);
                },(VolleyResponse.OnError)(e)->{
                }));
            }else{
                Utility.showSingleButtonDialog(context,"Error !", Content_Message.error_message,false);
            }
        }
    }

    static void updateMandateAgainstOpeator(Context context,int mandateId,int actionId,int providerId ,boolean mandateByList) {
        try {

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = OxigenPlanBO.updateMandateAgainstOpeator();
            OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();

            oxigenTransactionVO.setTypeId(actionId);
            oxigenTransactionVO.setEventIs(mandateByList);

            AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
            authServiceProviderVO.setProviderId(providerId);
            oxigenTransactionVO.setProvider(authServiceProviderVO);
            oxigenTransactionVO.setAnonymousString(String.valueOf(mandateId));

            Gson gson = new Gson();
            String json = gson.toJson(oxigenTransactionVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("proceedBillPayment",gson.toJson(oxigenTransactionVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    OxigenTransactionVO oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(oxigenTransactionVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) oxigenTransactionVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,oxigenTransactionVOresp.getDialogTitle(),sb.toString(),false);
                    }else {
                        Utility.showSingleButtonDialog(context,oxigenTransactionVOresp.getDialogTitle(),oxigenTransactionVOresp.getAnonymousString(),true);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }


}

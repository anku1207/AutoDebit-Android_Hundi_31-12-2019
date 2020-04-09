package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;

import com.google.gson.Gson;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.PaymentGatewayResponse;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.CCTransactionStatusVO;
import com.uav.autodebit.vo.ConnectionVO;
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

import static com.uav.autodebit.util.Utility.showDoubleButtonDialogConfirmation;

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

    private static void proceedBillPayment(OxigenTransactionVO oxigenTransactionVO, Context context,VolleyResponse volleyResponse) {
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
            }else if(!amount.getText().toString().equals("") && ((int)Double.parseDouble(amount.getText().toString()))<minamt){
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

               //show two button dialog on customer for first time pay bill
               OxigenTransactionVO oxigenPlanresp=(OxigenTransactionVO) pg;
               if(!oxigenPlanresp.isEventIs()){
                   String [] btn ={"PG","Mandate"};
                   Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                       @Override
                       public void confirm(Dialog dialog) {
                           dialog.dismiss();
                           ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenPlanresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                       }
                       @Override
                       public void modify(Dialog dialog) {
                           dialog.dismiss();
                           ((Activity)context).startActivityForResult(new Intent(context,PaymentGateWay.class).putExtra("oxigenTypeId",((OxigenTransactionVO)pg).getTypeId().toString()),200);
                       }
                   },context,oxigenPlanresp.getAnonymousString(),"",btn);
               }else {
                   ((Activity)context).startActivityForResult(new Intent(context,PaymentGateWay.class).putExtra("oxigenTypeId",((OxigenTransactionVO)pg).getTypeId().toString()),200);
               }

               },(PaymentGatewayResponse.OnEnach)(onEnach)->{

               BillPayRequest.proceedBillPayment((OxigenTransactionVO)onEnach,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                   handelRechargeSuccess(context,(OxigenTransactionVO)s);
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
                oxigenValidateResponce = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                if(oxigenValidateResponce.getStatusCode().equals("400")){
                    StringBuffer stringBuffer= new StringBuffer();
                    for(int i=0;i<oxigenValidateResponce.getErrorMsgs().size();i++){
                        stringBuffer.append(oxigenValidateResponce.getErrorMsgs().get(i));
                    }
                    Utility.showSingleButtonDialog(context,"Error !",stringBuffer.toString(),false);
                }else {
                    if(oxigenValidateResponce.getTypeId()==null){
                        Utility.showSingleButtonDialog(context,"Error !","Something went wrong, Please try again!",false);
                        return;
                    }
                    if(oxigenValidateResponce.isEventIs()){
                        String [] btn ={"PG","Mandate"};
                        Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                            @Override
                            public void confirm(Dialog dialog) {
                                dialog.dismiss();

                                AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                                authServiceProviderVO.setProviderId(AuthServiceProviderVO.ENACHIDFC);

                                OxigenTransactionVO responseOxigenTransactionVO =new OxigenTransactionVO();
                                responseOxigenTransactionVO.setTypeId(oxigenValidateResponce.getTypeId());
                                responseOxigenTransactionVO.setAnonymousString(oxigenValidateResponce.getAnonymousInteger().toString());
                                responseOxigenTransactionVO.setProvider(authServiceProviderVO);
                                paymentGatewayResponse.onEnach(responseOxigenTransactionVO);
                            }
                            @Override
                            public void modify(Dialog dialog) {
                                dialog.dismiss();
                                paymentGatewayResponse.onPg(oxigenValidateResponce);
                            }
                        },context,oxigenValidateResponce.getAnonymousString(),"",btn);
                    }else {

                        //first time payment show two btn dialog on customer
                        paymentGatewayResponse.onPg(oxigenValidateResponce);

                    }
                }
            }
        });
    }

    private static void handelRechargeSuccess(Context context,OxigenTransactionVO oxigenTransactionVOresp) {
        // ask to customer for bank mandate
        if(oxigenTransactionVOresp.isEventIs()){

            String [] btnname={"No","Yes"};

            showDoubleButtonDialogConfirmation(new DialogInterface() {
                @Override
                public void confirm(Dialog dialog) {
                    dialog.dismiss();
                    ((Activity)context).startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_ENACH_MANDATE);
                }

                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                    ((Activity)context).startActivity(new Intent(context,HistorySummary.class).putExtra("historyId",oxigenTransactionVOresp.getAnonymousInteger().toString()));
                    ((Activity)context).finish();
                }
            },context,oxigenTransactionVOresp.getAnonymousString(),"",btnname);
        }else {
            Utility.showSingleButtonDialogconfirmation(context,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ((Activity)context).startActivity(new Intent(context,HistorySummary.class).putExtra("historyId",oxigenTransactionVOresp.getAnonymousInteger().toString()));
                ((Activity)context).finish();
            }),"",oxigenTransactionVOresp.getAnonymousString());
        }

    }

    public static void showDoubleButtonDialogConfirmation(com.uav.autodebit.util.DialogInterface mcxtinter, Context context , String Msg , String title,  String... buttons){
        String leftButton= (buttons.length==0 ?"Modify":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton=(buttons.length<=1 ?"Next":buttons[1]);//(rightButton==null?"Next":rightButton);
        try{
            final com.uav.autodebit.util.DialogInterface dialogInterface =mcxtinter;
            final Dialog var3 = new Dialog(context);
            var3.requestWindowFeature(1);
            var3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            var3.setContentView(R.layout.showdoublebuttondialogconfirmation);
            var3.setCanceledOnTouchOutside(false);
            var3.setCancelable(false);
            var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            TextView titletext=var3.findViewById(R.id.dialog_one_tv_title);
            TextView msg=var3.findViewById(R.id.dialog_one_tv_text);
            Button modify=var3.findViewById(R.id.button1);
            Button next=var3.findViewById(R.id.button2);

            modify.setText(leftButton);
            next.setText(rightButton);

            titletext.setText(title);
           /* if(title==null || title.equals("") ){
                titletext.setVisibility(View.GONE);
            }else {
                titletext.setVisibility(View.VISIBLE);
            }*/
            msg.setText(Msg);

            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.modify(var3);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.confirm(var3);
                }
            });

            if(!var3.isShowing())  var3.show();
        }catch (Exception e){
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
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
                ((Activity)context).startActivity(new Intent(context,History.class));
                ((Activity)context).finish();
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
                //recharge for after enach mandate
                BillPayRequest.proceedBillPayment(responseOxigenTransactionVO,context,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                    handelRechargeSuccess(context,(OxigenTransactionVO)s);
                },(VolleyResponse.OnError)(e)->{
                }));
            }else{
                Utility.showSingleButtonDialog(context,"Alert",data.getStringExtra("msg"),false);
            }
        }
    }






}

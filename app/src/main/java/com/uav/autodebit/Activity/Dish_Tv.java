package com.uav.autodebit.Activity;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.DishTVBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.D2HVO;
import com.uav.autodebit.vo.DishTvVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Dish_Tv  extends Base_Activity implements View.OnClickListener  {
    EditText accountnumber;
    Button proceed;
    Context context;
    LinearLayout plandetailslayout;
    String monthlySubscriptionAmount;
    DishTvVO response_DishTVVO;
    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish__tv);
        getSupportActionBar().hide();

        context=Dish_Tv.this;

        monthlySubscriptionAmount=null;
        response_DishTVVO=null;

        accountnumber=findViewById(R.id.accountnumber);
        proceed=findViewById(R.id.proceed);
        plandetailslayout=findViewById(R.id.plandetailslayout);
        back_activity_button=findViewById(R.id.back_activity_button);


        proceed.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back_activity_button){
            finish();

        }else if(view.getId()==R.id.proceed){
            Utility.hideKeyboard(this);

            if(accountnumber.getText().toString().equals("")){
                accountnumber.setError(Content_Message.user_Registration_Filed_Required);
                return;
            }
            getPlanDetail();
        }
    }


    private void getPlanDetail(){
        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = DishTVBO.getDishTvPlanDetail();
            CustomerVO customerVO =new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));

            D2HVO d2HVO=new D2HVO();
            d2HVO.setVcNo(accountnumber.getText().toString());
            d2HVO.setCustomer(customerVO);
            params.put("volley",gson.toJson(d2HVO));
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Log.w("responce",response.toString());

                    Gson gson = new Gson();
                    response_DishTVVO = gson.fromJson(response.toString(), DishTvVO.class);

                    if(response_DishTVVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) response_DishTVVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,response_DishTVVO.getDialogTitle(),sb.toString(),false);
                    }else{
                       // if(response_DishTVVO.getEnachMandateId()==null || response_DishTVVO.getEnachMandateId().equals("")){
                            Utility.removeEle(proceed);
                            Utility.removeEle(accountnumber);
                            JSONObject detailJson=(new JSONObject(response_DishTVVO.getVcDetails())).getJSONObject("Result");
                            JSONArray planDetailarr= new JSONArray();
                            JSONObject jsonObject ;
                            if(detailJson.has("SubscriberName") && !detailJson.getString("SubscriberName").equals("")){
                                jsonObject=new JSONObject();
                                jsonObject.put("key","SubscriberName");
                                jsonObject.put("value",detailJson.getString("SubscriberName"));
                                planDetailarr.put(jsonObject);
                            }

                            if(detailJson.has("SwitchOffDate") && !detailJson.getString("SwitchOffDate").equals("")){
                                jsonObject=new JSONObject();
                                jsonObject.put("key","SwitchOffDate");
                                jsonObject.put("value",detailJson.getString("SwitchOffDate"));
                                planDetailarr.put(jsonObject);
                            }

                            if(detailJson.has("VCNO") && !detailJson.getString("VCNO").equals("")){
                                jsonObject=new JSONObject();
                                jsonObject.put("key","VCNO");
                                jsonObject.put("value",detailJson.getString("VCNO"));
                                planDetailarr.put(jsonObject);
                            }

                            if(detailJson.has("monthlySubscriptionAmount") && !detailJson.getString("monthlySubscriptionAmount").equals("")){
                                jsonObject=new JSONObject();
                                jsonObject.put("key","monthlySubscriptionAmount");
                                jsonObject.put("value",detailJson.getString("monthlySubscriptionAmount"));
                                planDetailarr.put(jsonObject);
                                monthlySubscriptionAmount=detailJson.getString("monthlySubscriptionAmount");
                            }
                            if(detailJson.has("Mobileno") && !detailJson.getString("Mobileno").equals("")){
                                jsonObject=new JSONObject();
                                jsonObject.put("key","Mobileno");
                                jsonObject.put("value",detailJson.getString("Mobileno"));
                                planDetailarr.put(jsonObject);
                            }
                            if(detailJson.has("Emailid") && !detailJson.getString("Emailid").equals("")){
                                jsonObject=new JSONObject();
                                jsonObject.put("key","Emailid");
                                jsonObject.put("value",detailJson.getString("Emailid"));
                                planDetailarr.put(jsonObject);
                            }
                            createLayout(planDetailarr,false,null);
                        /*}else {
                            getDishTvPostMandate();
                        }*/
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    private void createLayout(JSONArray planDetailarr ,boolean isMandate ,String msg){

        try {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinssemibold);

            if(isMandate){
                TextView mandatemsg = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                mandatemsg.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                mandatemsg.setText(msg);
                mandatemsg.setTypeface(typeface);
                mandatemsg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                mandatemsg.setTextColor(getResources().getColor(R.color.green));
                plandetailslayout.addView(mandatemsg);
            }


            for(int i=0 ;i<planDetailarr.length();i++){
                JSONObject jsonObject1 =planDetailarr.getJSONObject(i);

                LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_layout));
                et.setPadding(Utility.getPixelsFromDPs(context,10),Utility.getPixelsFromDPs(context,10),Utility.getPixelsFromDPs(context,10),Utility.getPixelsFromDPs(context,10));
                TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                text.setText(jsonObject1.getString("key"));
                text.setMaxLines(1);
                text.setEllipsize(TextUtils.TruncateAt.END);
                text.setTypeface(typeface);
                text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                text.setPadding(10,0,10,10);

                TextView commaText = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                commaText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) .1));
                commaText.setText(":");
                commaText.setTypeface(typeface);
                commaText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                TextView value = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_value));
                value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                value.setText(jsonObject1.getString("value"));
                value.setTypeface(typeface);
                value.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                value.setPadding(10,0,10,10);

                et.addView(text);
                et.addView(commaText);
                et.addView(value);
                plandetailslayout.addView(et);
            }

            if(!isMandate){
                LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_layout));
                et.setPadding(Utility.getPixelsFromDPs(context,10),Utility.getPixelsFromDPs(context,10),Utility.getPixelsFromDPs(context,10),Utility.getPixelsFromDPs(context,10));

                TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,  1));
                text.setText("Mandate Amount");
                text.setMaxLines(1);
                text.setEllipsize(TextUtils.TruncateAt.END);
                text.setTypeface(typeface);
                text.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

                TextView commaText = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                commaText.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) .1));
                commaText.setText(":");
                commaText.setTypeface(typeface);
                commaText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                EditText editText =new EditText(context);
                editText.setLayoutParams(new LinearLayout.LayoutParams(0,ViewGroup.LayoutParams.WRAP_CONTENT ,(float) 1));
                editText.setTypeface(typeface);
                editText.setBackground(getApplication().getDrawable(R.drawable.edittext_round_border));
                editText.setTextSize(14);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                double v=monthlySubscriptionAmount!=null?Math.ceil(Double.parseDouble(monthlySubscriptionAmount)):0.00;
                editText.setText((int)v+"");

                et.addView(text);
                et.addView(commaText);
                et.addView(editText);
                plandetailslayout.addView(et);

                Button button =Utility.getButton(context);
                button.setText("Proceed");
                plandetailslayout.addView(button);

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Utility.hideKeyboard(Dish_Tv.this);
                        boolean valid=true;
                        if(editText.getText().toString().equals("")){
                            editText.setError(  Utility.getErrorSpannableStringDynamicEditText(context, "this field is required"));
                            valid=false;
                        }
                        if(valid){
                            if(Double.parseDouble(monthlySubscriptionAmount) > Double.parseDouble(editText.getText().toString()) ){
                                Utility.showSingleButtonDialog(context,"Alert","Mandate Amount Should Be Greater then or equal Rs." +Math.ceil(Double.parseDouble(monthlySubscriptionAmount)),false);
                            }else {
                                //  addBank(button,editText.getText().toString());

                                Utility.confirmationDialog(new DialogInterface() {
                                    @Override
                                    public void confirm(Dialog dialog) {
                                        Utility.dismissDialog(Dish_Tv.this,dialog);
                                        addBank(button,editText.getText().toString(),planDetailarr);
                                    }
                                    @Override
                                    public void modify(Dialog dialog) {
                                        Utility.dismissDialog(Dish_Tv.this,dialog);

                                    }
                                },Dish_Tv.this,planDetailarr,null,"Confirmation");
                            }
                        }
                    }
                });
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }


    private void addBank(Button button,String mandateAmt , JSONArray planDetailarr) {
        try {
                JSONObject detailfiled=new JSONObject();
                for(int i=0;i<planDetailarr.length();i++){
                    JSONObject jsonObject1 =planDetailarr.getJSONObject(i);
                    if(jsonObject1.getString("key").equals("VCNO")){
                        detailfiled.put("Registered Mobile Number / Viewing Card Number",jsonObject1.getString("value"));
                    }
                }

                OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                oxigenTransactionVO.setOperateName("Dishtv");
                oxigenTransactionVO.setAmount(Double.valueOf(mandateAmt));
                oxigenTransactionVO.setAnonymousString(detailfiled.toString());

                ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                serviceTypeVO.setServiceTypeId(ApplicationConstant.DISHTV);
                oxigenTransactionVO.setServiceType(serviceTypeVO);

                CustomerVO customerVO =new CustomerVO();
                customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
                oxigenTransactionVO.setCustomer(customerVO);

                BillPayRequest.proceedRecharge(this,false,oxigenTransactionVO);

      /*      Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = D2HBO.mandateAmountOverrideByServiceId();
            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
            customerVO.setServiceId(ApplicationConstant.DISHTV);
            customerVO.setAnonymousInteger(Integer.parseInt(mandateAmt));

            params.put("volley",gson.toJson(customerVO));
            connectionVO.setParams(params);
            Log.w("request",params.toString());
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Log.w("responce",response.toString());
                    Gson gson = new Gson();
                    DishTvVO dishTvVO = gson.fromJson(response.toString(), DishTvVO.class);


                    if(dishTvVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) dishTvVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,"Error !",sb.toString(),false);
                    }else {

                        if(!dishTvVO.getStatusCode().equals("200") && !dishTvVO.getStatusCode().equals("ap104")){
                            if(dishTvVO.getStatusCode().equals("ap105") || dishTvVO.getStatusCode().equals("ap107") ||dishTvVO.getStatusCode().equals("ap102")){
                                // 12/04/2020
                                MyDialog.showWebviewAlertDialog(Dish_Tv.this, dishTvVO.getHtmlString(),false,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(dialog)->{
                                    dialog.dismiss();
                                    startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(ApplicationConstant.DISHTV))).putExtra("disAmountEdittext",true),ApplicationConstant.REQ_ENACH_MANDATE);
                                },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                                    cancel.dismiss();
                                }));
                            }else if(dishTvVO.getStatusCode().equals("ap106") || dishTvVO.getStatusCode().equals("ap103") || dishTvVO.getStatusCode().equals("ap108")) {

                                // 12/04/2020
                                MyDialog.showWebviewAlertDialog(Dish_Tv.this, dishTvVO.getHtmlString(),false,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(dialog)->{
                                    dialog.dismiss();
                                    String[] buttons = {"New Bank","Existing Bank"};
                                    Utility.showDoubleButtonDialogConfirmation(new com.uav.autodebit.util.DialogInterface() {
                                        @Override
                                        public void confirm(Dialog dialog) {
                                            dialog.dismiss();
                                            try {
                                                JSONArray arryjson = new JSONArray(dishTvVO.getAnonymousString());
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

                                                Utility.alertselectdialog(context, "Choose from existing Bank", customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (s) -> {
                                                    if (!s.equals("0")) {
                                                        setBankForService(ApplicationConstant.d2h, Integer.parseInt(Session.getCustomerId(context)), Integer.parseInt(s));
                                                    } else {
                                                        startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(ApplicationConstant.DISHTV))).putExtra("disAmountEdittext",true), ApplicationConstant.REQ_ENACH_MANDATE);
                                                    }
                                                }));
                                            } catch (Exception e) {
                                                ExceptionsNotification.ExceptionHandling(Dish_Tv.this , Utility.getStackTrace(e));
                                            }
                                        }
                                        @Override
                                        public void modify(Dialog dialog) {
                                            dialog.dismiss();
                                            startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(ApplicationConstant.DISHTV))).putExtra("disAmountEdittext",true), ApplicationConstant.REQ_ENACH_MANDATE);
                                        }
                                    }, context, dishTvVO.getErrorMsgs().get(0), "", buttons);
                                },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                                    cancel.dismiss();
                                }));

                            }
                        }else {
                            getDishTvPostMandate();
                        }


                    }
                }

            });*/
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    //bank list select bank for service
    public void setBankForService( int serviceId,int customerId,int bankId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.setBankForService();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(customerId);
        customerVO.setServiceId(serviceId);
        customerVO.setAnonymousInteger(bankId);
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("setBankForService",params.toString());
        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
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
                    getDishTvPostMandate();
                }
            }
        });
    }

    public void getDishTvPostMandate(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = DishTVBO.getDishTvPostMandate();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));

        DishTvVO dishTvVO =new DishTvVO();
        dishTvVO.setCustomer(customerVO);
        dishTvVO.setDishtvId(response_DishTVVO.getDishtvId());
        Gson gson =new Gson();
        String json = gson.toJson(dishTvVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                DishTvVO dishTvVO1 = gson.fromJson(response.toString(), DishTvVO.class);

                if(dishTvVO1.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) dishTvVO1.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,dishTvVO1.getDialogTitle(),sb.toString(),false);
                }else {
                    //set session customer or local cache

                    if(proceed.getParent()!=null && accountnumber.getParent()!=null){
                        Utility.removeEle(proceed);
                        Utility.removeEle(accountnumber);
                    }
                    plandetailslayout.removeAllViews();

                    JSONObject jsonObject;
                    JSONArray jsonArray =new JSONArray();

                    if(dishTvVO1.getSubscriberName()!=null){
                        jsonObject=new JSONObject();
                        jsonObject.put("key","SubscriberName");
                        jsonObject.put("value",dishTvVO1.getSubscriberName());
                        jsonArray.put(jsonObject);
                    }

                    if(dishTvVO1.getSwitchOffDate()!=null){
                        jsonObject=new JSONObject();
                        jsonObject.put("key","SwitchOffDate");
                        jsonObject.put("value",Utility.convertDate2String(new Date(dishTvVO1.getSwitchOffDate()),"dd/MM/YYYY"));
                        jsonArray.put(jsonObject);
                    }

                    if(dishTvVO1.getVcNo()!=null){
                        jsonObject=new JSONObject();
                        jsonObject.put("key","Customer Id");
                        jsonObject.put("value",dishTvVO1.getVcNo());
                        jsonArray.put(jsonObject);
                    }

                    if(dishTvVO1.getRechargeAmt()!=null){
                        jsonObject=new JSONObject();
                        jsonObject.put("key","Plan Amount");
                        jsonObject.put("value",dishTvVO1.getRechargeAmt());
                        jsonArray.put(jsonObject);
                    }

                    if(dishTvVO1.getEnachMandateAmount()!=null){
                        jsonObject=new JSONObject();
                        jsonObject.put("key","Mandate Amount");
                        jsonObject.put("value",dishTvVO1.getEnachMandateAmount());
                        jsonArray.put(jsonObject);
                    }
                    createLayout(jsonArray,true,dishTvVO1.getAnonymousString());
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==200 || requestCode== ApplicationConstant.REQ_ENACH_MANDATE || requestCode==ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE || requestCode== ApplicationConstant.REQ_SI_MANDATE || requestCode== ApplicationConstant.REQ_MANDATE_FOR_BILL_FETCH_ERROR || requestCode== ApplicationConstant.REQ_SI_FOR_BILL_FETCH_ERROR || requestCode==ApplicationConstant.REQ_UPI_FOR_MANDATE){
                if(data !=null){
                    BillPayRequest.onActivityResult(this,data,requestCode);
                }else {
                    Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                }
            }
        }
    }
}







package com.uav.autodebit.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenBillerAutoPaymentVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PayBillByNotification extends Base_Activity implements View.OnClickListener {
    LinearLayout dynamic_layout,fetchbilllayout;
    Button bill_pay;
    OxigenTransactionVO oxigenTransactionVOresp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill_by_notification);
        getSupportActionBar().hide();
        dynamic_layout=findViewById(R.id.dynamic_layout);
        fetchbilllayout=findViewById(R.id.fetchbilllayout);
        bill_pay=findViewById(R.id.bill_pay);
        bill_pay.setOnClickListener(this);

        try {
            JSONObject jsonObject =new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));

            if(jsonObject.has("value") &&   jsonObject.isNull("value")){
                Utility.showSingleButtonDialog(this,"Error !",Content_Message.error_message,true);
            }else{
                getAutoBillFetch(jsonObject.getInt("value") ,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{

                    try {
                        oxigenTransactionVOresp = (OxigenTransactionVO) success;
                        JSONObject billDetailsJson=new JSONObject(oxigenTransactionVOresp.getAnonymousString());


                        JSONArray fetchBillDetails=billDetailsJson.getJSONArray("billFetchDetails");
                        for(int i=0;i<fetchBillDetails.length();i++){
                            JSONObject billJson=fetchBillDetails.getJSONObject(i);
                            LinearLayout et1 = new LinearLayout(new ContextThemeWrapper(this,R.style.confirmation_dialog_layout));

                            et1.setPadding(Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10));

                            TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));
                            text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                            text.setText(billJson.getString("key"));
                            text.setMaxLines(1);
                            text.setEllipsize(TextUtils.TruncateAt.END);
                            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                            TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));
                            value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                            value.setText(billJson.getString("value"));
                            value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            et1.addView(text);
                            et1.addView(value);
                            fetchbilllayout.addView(et1);
                        }


                        //add billFetchFileds

                        CardView cardView = Utility.getCardViewStyle(this);
                        UAVEditText et = Utility.getUavEditText(this);
                        et.setId(View.generateViewId());
                        et.setText(oxigenTransactionVOresp.getOperateName());
                        cardView.addView(et);
                        dynamic_layout.addView(cardView);

                        JSONArray billFiled=billDetailsJson.getJSONArray("billNoData");
                        for(int i=0 ; i<billFiled.length() ; i++){
                            String value=billFiled.getString(i);

                            cardView = Utility.getCardViewStyle(this);
                            et = Utility.getUavEditText(this);
                            et.setId(View.generateViewId());
                            et.setText(value+"");
                            cardView.addView(et);
                            dynamic_layout.addView(cardView);
                        }

                        cardView = Utility.getCardViewStyle(this);
                        et = Utility.getUavEditText(this);
                        et.setId(View.generateViewId());
                        et.setText(oxigenTransactionVOresp.getNetAmount()+"");
                        cardView.addView(et);
                        dynamic_layout.addView(cardView);

                        bill_pay.setVisibility(View.VISIBLE);
                        Utility.disable_AllEditTest(dynamic_layout);
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(PayBillByNotification.this , Utility.getStackTrace(e));
                    }
                }));
            }
       }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(PayBillByNotification.this , Utility.getStackTrace(e));
        }
    }

    private  void getAutoBillFetch(int id,VolleyResponse volleyResponse)throws Exception{
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = OxigenPlanBO.notificationBillDetailsByOxigenBillAutoPaymentId();

        OxigenBillerAutoPaymentVO oxigenBillerAutoPaymentVO =new OxigenBillerAutoPaymentVO();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(PayBillByNotification.this)));

        oxigenBillerAutoPaymentVO.setTypeId(id);
        oxigenBillerAutoPaymentVO.setCustomer(customerVO);
        Gson gson=new Gson();
        String json = gson.toJson(oxigenBillerAutoPaymentVO);
        params.put("volley", json);
        connectionVO.setParams(params);


        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(PayBillByNotification.this,"Error !",sb.toString(),true);
                    bill_pay.setVisibility(View.GONE);
                }else {
                    volleyResponse.onSuccess(oxigenTransactionVOresp);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bill_pay) {
            try {
                BillPayRequest.proceedRecharge(this, true, oxigenTransactionVOresp);
            } catch (Exception e) {
                ExceptionsNotification.ExceptionHandling(this, Utility.getStackTrace(e));
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(resultCode==RESULT_OK){
               if(requestCode==200 || requestCode== ApplicationConstant.REQ_ENACH_MANDATE ||
                       requestCode==ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE){
                    if(data !=null){
                        BillPayRequest.onActivityResult(this,data,requestCode);
                    }else {
                        Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                    }
                }
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
            // Utility.exceptionAlertDialog(Electricity_Bill.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }
}

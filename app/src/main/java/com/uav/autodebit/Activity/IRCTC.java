package com.uav.autodebit.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.IRCTCBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.CustomDialog.BeforeRecharge;
import com.uav.autodebit.Interface.MandateAndRechargeInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class IRCTC extends Base_Activity {
    ImageView imageview,back_activity_button;
    LinearLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_irctc);
        getSupportActionBar().hide();

        imageview=findViewById(R.id.imageview);
        main_layout=findViewById(R.id.main_layout);
        back_activity_button=findViewById(R.id.back_activity_button);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getDetails();

    }

    public void getDetails(){
        getPaymentOpationForService(IRCTC.this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            try {
                CustomerVO customerVO = (CustomerVO) success;
                Picasso.with(this).load(customerVO.getImage()).into(imageview, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        //do smth when picture is loaded successfully
                    }
                    @Override
                    public void onError() {
                    }
                });
                Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
                TextView tv = new TextView(this);
                tv.setText(customerVO.getDialogTitle());
                tv.setTypeface(typeface);
                tv.setLayoutParams(Utility.getLayoutparams(this,20,20,20,20));
                main_layout.addView(tv);

                List<String> existBankList = customerVO.getPaymentType();
                if(existBankList.size()>0){
                    for(String s :existBankList ){
                        tv = new TextView(this);
                        tv.setText(s);
                        tv.setTypeface(typeface);
                        tv.setLayoutParams(Utility.getLayoutparams(this,20,0,20,0));
                        tv.setTextColor(Utility.getColorWrapper(this,R.color.defaultTextColor));
                        main_layout.addView(tv);
                    }
                }


                JSONArray PaymentTypeList=new JSONArray(customerVO.getAnonymousString());

                if(PaymentTypeList.length()>0){
                    RadioGroup radiogroup = new RadioGroup(this);
                    for(int i=0 ; i<PaymentTypeList.length() ; i++){
                        JSONObject object = PaymentTypeList.getJSONObject(i);
                        String text = object.getString("key");
                        RadioButton rdbtn = new RadioButton(this);
                        rdbtn.setId(object.getInt("id"));
                        rdbtn.setText(text);
                        rdbtn.setChecked(false);
                        RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        params.setMargins(20, 10, 20, 10);
                        rdbtn.setLayoutParams(params);
                        rdbtn.setPadding(50,0,0,0);

                        rdbtn.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                        radiogroup.addView(rdbtn);
                    }
                    main_layout.addView(radiogroup);

                    Button proceed=Utility.getButton(this);
                    proceed.setText("Proceed");
                    proceed.setVisibility(View.GONE);
                    main_layout.addView(proceed);

                    radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                            proceed.setVisibility(View.VISIBLE);
                        }
                    });

                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            switch (radiogroup.getCheckedRadioButtonId()){
                                case 1:
                                    BankMandate(IRCTC.this, ApplicationConstant.IRCTC);
                                    break;
                                case 2:
                                    startSIActivity(IRCTC.this,1.00,ApplicationConstant.IRCTC,ApplicationConstant.PG_MANDATE);
                                    break;
                                case 3:
                                    startActivityForResult(new Intent(IRCTC.this,UPI_Mandate.class),ApplicationConstant.REQ_UPI_FOR_MANDATE);
                                    break;
                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
                ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
            }
        }));
    }
    public  void BankMandate(Context context , int  serviceId){
        OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();
        oxigenTransactionVO.setServiceId(serviceId);

        AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(AuthServiceProviderVO.ENACHIDFC);
        oxigenTransactionVO.setProvider(authServiceProviderVO);

        BeforeRecharge.beforeRechargeAddMandate(context,oxigenTransactionVO,new MandateAndRechargeInterface((MandateAndRechargeInterface.OnRecharge)(recharge)->{
            setBankForService(ApplicationConstant.IRCTC,Integer.parseInt(Session.getCustomerId(context)),Integer.parseInt(String.valueOf(recharge)));
        }, (MandateAndRechargeInterface.OnMandate)(mandate)->{
            startActivityForResult(new Intent(context,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))), ApplicationConstant.REQ_ENACH_MANDATE);
        }));
    }
    //bank list select bank for service
    public  void setBankForService(int serviceId,int customerId,int bankId){
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
        VolleyUtils.makeJsonObjectRequest(IRCTC.this,connectionVO , new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(IRCTC.this,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                    main_layout.removeAllViews();
                    getDetails();
                }
            }
        });
    }



    public static void startSIActivity(Context context , double amount , int serviceId,String paymentType ){
        Intent intent = new Intent(context,SI_First_Data.class);
        intent.putExtra("amount",amount);
        intent.putExtra("serviceId",serviceId+"");
        intent.putExtra("paymentType",paymentType);
        ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_MANDATE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(resultCode==RESULT_OK){
                 if(requestCode== ApplicationConstant.REQ_SI_MANDATE){
                    if(data !=null && data.getIntExtra("mandateId",0)!=0){
                        main_layout.removeAllViews();
                        getDetails();
                    }else {
                        Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                    }
                }else if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                     if(data !=null && data.getBooleanExtra("mandate_status",false)){
                         main_layout.removeAllViews();
                         getDetails();
                     }else {
                         Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                     }
                 }else if(requestCode==ApplicationConstant.REQ_UPI_FOR_MANDATE){
                     Toast.makeText(this, "sdfsdfsdf", Toast.LENGTH_SHORT).show();
                 }
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }


    private void getPaymentOpationForService(Context context, VolleyResponse volleyResponse) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = IRCTCBO.getPaymentOpationForService();
            CustomerVO customerVO = new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(context)));
            customerVO.setServiceId(ApplicationConstant.IRCTC);

            Gson gson = new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);
            Log.w("OpationForService",gson.toJson(customerVO));
            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;
                    CustomerVO resp_CustomerVO = gson.fromJson(response.toString(), CustomerVO.class);
                    Log.w("Server_Resp",response.toString());

                    if(resp_CustomerVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) resp_CustomerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(context,"Error !",sb.toString(),false);
                        volleyResponse.onError(null);
                    }else {
                        volleyResponse.onSuccess(resp_CustomerVO);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
}

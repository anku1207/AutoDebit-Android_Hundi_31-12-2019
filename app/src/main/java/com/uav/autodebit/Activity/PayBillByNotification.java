package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
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
import com.uav.autodebit.vo.OxigenPlanVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PayBillByNotification extends AppCompatActivity implements View.OnClickListener {
    LinearLayout dynamic_layout,fetchbilllayout;
    Button bill_pay;
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
                    OxigenTransactionVO oxigenTransactionVOresp = (OxigenTransactionVO) success;


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
                }else {
                    volleyResponse.onSuccess(oxigenTransactionVOresp);
                }
            }
        });


    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bill_pay){
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = OxigenPlanBO.getPlan();

            OxigenPlanVO oxigenPlanVO =new OxigenPlanVO();
            ServiceTypeVO serviceTypeVO =new ServiceTypeVO();

            Gson gson=new Gson();
            String json = gson.toJson(oxigenPlanVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    OxigenPlanVO oxigenPlanVO = gson.fromJson(response.toString(), OxigenPlanVO.class);


                    if(oxigenPlanVO.getStatusCode().equals("400")){

                        StringBuffer stringBuffer= new StringBuffer();

                        for(int i=0;i<oxigenPlanVO.getErrorMsgs().size();i++){
                            stringBuffer.append(oxigenPlanVO.getErrorMsgs().get(i));
                        }
                        Utility.showSingleButtonDialog(PayBillByNotification.this,"Error !",stringBuffer.toString(),false);
                    }else {

                    }
                }
            });
        }

    }
}

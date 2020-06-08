package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OxigenBillerAutoPaymentVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Mandate_Revoke_Dialog extends AppCompatActivity implements View.OnClickListener {
    Button yesbtn,nobtn;
    TextView textview;
    CustomerVO customerVOresp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandate__revoke__dialog);
        getSupportActionBar().hide();

        yesbtn=findViewById(R.id.yesbtn);
        nobtn=findViewById(R.id.nobtn);
        textview=findViewById(R.id.textview);

        yesbtn.setOnClickListener(this);
        nobtn.setOnClickListener(this);

        customerVOresp=new CustomerVO();

        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.7));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);

        try {
            JSONObject jsonObject = new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));
            if (jsonObject.has("value") && jsonObject.isNull("value")) {
                Utility.showSingleButtonDialog(this, "Error !", Content_Message.error_message, true);
            } else {
                getMandateRevokeDetails(jsonObject.getInt("value"),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                    yesbtn.setVisibility(View.VISIBLE);
                    nobtn.setVisibility(View.VISIBLE);

                    customerVOresp = (CustomerVO) success;
                    try {
                        JSONObject object = new JSONObject(customerVOresp.getAnonymousString());
                        yesbtn.setText(object.getString("button1"));
                        nobtn.setText(object.getString("button2"));
                        textview.setText(customerVOresp.getDialogTitle());
                    } catch (JSONException e) {
                        ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                    }
                }));
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }

    }

    private void getMandateRevokeDetails(int id,VolleyResponse volleyResponse)throws Exception{
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.getMandateRevokeDetail();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(this)));
        customerVO.setAnonymousInteger(id);

        Gson gson=new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);

       VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                finish();
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Log.w("respele",response.toString());
                CustomerVO customerResp = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerResp.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerResp.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Mandate_Revoke_Dialog.this,"Error !",sb.toString(),true);
                    yesbtn.setVisibility(View.GONE);
                    nobtn.setVisibility(View.GONE);
                }else {
                    volleyResponse.onSuccess(customerResp);
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.yesbtn:
                skipMandate();
                break;
            case R.id.nobtn:
                finish();
                break;
        }

    }

    private void skipMandate() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.revokeMandate();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(customerVOresp.getCustomerId());
        customerVO.setAnonymousInteger(customerVOresp.getAnonymousInteger());

        Gson gson=new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                finish();
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Log.w("respele",response.toString());
                CustomerVO customerResp = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerResp.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerResp.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Mandate_Revoke_Dialog.this,"Error !",sb.toString(),true);
                }else {
                    Utility.showSingleButtonDialogconfirmation(Mandate_Revoke_Dialog.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        ok.dismiss();
                        finish();
                    }),customerVO.getDialogTitle(),customerVO.getAnonymousString());
                }
            }
        });

    }
}
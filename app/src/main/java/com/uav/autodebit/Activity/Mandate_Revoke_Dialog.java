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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.CustomDialog.MyDialog;
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
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Mandate_Revoke_Dialog extends Base_Activity implements View.OnClickListener {
    Button yesbtn,nobtn;
    TextView textview;
    CustomerVO customerVOresp;
    LinearLayout main_layout;
    JSONObject intentJsonObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mandate__revoke__dialog);


        yesbtn=findViewById(R.id.yesbtn);
        nobtn=findViewById(R.id.nobtn);
        textview=findViewById(R.id.textview);
        main_layout=findViewById(R.id.main_layout);

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
            intentJsonObject = new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));
            if (!intentJsonObject.has("value") || intentJsonObject.isNull("value") || !intentJsonObject.has("notificationId") || intentJsonObject.isNull("notificationId")) {
                Utility.showSingleButtonDialog(this, "Error !", Content_Message.error_message, true);
            } else {
                getMandateRevokeDetails(intentJsonObject.getInt("value"),intentJsonObject.getInt("notificationId"),new VolleyResponse((VolleyResponse.OnSuccess)(success)->{

                    try {
                        customerVOresp = (CustomerVO) success;
                        if(customerVOresp.isEventIs()){
                            main_layout.setVisibility(View.VISIBLE);
                            JSONObject object = new JSONObject(customerVOresp.getAnonymousString());
                            yesbtn.setText(object.getString("Button1"));
                            nobtn.setText(object.getString("Button2"));
                            textview.setText(customerVOresp.getHtmlString());
                        }else {
                            MyDialog.showSingleButtonBigContentDialog(Mandate_Revoke_Dialog.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                ok.dismiss();
                                finish();
                            }),customerVOresp.getDialogTitle(),customerVOresp.getAnonymousString());
                        }
                    } catch (JSONException e) {
                        ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                    }
                }));
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    private void getMandateRevokeDetails(int id,int notificationId,VolleyResponse volleyResponse)throws Exception{
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.getMandateRevokeDetail();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(this)));
        customerVO.setAnonymousInteger(id);
        customerVO.setNotificationId(notificationId);

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
                    main_layout.setVisibility(View.GONE);
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
                try {
                    skipMandate();
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                }
                break;
            case R.id.nobtn:
                finish();
                break;
        }

    }

    private void skipMandate() throws JSONException {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.revokeMandate();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(customerVOresp.getCustomerId());
        customerVO.setAnonymousInteger(customerVOresp.getAnonymousInteger());
        customerVO.setNotificationId(intentJsonObject.getInt("notificationId"));

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
                    MyDialog.showSingleButtonBigContentDialog(Mandate_Revoke_Dialog.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        ok.dismiss();
                        finish();
                    }),customerResp.getDialogTitle(),customerResp.getAnonymousString());
                }
            }
        });

    }
}
package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.uav.autodebit.BO.ContactUsBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Help extends Base_Activity implements View.OnClickListener ,PermissionUtils.PermissionResultCallback{

    ImageView back_activity_button;
    //email,contact_request;
    PermissionUtils permissionUtils;
    //FloatingActionButton fabCall;
    ImageButton email,contact_request,fabCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        getSupportActionBar().hide();

        permissionUtils =new PermissionUtils(Help.this);
        contact_request=findViewById(R.id.contact_request);
        fabCall =findViewById(R.id.fabCall);
        email=findViewById(R.id.email);

        back_activity_button = findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);
        contact_request.setOnClickListener(this);
        email.setOnClickListener(this);
        fabCall.setOnClickListener(this);

    }


    public void backbuttonfun(){
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backbuttonfun();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageviewclickeffect);
        switch (view.getId()){
            case R.id.back_activity_button:
                backbuttonfun();
                break;
            case R.id.contact_request:
                contact_request.startAnimation(animation);
                sendUserDetail();
                break;
            case R.id.email:
                email.startAnimation(animation);
                sendMail();
                break;
            case R.id.fabCall:
                fabCall.startAnimation(animation);
                permissionUtils.check_permission(PermissionHandler.makeCallPermissionArrayList(Help.this),
                        Content_Message.MAKE_CALL_PERMISSION, ApplicationConstant.REQ_MAKE_CALL_PERMISSION);
                break;

        }
    }

    private void makeCallToSupport() {
        Gson gson = new Gson();
        LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE),
                LocalCacheVO.class);
        if (!localCacheVO.getCustomerSupportNumber().isEmpty()) {
            String phNo = localCacheVO.getCustomerSupportNumber();
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(phNo)));

            Log.e("cc_no", phNo + "");
        }
        else{
            Toast.makeText(Help.this,Content_Message.error_message,Toast.LENGTH_LONG).show();
        }
    }

    private void sendMail(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"support@autope.in"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Query Id : "+Session.getCustomerId(Help.this));
        intent.putExtra(Intent.EXTRA_TEXT, ""); // do this so some email clients don't complain about empty body.
        startActivity(intent);
    }


    private void sendUserDetail() {

        ConnectionVO connectionVO = ContactUsBO.saveContactRequest();
        HashMap<String, Object> params = new HashMap<String, Object>();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(this)));

        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {

                JSONObject response = (JSONObject) resp;
                Gson gson=new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Help.this,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                   MyDialog.showSingleButtonBigContentDialog(Help.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                       Utility.dismissDialog(Help.this, ((Dialog)ok));
                   }),customerVO.getDialogTitle(),customerVO.getAnonymousString());
                }
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);

    }

    @Override
    public void PermissionGranted(int request_code) {
        if(request_code==ApplicationConstant.REQ_MAKE_CALL_PERMISSION)
            makeCallToSupport();
    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {


    }

    @Override
    public void PermissionDenied(int request_code) {
    }

    @Override
    public void NeverAskAgain(int request_code) {

    }
}

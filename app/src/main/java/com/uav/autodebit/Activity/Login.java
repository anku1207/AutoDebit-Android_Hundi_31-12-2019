package com.uav.autodebit.Activity;

import android.accessibilityservice.FingerprintGestureController;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.hardware.fingerprint.FingerprintManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.fingerprint.FingerprintHandler;
import com.uav.autodebit.fingerprint.Fingerprint_Authentication;
import com.uav.autodebit.fingerprint.IFingerPrint;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OTPVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashMap;

import javax.crypto.KeyGenerator;

public class Login extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {
    EditText password,userid;
    TextView forgorpassword,fingerprinttext;
    Button newuserbtn;
    TextView loginviaotpbtn,loginbtn,newuser;
    Fingerprint_Authentication fingerprint_authentication ;
    LinearLayout fingerprintlayout;



    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        disableAutofill();

        PermissionHandler.checkpermission(Login.this);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            fingerprint_authentication =new Fingerprint_Authentication();
        }



/*

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.x = 0;
        params.height = 550;
        params.width = 550;
        params.y = 550;

        this.getWindow().setAttributes(params);
*/
        password=findViewById(R.id.password);
        forgorpassword=findViewById(R.id.forgorpassword);
        loginbtn=findViewById(R.id.loginbtn);

        newuser=findViewById(R.id.newuser);
        fingerprinttext=findViewById(R.id.fingerprinttext);
        fingerprintlayout=findViewById(R.id.fingerprintlayout);




        // newuserbtn=findViewById(R.id.newuserbtn);
        loginviaotpbtn=findViewById(R.id.loginviaotpbtn);
        userid=findViewById(R.id.userid);

        loginviaotpbtn.setOnClickListener(this);
//        newuserbtn.setOnClickListener(this);
        loginbtn.setOnClickListener(this);
        newuser.setOnClickListener(this);
        forgorpassword.setOnClickListener(this);
        password.setOnTouchListener(this);
        userid.setOnTouchListener(this);

        password.setInputType( InputType.TYPE_CLASS_NUMBER);
        password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        userid.setText(getIntent().getStringExtra("user_mobile"));


        if(Session.check_Exists_key(Login.this,Session.CACHE_USER_LOGINID)){
            userid.setText(Session.getSessionByKey(Login.this,Session.CACHE_USER_LOGINID));
        }
        if(!userid.getText().toString().equals("")){
            try {
                startLoginFingerPrint();
            }catch (Exception e){
                fingerprintlayout.removeAllViews();
            }

        }

    }

    @TargetApi(Build.VERSION_CODES.M)
    public void startLoginFingerPrint(){
        if(fingerprint_authentication==null)return;
        fingerprint_authentication.startFingerPrintScanning(this,fingerprintlayout, fingerprinttext, new IFingerPrint() {
            @Override
            public void onAuthenticationError(int errMsgId, CharSequence errString) {
            }
            @Override
            public void onAuthenticationFailed() {
                Toast.makeText(Login.this,
                        "Authentication failed",
                        Toast.LENGTH_LONG).show();
            }
            @Override
            public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                Toast.makeText(Login.this,
                        "Authentication help\n" + helpString,
                        Toast.LENGTH_LONG).show();
            }
            @Override
            public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
                Toast.makeText(Login.this,"Success!",
                        Toast.LENGTH_LONG).show();

                String type=null;
                boolean checkvalid=false;
                if(Utility.validatePattern(userid.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)==null){
                    type="email";
                    checkvalid=true;
                }
                if(!checkvalid){
                    if (Utility.validatePattern(userid.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)==null){
                        type="mobile";
                        checkvalid=true;
                    }
                }
                if(!checkvalid){

                    Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_Valid_User_Id,false);
                    return;
                }
                loginByFigerprint(userid.getText().toString(),type);
            }
        });
    }

    public void loginByFigerprint(String loginId,String type){
        VolleyUtils.makeJsonObjectRequest(Login.this,SignUpBO.loginByFigerprint(loginId,type), new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(Login.this,"Alert",sb.toString(),false);
                }else {
                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(Login.this,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(Login.this,Session.CACHE_CUSTOMER,json);
                    startActivity(new Intent(Login.this,Home.class));
                    finish();

                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fingerprint_authentication==null)return;
        fingerprint_authentication.cancel();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.newuser:
                startActivity(new Intent(Login.this,User_Registration.class));
                break;
            case R.id.forgorpassword:
                if(userid.getText().toString().equals("")){
                    Utility.showSingleButtonDialog(Login.this,"Alert", ErrorMsg.login_User_empty,false);
                    return;
                }else {
                    String type=null;
                    boolean checkvalid=false;
                    if(Utility.validatePattern(userid.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)==null){
                        type="email";
                        checkvalid=true;
                    }
                    if(!checkvalid){
                        if (Utility.validatePattern(userid.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)==null){
                            type="mobile";
                            checkvalid=true;
                        }
                    }
                    if(!checkvalid){
                        Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_Valid_User_Id,false);
                        return;
                    }
                    forgotPasswordOTP(type,userid.getText().toString().trim());
                }
                break;
            case R.id.loginbtn:
                if(userid.getText().toString().equals("") || password.getText().toString().equals("")){

                    if(userid.getText().toString().equals("")){
                        Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_User_empty,false);
                        return;
                    }
                    if(password.getText().toString().equals("")){
                        Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_Password_empty,false);
                        return;
                    }


                }else {
                    String type=null;
                    boolean checkvalid=false;
                    if(Utility.validatePattern(userid.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)==null){
                        type="email";
                        checkvalid=true;
                    }
                    if(!checkvalid){
                        if (Utility.validatePattern(userid.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)==null){
                            type="mobile";
                            checkvalid=true;
                        }
                    }
                    if(!checkvalid){
                        Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_Valid_User_Id,false);
                        return;
                    }
                    loginviapassword(userid.getText().toString().trim(),password.getText().toString().trim(),type);
                }
            break;
            case  R.id.  loginviaotpbtn:
                if(userid.getText().toString().equals("")){
                    Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_User_empty,false);
                    return;
                }else {
                    String type=null;
                    boolean checkvalid=false;
                    if(Utility.validatePattern(userid.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)==null){
                        type="email";
                        checkvalid=true;
                    }
                    if(!checkvalid){
                        if (Utility.validatePattern(userid.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)==null){
                            type="mobile";
                            checkvalid=true;
                        }

                    }
                    if(!checkvalid){
                        Utility.showSingleButtonDialog(Login.this,"Alert",ErrorMsg.login_Valid_User_Id,false);
                        return;
                    }
                    resendotpfun(type,userid.getText().toString().trim());
                }
                 break;
        }
    }


    public void loginviapassword(String loginid,String pass,String Type){
        VolleyUtils.makeJsonObjectRequest(this,SignUpBO.loginViaPassword(loginid,pass,Type,Session.getSessionByKey(this,Session.CACHE_TOKENID)), new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(Login.this,"Alert",sb.toString(),false);
                }else {
                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(Login.this,Session.CACHE_USER_LOGINID,userid.getText().toString());
                    Session.set_Data_Sharedprefence(Login.this,Session.CACHE_CUSTOMER,json);

                    startActivity(new Intent(Login.this,Home.class));
                    finish();

                }
            }
        });
    }

    public void forgotPasswordOTP(final String type, String value){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SignUpBO.forgotPasswordOTP();

        CustomerVO customerVO=new CustomerVO();
        if(type.equals("mobile")){
            customerVO.setMobileNumber(value);
        }else if(type.equals("email")){
            customerVO.setEmailId(value);
        }
        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
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
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);


                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Login.this,"Alert",sb.toString(),false);
                }else {
                    customerVO.setUserid(userid.getText().toString());
                    customerVO.setLoginType(type);
                    if(!customerVO.getStatus().getStatusId().equals(CustomerStatusVO.CREATED)){
                        Intent intent=new Intent(Login.this,Verify_OTP.class);
                        customerVO.setActionname("signInViaOTP");
                        String json = gson.toJson(customerVO); // myObject - instance of MyObject
                        intent.putExtra("resp",json);
                        startActivityForResult(intent,200);
                    }



                }
            }
        });

    }






    public void resendotpfun(final String type, String value){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SignUpBO.resendOTP();

        OTPVO otpvo=new OTPVO();
        if(type.equals("mobile")){
            otpvo.setMobileNo(value);
        }else if(type.equals("email")){
            otpvo.setEmailId(value);
        }
        Gson gson = new Gson();
        String json = gson.toJson(otpvo);
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
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);


                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Login.this,"Alert",sb.toString(),false);
                }else {

                    customerVO.setUserid(userid.getText().toString());
                    customerVO.setLoginType(type);

                    if(customerVO.getStatus().getStatusId().equals(CustomerStatusVO.CREATED) ){

                        Intent intent=new Intent(Login.this,Verify_OTP.class);
                        customerVO.setActionname("verifySignUp");
                       // customerVO.setAnonymousString(customerVO.getOtpExpiredMobile().toString());
                        String json = gson.toJson(customerVO); // myObject - instance of MyObject
                        intent.putExtra("resp",json);
                        startActivityForResult(intent,100);
                    }else {
                        Intent intent=new Intent(Login.this,Verify_OTP.class);
                        customerVO.setActionname("signInViaOTP");
                        String json = gson.toJson(customerVO); // myObject - instance of MyObject
                        intent.putExtra("resp",json);
                        startActivityForResult(intent,100);
                    }

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==100){
              if(data!=null){
                 if(Integer.parseInt(data.getStringExtra("key"))==(CustomerStatusVO.SIGNUP_MOBILE_OTP_VERIFIED)){
                      Intent intent =new Intent(Login.this,Password.class);
                      intent.putExtra("customerid",data.getStringExtra("value"));
                      intent.putExtra("methodname","setCustomerPassword");
                      startActivity(intent);
                  }else if(Integer.parseInt(data.getStringExtra("key"))==(CustomerStatusVO.CUSTOMER_VERFIED)){
                     Session.set_Data_Sharedprefence(Login.this,Session.CACHE_CUSTOMER,data.getStringExtra("value"));
                     Intent intent =new Intent(Login.this, Home.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                     startActivity(intent);
                 }
              }
            }

            if(requestCode==200){
                if(data!=null){
                    if(!data.getStringExtra("key").equals("")){
                        Intent intent =new Intent(Login.this,Password.class);
                        intent.putExtra("customerid",data.getStringExtra("value"));
                        intent.putExtra("methodname","setCustomerChangePassword");
                        startActivity(intent);
                    }
                }
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.userid:
                if(fingerprint_authentication==null)break;;
                fingerprint_authentication.cancel();

            case R.id.password:
                if(fingerprint_authentication==null)break;;
                fingerprint_authentication.cancel();
                break;
        }
        return false;
    }
}

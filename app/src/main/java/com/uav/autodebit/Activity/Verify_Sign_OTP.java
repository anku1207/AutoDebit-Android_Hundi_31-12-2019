package com.uav.autodebit.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OTPVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Verify_Sign_OTP extends Base_Activity implements View.OnClickListener ,TextWatcher ,View.OnFocusChangeListener,PermissionUtils.PermissionResultCallback , ActivityCompat.OnRequestPermissionsResultCallback {
    Button phonepinverifybtn,emailpinverifybtn;
    LinearLayout emailotplayout,mobileotplayout;
    CountDownTimer mobiletime = null;
    CountDownTimer emailtime = null;

    private EditText phone_pin_first_edittext,phone_pin_second_edittext,phone_pin_third_edittext,phone_pin_forth_edittext,email_pin_first_edittext
            ,email_pin_second_edittext,email_pin_third_edittext,email_pin_forth_edittext;

    String mobilenumber,emailid;

    PermissionUtils permissionUtils;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_sign_otp);
        getSupportActionBar().hide();


        permissionUtils=new PermissionUtils(Verify_Sign_OTP.this);
        permissionUtils.check_permission(PermissionHandler.readSmsPermissionArrayList(Verify_Sign_OTP.this), Content_Message.SMS_PERMISSION, ApplicationConstant.REQ_READ_SMS_PERMISSION);



        phonepinverifybtn=findViewById(R.id.phonepinverifybtn);
        emailpinverifybtn=findViewById(R.id.emailpinverifybtn);

        emailotplayout=findViewById(R.id.emailotplayout);
        mobileotplayout=findViewById(R.id.mobileotplayout);




        phone_pin_first_edittext = (EditText) findViewById(R.id.phone_pin_first_edittext);
        phone_pin_second_edittext = (EditText) findViewById(R.id.phone_pin_second_edittext);
        phone_pin_third_edittext = (EditText) findViewById(R.id.phone_pin_third_edittext);
        phone_pin_forth_edittext = (EditText) findViewById(R.id.phone_pin_forth_edittext);


        email_pin_first_edittext = (EditText) findViewById(R.id.email_pin_first_edittext);
        email_pin_second_edittext = (EditText) findViewById(R.id.email_pin_second_edittext);
        email_pin_third_edittext = (EditText) findViewById(R.id.email_pin_third_edittext);
        email_pin_forth_edittext = (EditText) findViewById(R.id.email_pin_forth_edittext);


        setPINListeners();




        try {
            Intent intent = getIntent();
            String object1=intent.getStringExtra("resp");

            Gson gson = new Gson();
            CustomerVO customerVO = gson.fromJson(object1, CustomerVO.class);

            if(customerVO.getStatus().getStatusId().equals(CustomerStatusVO.SIGNUP_MOBILE_OTP_VERIFIED)){
                emailid=customerVO.getEmailId();
                emailotplayout.setVisibility(View.VISIBLE);
                startTimer(Long.parseLong(customerVO.getOtpExpiredEmail().toString()),"emailotp");
            }/*else if(customerVO.getStatus().getStatusId().equals(CustomerStatusVO.SIGNUP_EMAIL_OTP_VERIFIED)){
                mobilenumber=customerVO.getMobileNumber();
                mobileotplayout.setVisibility(View.VISIBLE);
                startTimer(Long.parseLong(customerVO.getOtpExpiredMobile().toString()),"mobileotp");
            }*/else {

                emailid=customerVO.getEmailId();
                mobilenumber=customerVO.getMobileNumber();
                emailotplayout.setVisibility(View.VISIBLE);
                mobileotplayout.setVisibility(View.VISIBLE);
                startTimer(Long.parseLong(customerVO.getOtpExpiredMobile().toString()),"mobileotp");
                startTimer(Long.parseLong(customerVO.getOtpExpiredEmail().toString()),"emailotp");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.emailpinverifybtn:
                if(emailpinverifybtn.getTag().equals("resend")){
                    resendotpfun("email",emailid);
                }else {

                    if(!emailotpfiledcheck()) {
                        Utility.alertDialog(Verify_Sign_OTP.this,"Alert","Email OTP is Empty","Ok");
                        break;
                    }else {

                        signiotpverify("email",emailid);
                    }
                }
                break;
            case R.id.phonepinverifybtn:


                if(phonepinverifybtn.getTag().equals("resend")){
                    resendotpfun("mobile",mobilenumber);
                }else {

                    if(!mobileotpfiledcheck()) {
                        Utility.alertDialog(Verify_Sign_OTP.this,"Alert","Mobile OTP is Empty","Ok");
                        break;
                    }else {

                        signiotpverify("mobile",mobilenumber);
                    }


                }

                break;


        }

    }



    public void resendotpfun(final String type,String value){

        HashMap<String, Object> params = new HashMap<String, Object>();
        final ConnectionVO connectionVO = SignUpBO.resendOTP();

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

                if(customerVO.getStatusCode().equals("fail")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(Verify_Sign_OTP.this,customerVO.getDialogTitle(),sb.toString(),"Ok");

                }else {
                        if(type.equals("mobile")){
                            phonepinverifybtn.setTag("verify");
                            phonepinverifybtn.setText("Verify");
                           startTimer(Long.parseLong(customerVO.getAnonymousString().toString()),"mobileotp");
                        }else if(type.equals("email")){
                            startTimer(Long.parseLong(customerVO.getAnonymousString().toString()),"emailotp");
                        }
                }
            }
        });
    }

    //start timer function
    void startTimer(Long timeperiod,final String type) {

        CountDownTimer counttimetype=null;

        final CountDownTimer finalCounttimetype = counttimetype;
        counttimetype=new CountDownTimer(timeperiod*1000, 1000) {
            public void onTick(long millisUntilFinished) {

               /* String text =String.format(Locale.getDefault(), "%02d min: %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);*/


                String text=(millisUntilFinished/1000)+" sec";

                if(type.equals("mobileotp")){
                    phonepinverifybtn.setText(Html.fromHtml("<span>Verify</span><br><span>"+text+"</span>"));
                } else if(type.equals("emailotp")){
                    emailpinverifybtn.setText(Html.fromHtml("<span>Verify</span><br><span>"+text+"</span>"));
                }




            }
            public void onFinish() {
                if(type.equals("mobileotp")){
                    phonepinverifybtn.setTag("resend");
                    phonepinverifybtn.setText("Resend");

                }else if(type.equals("emailotp")){

                    emailpinverifybtn.setTag("resend");
                    emailpinverifybtn.setText("Resend");
                }



            }
        };
        if(type.equals("mobileotp")){
            mobiletime= counttimetype;
            counttimetype.start();
        } else if(type.equals("emailotp")){
            emailtime= counttimetype;
            counttimetype.start();
        }

    }
    //cancel timer
    void cancelTimer(String type) {
        if(type.equals("mobile")){
            if(mobiletime!=null)
                mobiletime.cancel();
                phonepinverifybtn.setTag("verify");
                phonepinverifybtn.setText("Verify");


        }else if(type.equals("email")){
            if(emailtime!=null)
                emailtime.cancel();
                emailpinverifybtn.setTag("verify");
                emailpinverifybtn.setText("verify");
        }

    }

    public boolean mobileotpfiledcheck(){
        if( phone_pin_first_edittext.getText().toString().equals("") ||phone_pin_second_edittext.getText().toString().equals("") || phone_pin_third_edittext.getText().equals("") ||
                phone_pin_forth_edittext.getText().toString().equals("")){
            return false;
        }
        return true;
    }

    public boolean emailotpfiledcheck(){

        if( email_pin_first_edittext.getText().toString().equals("") ||email_pin_second_edittext.getText().toString().equals("") || email_pin_third_edittext.getText().equals("") ||
                email_pin_forth_edittext.getText().toString().equals("")){
            return false;
        }
        return true;
    }


    public void signiotpverify(final String type,String value){


        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =SignUpBO.signotpverify();

        OTPVO otpvo=new OTPVO();
        if(type.equals("mobile")){
            otpvo.setMobileNo(value);
            otpvo.setOtp(getmobileotp());
        }else if(type.equals("email")){
            otpvo.setEmailId(value);
            otpvo.setOtp(getemailotp());
        }
        Gson gson = new Gson();
        String json = gson.toJson(otpvo);
        params.put("volley", json);
        connectionVO.setParams(params);

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
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(Verify_Sign_OTP.this,customerVO.getDialogTitle(),sb.toString(),"Ok");
                }else {


                    if(customerVO.getStatusCode().equals("440")){
                        if(type.equals("mobile")){
                            phonepinverifybtn.setTag("resend");
                            phonepinverifybtn.setText("Resend");
                                ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                                StringBuilder sb = new StringBuilder();
                                for(int i=0; i<error.size(); i++){
                                    sb.append(error.get(i)).append("\n");
                                }
                                 Utility.alertDialog(Verify_Sign_OTP.this,customerVO.getDialogTitle(),sb.toString(),"Ok");
                        }else if(type.equals("email")){
                            emailpinverifybtn.setTag("resend");
                            emailpinverifybtn.setText("Resend");
                                ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                                StringBuilder sb = new StringBuilder();
                                for(int i=0; i<error.size(); i++){
                                    sb.append(error.get(i)).append("\n");
                                }
                                Utility.alertDialog(Verify_Sign_OTP.this,customerVO.getDialogTitle(),sb.toString(),"Ok");
                        }
                        return;
                    }else if(customerVO.getStatus().getStatusId().equals(CustomerStatusVO.CUSTOMER_VERFIED)){

                        Intent intent =new Intent(Verify_Sign_OTP.this,PanVerification.class);
                        intent.putExtra("customerid",customerVO.getCustomerId().toString());
                        startActivity(intent);
                        finish();


                    }else {
                        if(type.equals("mobile")){
                            phonepinverifybtn.setText("success");
                            mobileotpeledisablev();
                        }else if(type.equals("email")){
                            emailpinverifybtn.setText("success");
                            emailotpeledisablev();
                        }

                    }
                }
            }
        });

    }

    public String getmobileotp(){
        return   phone_pin_first_edittext.getText().toString()+phone_pin_second_edittext.getText().toString()+phone_pin_third_edittext.getText()+
                phone_pin_forth_edittext.getText().toString();
    }

    public String getemailotp(){
        return   email_pin_first_edittext.getText().toString()+email_pin_second_edittext.getText().toString()+email_pin_third_edittext.getText()+
                email_pin_forth_edittext.getText().toString();
    }


    @Override
    public void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("otp"));
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase("otp")) {
                final String message = intent.getStringExtra("message");
                String otp=message.substring(0,4);
                cancelTimer("mobile");
                char[] array = otp.toCharArray();

                for(int i = 0; i < array.length; i++) {
                    if(i==0){
                        phone_pin_first_edittext.setText(String.valueOf(array[i]));
                    }else if(i==1){
                        phone_pin_second_edittext.setText(String.valueOf(array[i]));
                    }else if(i==2){
                        phone_pin_third_edittext.setText(String.valueOf(array[i]));
                    }else if(i==3){
                        phone_pin_forth_edittext.setText(String.valueOf(array[i]));
                    }
                }
               // phonepinverifybtn.performClick();
            }
        }
    };

    public void mobileotpeledisablev(){
        phone_pin_first_edittext.setEnabled(false);
        phone_pin_second_edittext.setEnabled(false);
        phone_pin_third_edittext.setEnabled(false);
        phone_pin_forth_edittext.setEnabled(false);
        phonepinverifybtn.setEnabled(false);
    }

    public void emailotpeledisablev(){
        emailpinverifybtn.setEnabled(false);
        email_pin_first_edittext.setEnabled(false);
        email_pin_second_edittext.setEnabled(false);
        email_pin_third_edittext.setEnabled(false);
        email_pin_forth_edittext.setEnabled(false);

    }



    private void setPINListeners() {


        phonepinverifybtn.setOnClickListener(this);
        emailpinverifybtn.setOnClickListener(this);

        phone_pin_first_edittext.addTextChangedListener(this);
        phone_pin_second_edittext.addTextChangedListener(this);
        phone_pin_third_edittext.addTextChangedListener(this);
        phone_pin_forth_edittext.addTextChangedListener(this);

        phone_pin_first_edittext.setOnFocusChangeListener(this);
        phone_pin_second_edittext.setOnFocusChangeListener(this);
        phone_pin_third_edittext.setOnFocusChangeListener(this);
        phone_pin_forth_edittext.setOnFocusChangeListener(this);

        email_pin_first_edittext.addTextChangedListener(this);
        email_pin_second_edittext.addTextChangedListener(this);
        email_pin_third_edittext.addTextChangedListener(this);
        email_pin_forth_edittext.addTextChangedListener(this);

        email_pin_first_edittext.setOnFocusChangeListener(this);
        email_pin_second_edittext.setOnFocusChangeListener(this);
        email_pin_third_edittext.setOnFocusChangeListener(this);
        email_pin_forth_edittext.setOnFocusChangeListener(this);





    }



    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence s, int i, int i1, int i2) {


        if (phone_pin_first_edittext.getText().hashCode() == s.hashCode()){
            if(!phone_pin_first_edittext.getText().toString().equals("")){
                phone_pin_second_edittext.requestFocus();
            }

        }else if(phone_pin_second_edittext.getText().hashCode() == s.hashCode()){
            if(!phone_pin_second_edittext.getText().toString().equals("")){
                phone_pin_third_edittext.requestFocus();
            }else {
                phone_pin_first_edittext.requestFocus();
            }
        }else if(phone_pin_third_edittext.getText().hashCode() == s.hashCode()){
            if(!phone_pin_third_edittext.getText().toString().equals("")){
                phone_pin_forth_edittext.requestFocus();
            }else {
                phone_pin_second_edittext.requestFocus();
            }
        }else if(phone_pin_forth_edittext.getText().hashCode() == s.hashCode()){
            if(!phone_pin_forth_edittext.getText().toString().equals("")){
                if(mobileotpfiledcheck()){
                    signiotpverify("mobile",mobilenumber);
                }
            }else {
                phone_pin_third_edittext.requestFocus();
            }
        }




        if (email_pin_first_edittext.getText().hashCode() == s.hashCode()){
            if(!email_pin_first_edittext.getText().toString().equals("")){
                email_pin_second_edittext.requestFocus();
            }

        }else if(email_pin_second_edittext.getText().hashCode() == s.hashCode()){
            if(!email_pin_second_edittext.getText().toString().equals("")){
                email_pin_third_edittext.requestFocus();
            }else {
                email_pin_first_edittext.requestFocus();
            }
        }else if(email_pin_third_edittext.getText().hashCode() == s.hashCode()){
            if(!email_pin_third_edittext.getText().toString().equals("")){
                email_pin_forth_edittext.requestFocus();
            }else {
                email_pin_second_edittext.requestFocus();
            }
        }else if(email_pin_forth_edittext.getText().hashCode() == s.hashCode()){
            if(!email_pin_forth_edittext.getText().toString().equals("")){
                if(emailotpfiledcheck()){
                    signiotpverify("email",emailid);
                }
            }else {
                email_pin_third_edittext.requestFocus();
            }
        }



    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

        final int id = v.getId();



        switch (id) {
            case R.id.phone_pin_first_edittext:
                if (hasFocus) {

                    phone_pin_first_edittext.setSelection(phone_pin_first_edittext.getText().length());
                    //showSoftKeyboard(mPinHiddenEditText);
                }
                break;

            case R.id.phone_pin_second_edittext:
                if (hasFocus) {
                    phone_pin_second_edittext.setSelection(phone_pin_second_edittext.getText().length());


                }
                break;

            case R.id.phone_pin_third_edittext:
                if (hasFocus) {

                    phone_pin_third_edittext.setSelection(phone_pin_third_edittext.getText().length());

                }
                break;

            case R.id.phone_pin_forth_edittext:
                if (hasFocus) {
                    phone_pin_forth_edittext.setSelection(phone_pin_forth_edittext.getText().length());



                }
                break;

            case R.id.email_pin_first_edittext:
                if (hasFocus) {

                    email_pin_first_edittext.setSelection(email_pin_first_edittext.getText().length());

                }
                break;
            case R.id.email_pin_second_edittext:
                if (hasFocus) {

                    email_pin_second_edittext.setSelection(email_pin_second_edittext.getText().length());

                }
                break;
            case R.id.email_pin_third_edittext:
                if (hasFocus) {

                    email_pin_third_edittext.setSelection(email_pin_third_edittext.getText().length());

                }
                break;
            case R.id.email_pin_forth_edittext:
                if (hasFocus) {

                    email_pin_forth_edittext.setSelection(email_pin_forth_edittext.getText().length());

                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void PermissionGranted(int request_code) {

    }

    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
        Toast.makeText(Verify_Sign_OTP.this, "Permission not granted!!!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void PermissionDenied(int request_code) {
        Toast.makeText(Verify_Sign_OTP.this, "Permission not granted!!!", Toast.LENGTH_LONG).show();
    }

    @Override
    public void NeverAskAgain(int request_code) {
    }


}

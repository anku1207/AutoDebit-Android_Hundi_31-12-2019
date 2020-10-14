package com.uav.autodebit.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.CountDownTimer;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.OTPVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Verify_Email_Otp_By_Id extends Base_Activity implements TextWatcher,View.OnFocusChangeListener {

    TextView resendotpbtn,otp_send;
    ImageView back_activity_button;
    Button otpverifybtn;
    LinearLayout mobileotplayout;
    CountDownTimer mobiletime = null;
    CountDownTimer emailtime = null;
    private EditText phone_pin_first_edittext,phone_pin_second_edittext,phone_pin_third_edittext,phone_pin_forth_edittext;

    String userEmail,useridtype,tokenId;
    boolean resendotp=false;

    String methodname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify__otp__by__id);
        getSupportActionBar().hide();




        otpverifybtn=findViewById(R.id.otpverifybtn);
        mobileotplayout=findViewById(R.id.mobileotplayout);
        back_activity_button=findViewById(R.id.back_activity_button);
        resendotpbtn=findViewById(R.id.resendotpbtn);
        otp_send=findViewById(R.id.otp_send);




        phone_pin_first_edittext = (EditText) findViewById(R.id.phone_pin_first_edittext);
        phone_pin_second_edittext = (EditText) findViewById(R.id.phone_pin_second_edittext);
        phone_pin_third_edittext = (EditText) findViewById(R.id.phone_pin_third_edittext);
        phone_pin_forth_edittext = (EditText) findViewById(R.id.phone_pin_forth_edittext);
        setPINListeners();


        try {
            Intent intent = getIntent();
            String object=intent.getStringExtra("resp");
            Gson gson = new Gson();
            CustomerVO customerVO = gson.fromJson(object, CustomerVO.class);
            useridtype=customerVO.getLoginType();
            userEmail=customerVO.getUserid();
            methodname=customerVO.getActionname();

            if(useridtype.equals("mobile")){
                otp_send.setText("OTP has been sent to "+Utility.maskString(customerVO.getMobileNumber(),3,7,'*'));
                startTimer(Long.parseLong(customerVO.getAnonymousString()),"mobileotp");
            }else if(useridtype.equals("email")){
                otp_send.setText("OTP has been sent to "+Utility.maskString(customerVO.getEmailId(),0,0,'*'));
                startTimer(Long.parseLong(customerVO.getAnonymousString()),"emailotp");
            }
        }catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(Verify_Email_Otp_By_Id.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(Verify_OTP.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }





        resendotpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resendotpbtn.setVisibility(View.GONE);
                resendotpfun(useridtype,userEmail+"");
            }
        });

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        otpverifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!mobileotpfiledcheck()) {
                    Utility.alertDialog(Verify_Email_Otp_By_Id.this,"Alert"," OTP is Empty","Ok");

                }else {
                    otpverify(useridtype,userEmail);
                }
            }
        });
    }
    private void setPINListeners() {
        phone_pin_first_edittext.addTextChangedListener(this);
        phone_pin_second_edittext.addTextChangedListener(this);
        phone_pin_third_edittext.addTextChangedListener(this);
        phone_pin_forth_edittext.addTextChangedListener(this);

        phone_pin_first_edittext.setOnFocusChangeListener(this);
        phone_pin_second_edittext.setOnFocusChangeListener(this);
        phone_pin_third_edittext.setOnFocusChangeListener(this);
        phone_pin_forth_edittext.setOnFocusChangeListener(this);
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
                    otpverify(useridtype,userEmail);
                }
            }else {
                phone_pin_third_edittext.requestFocus();
            }
        }

    }

    public boolean mobileotpfiledcheck(){
        if( phone_pin_first_edittext.getText().toString().equals("") ||phone_pin_second_edittext.getText().toString().equals("") || phone_pin_third_edittext.getText().equals("") ||
                phone_pin_forth_edittext.getText().toString().equals("")){
            return false;
        }
        return true;
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
            default:
                break;
        }

    }
    public String getmobileotp(){
        return   phone_pin_first_edittext.getText().toString()+phone_pin_second_edittext.getText().toString()+phone_pin_third_edittext.getText()+
                phone_pin_forth_edittext.getText().toString();
    }

    public void otpverify(String type,String email){



        HashMap<String, Object> params = new HashMap<String, Object>();


        final ConnectionVO connectionVO =new ConnectionVO();

        OTPVO otpvo =new OTPVO();
        otpvo.setOtp(getmobileotp());
        otpvo.setAnonymousInteger(Integer.parseInt(Session.getCustomerId(Verify_Email_Otp_By_Id.this)));
        otpvo.setEmailId(email);

        String json = new Gson().toJson(otpvo);
        params.put("volley", json);
        connectionVO.setParams(params);
        connectionVO.setMethodName(methodname);
        connectionVO.setRequestType(ConnectionVO.REQUEST_POST);
        connectionVO.setParams(params);


        Log.w("request",json);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                OTPVO  otpvo= gson.fromJson(response.toString(), OTPVO.class);


                if(otpvo.getStatusCode().equals("400")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    ArrayList error = (ArrayList) otpvo.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }


                    Utility.alertDialog(Verify_Email_Otp_By_Id.this,otpvo.getDialogTitle(),sb.toString(),"Ok");
                }else {
                    if(otpvo.getStatusCode().equals("440")){
                        resendotpbtn.setVisibility(View.VISIBLE);
                        ArrayList error = (ArrayList) otpvo.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                       Utility.alertDialog(Verify_Email_Otp_By_Id.this,otpvo.getDialogTitle(),sb.toString(),"Ok");
                    }else {
                        Intent intent12 = new Intent();
                        intent12.putExtra("msg",otpvo.getAnonymousString());
                        setResult(RESULT_OK,intent12);
                        finish() ;
                    }
                }
            }
        });
    }

    public void resendotpfun(final String type,String value){
        OTPApi.sendOtpToEmailVerification(Verify_Email_Otp_By_Id.this,value,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            CustomerVO customerVO = (CustomerVO) s;
            if(type.equals("mobile")){
                startTimer(Long.parseLong(customerVO.getAnonymousString()),"mobileotp");
            }else if(type.equals("email")){
                startTimer(Long.parseLong(customerVO.getAnonymousString()),"emailotp");
            }
        }));
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




    //start timer function
    void startTimer(Long timeperiod,final String type) {

        CountDownTimer counttimetype=null;

        final CountDownTimer finalCounttimetype = counttimetype;
        counttimetype=new CountDownTimer(timeperiod*1000, 1000) {
            public void onTick(long millisUntilFinished) {

               /* String text =String.format(Locale.getDefault(), "%02d min: %02d sec",
                        TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60,
                        TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60);*/


             /*   String text=(millisUntilFinished/1000)+" sec";

                if(type.equals("mobileotp")){
                    otpverifybtn.setText(Html.fromHtml("<span>Verify</span><br><span>"+text+"</span>"));
                } else if(type.equals("emailotp")){
                    otpverifybtn.setText(Html.fromHtml("<span>Verify</span><br><span>"+text+"</span>"));
                }
*/



            }
            public void onFinish() {
                if(type.equals("mobileotp")){
                    /*otpverifybtn.setTag("resend");
                    otpverifybtn.setText("Resend");*/

                    resendotpbtn.setVisibility(View.VISIBLE);

                }else if(type.equals("emailotp")){

                    /*otpverifybtn.setTag("resend");
                    otpverifybtn.setText("Resend");*/
                    resendotpbtn.setVisibility(View.VISIBLE);

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
            resendotpbtn.setVisibility(View.GONE);

           /* otpverifybtn.setTag("verify");
            otpverifybtn.setText("Verify");*/


        }else if(type.equals("email")){
            if(emailtime!=null)
                emailtime.cancel();
           /* otpverifybtn.setTag("verify");
            otpverifybtn.setText("verify");*/
            resendotpbtn.setVisibility(View.GONE);
        }

    }
}


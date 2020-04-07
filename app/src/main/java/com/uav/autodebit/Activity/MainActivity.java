package com.uav.autodebit.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;

import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends Base_Activity {
    String request=null;


    public static final String MOBILENO_VALIDATION="{\"pattern\":\"^[6-9][0-9]{9}$\", \"msg\":  \"Mobile No. accepts only  numbers and length should be 10 (first number to start with [6-9])}\"}";
    public static final String SOMETHINGWRONG = "Something went wrong. Please Try Again";
    public static final String EMAIL_VALIDATION="{\"pattern\":\"^[a-zA-Z0-9][a-zA-Z0-9._-]+@[a-zA-Z0-9][a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$\",  \"msg\": \"Enter a valid email address\"}";
    /*String[] mPermission = {
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.INTERNET,
            Manifest.permission.SEND_SMS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS};*/


    LinearLayout loginpasswordlayout,signuplayout1,otpverifylayout,forgetlayout1,otplayout1,otpEmailverifylayout;
    CountDownTimer mobiletime = null;

    CountDownTimer emailtime = null;




    EditText username,emilsignup,signmobile,password,forgetmobileno,otpmobilenoormail,verifyotpid,verifyemailid;
    TextView validemobile,validemail,valideforgetmobileno,valideotpmobileno,valideverifyotpid,valideverifyemailid;
    ImageButton showpassbtn;

    Button signupbtn,otpverifybtn,emailverifybtn,otploginbtn;
    private static final int REQUEST_CODE_PERMISSION = 2;



    @TargetApi(Build.VERSION_CODES.N)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("Req Code for permission", "" + requestCode);
       /* if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[4] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[5] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[6] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permissions granted!!!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "Permission not granted!!!", Toast.LENGTH_LONG).show();
            }
        }*/



        for (String permission : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) MainActivity.this, permission)) {
                //denied
                Log.w("denied", permission);


            } else {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, permission) == PackageManager.PERMISSION_GRANTED) {
                    //allowed
                    Log.w("allowed", permission);


                } else {
                    //set to never ask again


                    Log.e("set to never ask again", permission);

                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + MainActivity.this.getPackageName()));
                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    MainActivity.this.startActivity(intent);
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//
//        try {
//            if (android.os.Build.VERSION.SDK_INT >= 23) {
//                if (ActivityCompat.checkSelfPermission(this, mPermission[0])
//                        != PackageManager.PERMISSION_GRANTED ||
//                        ActivityCompat.checkSelfPermission(this, mPermission[1])
//                                != PackageManager.PERMISSION_GRANTED ||
//                        ActivityCompat.checkSelfPermission(this, mPermission[2])
//                                != PackageManager.PERMISSION_GRANTED ||
//                        ActivityCompat.checkSelfPermission(this, mPermission[3])
//                                != PackageManager.PERMISSION_GRANTED ||
//                        ActivityCompat.checkSelfPermission(this, mPermission[4])
//                                != PackageManager.PERMISSION_GRANTED ||
//                        ActivityCompat.checkSelfPermission(this, mPermission[5])
//                                != PackageManager.PERMISSION_GRANTED ||
//                        ActivityCompat.checkSelfPermission(this, mPermission[6])
//                                != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, mPermission[7])
//                        != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, mPermission[8])
//                        != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, mPermission[9])
//                        != PackageManager.PERMISSION_GRANTED ||
//                ActivityCompat.checkSelfPermission(this, mPermission[10])
//                        != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(this, mPermission, REQUEST_CODE_PERMISSION);
//                    // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }




        loginpasswordlayout=findViewById(R.id.loginpasswordlayout1);
        signuplayout1=findViewById(R.id.signuplayout1);
        otpverifylayout=findViewById(R.id.otpverifylayout);
        forgetlayout1=findViewById(R.id.forgetlayout1);
        otplayout1=findViewById(R.id.otplayout1);
        otpEmailverifylayout=findViewById(R.id.otpEmailverifylayout);


        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        showpassbtn=findViewById(R.id.showpassbtn);



        emilsignup=findViewById(R.id.emilsignup);
        signmobile=findViewById(R.id.signmobile);
        validemobile=findViewById(R.id.validemobile);
        validemail=findViewById(R.id.validemail);

        forgetmobileno=findViewById(R.id.forgetmobileno);
        valideforgetmobileno=findViewById(R.id.valideforgetmobileno);


        //signin layout
        otpmobilenoormail=findViewById(R.id.otpmobilenoormail);
        valideotpmobileno=findViewById(R.id.valideotpmobileno);
        otploginbtn=findViewById(R.id.otploginbtn);



        // OTP verify layout
        verifyotpid=findViewById(R.id.verifyotpid);
        valideverifyotpid=findViewById(R.id.valideverifyotpid);
        otpverifybtn=findViewById(R.id.otpverifybtn);




        // Email verify layout
        verifyemailid=findViewById(R.id.verifyemailid);
        valideverifyemailid=findViewById(R.id.valideverifyemailid);
        emailverifybtn=findViewById(R.id.emailverifybtn);



        signupbtn=findViewById(R.id.signupbtn);



        valideverifyotpid.setVisibility(View.VISIBLE);


        signuplayout1.setVisibility(View.GONE);
        otpverifylayout.setVisibility(View.GONE);
        forgetlayout1.setVisibility(View.GONE);
        otplayout1.setVisibility(View.GONE);
        otpEmailverifylayout.setVisibility(View.GONE);



        final RadioGroup rg = (RadioGroup) findViewById(R.id.rg);

        rg.check(R.id.passwordlogin);
        passwordloginglayout();

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton rb = (RadioButton) radioGroup.findViewById(i);
                    switch(rb.getText().toString().toLowerCase()) {

                    case "sign in":
                        passwordloginglayout();

                        break;
                    case "sign in via otp":
                        loginotplayout();

                        break;
                    case "forgot password":

                        forgetpasswordlayout();

                        break;
                    case "sign up":
                        signuplayout();


                        signupbtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                signupuser();
                            }
                        });

                        break;
                    default:
                        // code block
                }
            }
        });


        otpverifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(otpverifybtn.getTag().equals("resend")){
                    otpverifybtn.setTag("verify");
                    otpverifybtn.setText("Verify");
                    verifyotpid.setEnabled(true);
                    valideverifyotpid.setText("");

                    if(request.equals("signup")){
                        resendotpfun("mobile",signmobile,"signup");
                    }else if(request.equals("signin")){
                        resendotpfun("mobile",otpmobilenoormail,"signin");
                    }


                }else {

                    if(request.equals("signup")){
                        if(verifyotpid.getText().toString().equals("")){
                            valideverifyotpid.setText("OTP Is Empty Not allow");
                        }else {
                            cancelTimer("mobile");
                            signinmobilotpverify("mobile");
                        }
                    }else if(request.equals("signin")){
                        if(verifyotpid.getText().toString().equals("")){
                            valideverifyotpid.setText("OTP Is Empty Not allow");
                        }else {
                            signincustomer("mobile");
                        }
                    }


                }
            }
        });

        emailverifybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(emailverifybtn.getTag().equals("resend")){
                    emailverifybtn.setTag("verify");
                    emailverifybtn.setText("Verify");
                    verifyemailid.setEnabled(true);
                    valideverifyemailid.setText("");

                    if(request.equals("signup")){
                        resendotpfun("email",emilsignup,"signup");
                    }else if(request.equals("signin")){
                        resendotpfun("email",otpmobilenoormail,"signin");
                    }



                }else {

                    if(request.equals("signup")){
                        if(verifyemailid.getText().toString().equals("")){
                            valideverifyemailid.setText("OTP Is Empty Not allow");

                        }else {
                            cancelTimer("email");
                            signinmobilotpverify("email");
                        }
                    }else if(request.equals("signin")){
                        if(verifyemailid.getText().toString().equals("")){
                            valideverifyemailid.setText("OTP Is Empty Not allow");
                        }else {
                            signincustomer("email");
                        }
                    }





                }
            }
        });


    }



    public void signupuser(){
        validemail.setVisibility(View.GONE);
        validemobile.setVisibility(View.GONE);
        validemail.setText("");
        validemobile.setText("");
        boolean checkvalid=true;
        if(validatePattern(emilsignup.getText().toString().trim(),EMAIL_VALIDATION)!=null){
            validemail.setVisibility(View.VISIBLE);
            validemail.setText(validatePattern(emilsignup.getText().toString().trim(),EMAIL_VALIDATION));
            checkvalid=false;
        }
        if (validatePattern(signmobile.getText().toString().trim(),MOBILENO_VALIDATION)!=null){
            validemobile.setVisibility(View.VISIBLE);
            validemobile.setText(validatePattern(signmobile.getText().toString().trim(),MOBILENO_VALIDATION));
            checkvalid=false;
        }

        if(!checkvalid) return;

         signuser();

    }



    public  void  signuplayout(){
        validemail.setVisibility(View.GONE);
        validemobile.setVisibility(View.GONE);
        validemail.setText("");
        validemobile.setText("");
        signmobile.setText("");
        emilsignup.setText("");


        otpverifylayout.setVisibility(View.GONE);
        signuplayout1.setVisibility(View.VISIBLE);
        loginpasswordlayout.setVisibility(View.GONE);
        forgetlayout1.setVisibility(View.GONE);
        otplayout1.setVisibility(View.GONE);
        otpEmailverifylayout.setVisibility(View.GONE);



    }




    public void signincustomer(final String type){



        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =SignUpBO.signInOTP();

        if(type.equals("mobile")){
            params.put("mobileNo",otpmobilenoormail.getText().toString());
            params.put("otp",verifyotpid.getText().toString());
        }else if(type.equals("email")){
            params.put("emailId",otpmobilenoormail.getText().toString());
            params.put("otp",verifyemailid.getText().toString());
        }

        connectionVO.setParams(params);


        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                if(response.get("status").equals("fail")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    JSONArray error = response.getJSONArray("error");
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.length(); i++){
                        sb.append(error.get(i)).append("\n");
                    }

                    if(type.equals("mobile")){
                        valideverifyotpid.setText(sb.toString());
                        valideverifyotpid.setVisibility(View.VISIBLE);
                    }else if(type.equals("email")){
                        valideverifyemailid.setText(sb.toString());
                        valideverifyemailid.setVisibility(View.VISIBLE);
                    }
                }else {
                    request="signin";
                    if(response.get("status").equals("expired")){

                        if(type.equals("mobile")){
                            otpverifybtn.setTag("resend");
                            otpverifybtn.setText("Resend");
                            valideverifyotpid.setText(response.getString("message"));
                            verifyotpid.setEnabled(false);
                        }else if(type.equals("email")){
                            emailverifybtn.setTag("resend");
                            emailverifybtn.setText("Resend");
                            valideverifyemailid.setText(response.getString("message"));
                            verifyemailid.setEnabled(false);
                        }
                        return;
                    }else if(response.get("status").equals("success")){
                        Intent intent =new Intent(MainActivity.this,PanVerification.class);
                        startActivity(intent);


                    }
                 }
            }
        });

    }



    public void resendotpfun(final String type,EditText editText,final String requesttype){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =SignUpBO.resendOTP();

        if(type.equals("mobile")){
            params.put("mobileNo",editText.getText().toString());
        }else if(type.equals("email")){
            params.put("emailId",editText.getText().toString());
        }

        connectionVO.setParams(params);


        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                if(response.get("status").equals("fail")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    JSONArray error = response.getJSONArray("error");
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.length(); i++){
                        sb.append(error.get(i)).append("\n");
                    }

                    if(type.equals("mobile")){
                        valideverifyotpid.setText(sb.toString());
                        valideverifyotpid.setVisibility(View.VISIBLE);
                    }else if(type.equals("email")){
                        valideverifyemailid.setText(sb.toString());
                        valideverifyemailid.setVisibility(View.VISIBLE);
                    }
                }else {

                    if(requesttype.equals("signin")){
                        request="signin";

                        if(type.equals("mobile")){
                            otpverifylayout.setVisibility(View.VISIBLE);
                            valideverifyotpid.setText("");
                            otpverifybtn.setTag("verify");
                            otpverifybtn.setText("Verify");
                            startTimer(Long.parseLong(response.getString("timeOutMobile")),"mobileotp");
                            otpverifybtn.setEnabled(true);
                        }else if(type.equals("email")){

                            otpEmailverifylayout.setVisibility(View.VISIBLE);
                            valideverifyemailid.setText("");
                            emailverifybtn.setTag("verify");
                            emailverifybtn.setText("Verify");
                            startTimer(Long.parseLong(response.getString("timeOutEmail")),"emailotp");
                            emailverifybtn.setEnabled(true);

                        }
                        return;

                    }if(requesttype.equals("signup")){
                        request="signup";
                        if(type.equals("mobile")){
                            startTimer(Long.parseLong(response.getString("timeOutMobile")),"mobileotp");
                        }else if(type.equals("email")){
                            startTimer(Long.parseLong(response.getString("timeOutEmail")),"emailotp");
                        }

                    }





                }
            }
        });


    }

    public void signinmobilotpverify(final String type){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =SignUpBO.signotpverify();

        if(type.equals("mobile")){
            params.put("mobileNo",signmobile.getText().toString());
            params.put("otp",verifyotpid.getText().toString());
        }else if(type.equals("email")){
            params.put("emailId",emilsignup.getText().toString());
            params.put("otp",verifyemailid.getText().toString());
        }



        connectionVO.setParams(params);




        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                if(type.equals("mobile")){
                    valideverifyotpid.setVisibility(View.VISIBLE);
                }else if(type.equals("email")){
                    valideverifyemailid.setVisibility(View.VISIBLE);
                }

                if(response.get("status").equals("fail")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                    JSONArray error = response.getJSONArray("error");
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.length(); i++){
                        sb.append(error.get(i)).append("\n");
                    }


                    if(type.equals("mobile")){
                        valideverifyotpid.setText(sb.toString());
                    }else if(type.equals("email")){
                        valideverifyemailid.setText(sb.toString());
                    }



                }else {


                    if(response.get("status").equals("expired")){
                        request="signup";
                        if(type.equals("mobile")){
                            otpverifybtn.setTag("resend");
                            otpverifybtn.setText("Resend");
                            valideverifyotpid.setText(response.getString("message"));
                            verifyotpid.setEnabled(false);
                        }else if(type.equals("email")){
                            emailverifybtn.setTag("resend");
                            emailverifybtn.setText("Resend");
                            valideverifyemailid.setText(response.getString("message"));
                            verifyemailid.setEnabled(false);
                        }
                        return;
                    }else if(response.get("status").equals(ApplicationConstant.STATUS_SIGNUP_ACTIVE)){
                                Intent intent =new Intent(MainActivity.this,PanVerification.class);
                                startActivity(intent);


                    }else {

                        if(type.equals("mobile")){

                            otpverifybtn.setText("success");
                            otpverifybtn.setEnabled(false);
                            verifyotpid.setEnabled(false);
                        }else if(type.equals("email")){
                            emailverifybtn.setText("success");
                            emailverifybtn.setEnabled(false);
                            verifyemailid.setEnabled(false);

                        }


                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                        builder.setMessage(response.getString("message"))
                                .setTitle("Status")
                                .setIcon(android.R.drawable.ic_dialog_alert)


                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    public void onClick(DialogInterface dialog, int id) {


                                    }
                                });
                        AlertDialog alert= builder.create();
                        alert.setCanceledOnTouchOutside(false);
                        alert.setCancelable(false);
                        alert.show();




                    }



                }
            }
        });

    }








    public  void  loginotplayout(){
        valideotpmobileno.setVisibility(View.GONE);

        otpmobilenoormail.setText("");
        valideotpmobileno.setText("");


        signuplayout1.setVisibility(View.GONE);
        loginpasswordlayout.setVisibility(View.GONE);
        forgetlayout1.setVisibility(View.GONE);
        otplayout1.setVisibility(View.VISIBLE);
        otpverifylayout.setVisibility(View.GONE);
        otpEmailverifylayout.setVisibility(View.GONE);

        otploginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(otpmobilenoormail.getText().toString().equals("")){
                    valideotpmobileno.setText("this filed is required");
                    valideotpmobileno.setVisibility(View.VISIBLE);
                }else {

                    String type=null;

                    boolean checkvalid=false;
                    if(validatePattern(otpmobilenoormail.getText().toString().trim(),EMAIL_VALIDATION)==null){
                        type="email";
                        checkvalid=true;
                    }
                    if(!checkvalid){

                        if (validatePattern(otpmobilenoormail.getText().toString().trim(),MOBILENO_VALIDATION)==null){
                            type="mobile";
                            checkvalid=true;
                        }

                    }
                    if(!checkvalid){
                        valideotpmobileno.setText("Error");
                        valideotpmobileno.setVisibility(View.VISIBLE);
                        return;
                    }
                    resendotpfun(type,otpmobilenoormail,"signin");
                }
            }
        });



    }

    public  void  forgetpasswordlayout(){

        forgetmobileno.setText("");
        valideforgetmobileno.setText("");
        valideforgetmobileno.setVisibility(View.GONE);


        signuplayout1.setVisibility(View.GONE);
        loginpasswordlayout.setVisibility(View.GONE);
        forgetlayout1.setVisibility(View.VISIBLE);
        otplayout1.setVisibility(View.GONE);
        otpverifylayout.setVisibility(View.GONE);
        otpEmailverifylayout.setVisibility(View.GONE);



        }


    public  void  passwordloginglayout(){
        username.setText("");
        password.setText("");


        loginpasswordlayout.setVisibility(View.VISIBLE);
        forgetlayout1.setVisibility(View.GONE);
        signuplayout1.setVisibility(View.GONE);
        otplayout1.setVisibility(View.GONE);
        otplayout1.setVisibility(View.GONE);
        otpverifylayout.setVisibility(View.GONE);
        otpEmailverifylayout.setVisibility(View.GONE);




        showpassbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                    password.setInputType( InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    showpassbtn.setImageResource(R.drawable.ava_showpin);

                }else {
                    password.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                    showpassbtn.setImageResource(R.drawable.ava_hidepin);

                }
                password.setSelection(password.getText().length());

            }
        });



    }



    public  void signuser(){
        VolleyUtils.makeJsonObjectRequest(this, SignUpBO.signuser(), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                Log.w("responsesignup",response.toString());
                if(response.get("status").equals("fail")){
                    Utility.furnishErrorMsg(  "Fail" ,response, MainActivity.this);

                }else {
                    request="signup";
                    emilsignup.setEnabled(false);
                    signmobile.setEnabled(false);
                    signupbtn.setVisibility(View.GONE);

                    if(response.getString("status").equals(ApplicationConstant.STATUS_PAN_VERIFIED)){
                        Toast.makeText(MainActivity.this, ApplicationConstant.STATUS_PAN_VERIFIED, Toast.LENGTH_SHORT).show();
                    }else if(response.getString("status").equals(ApplicationConstant.STATUS_SIGNUP_ACTIVE)){
                        Intent intent =new Intent(MainActivity.this,PanVerification.class);
                        startActivity(intent);
                    }else if(response.getString("status").equals(ApplicationConstant.STATUS_SIGNUP_MOBILE_OTP_VERIFY)){
                        otpEmailverifylayout.setVisibility(View.VISIBLE);
                        emilsignup.setEnabled(false);
                        startTimer(Long.parseLong(response.getString("timeOutEmail")),"emailotp");
                    }else if(response.getString("status").equals(ApplicationConstant.STATUS_SIGNUP_EMAIL_OTP_VERIFY)){
                        otpverifylayout.setVisibility(View.VISIBLE);
                        signmobile.setEnabled(false);
                        startTimer(Long.parseLong(response.getString("timeOutMobile")),"mobileotp");
                    }else {
                        otpverifylayout.setVisibility(View.VISIBLE);
                        otpEmailverifylayout.setVisibility(View.VISIBLE);
                       startTimer(Long.parseLong(response.getString("timeOutMobile")),"mobileotp");
                        startTimer(Long.parseLong(response.getString("timeOutEmail")),"emailotp");
                    }





                }
            }
        });
    }




    public static String validatePattern(String value, String key ){
        try{
            JSONObject valid = new JSONObject(key);
            if(value == null){
                return  valid.getString("msg");
            }
            Pattern ptrn = Pattern.compile(valid.getString("pattern"));
            Matcher matcher = ptrn.matcher(value);
            String errorMsg=null;

            if(! matcher.matches()){
                errorMsg= valid.getString("msg");
            }
            return errorMsg;
        }catch(Exception e){

            Log.w("error",e);
            return   SOMETHINGWRONG;
        }
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
                      otpverifybtn.setText(Html.fromHtml("<span>Verify</span><br><span>"+text+"</span>"));
                } else if(type.equals("emailotp")){
                     emailverifybtn.setText(Html.fromHtml("<span>Verify</span><br><span>"+text+"</span>"));
                }




            }
            public void onFinish() {
                if(type.equals("mobileotp")){
                    otpverifybtn.setTag("resend");
                    otpverifybtn.setText("Resend");
                    verifyotpid.setEnabled(false);
                }else if(type.equals("emailotp")){
                    verifyemailid.setEnabled(false);
                    emailverifybtn.setTag("resend");
                    emailverifybtn.setText("Resend");
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
            valideverifyotpid.setText("");
        }else if(type.equals("email")){
            if(emailtime!=null)
            emailtime.cancel();
            valideverifyemailid.setText("");
        }

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
                verifyotpid.setText(message.substring(0,4));
                cancelTimer("mobile");
                otpverifybtn.performClick();
            }
        }
    };

}

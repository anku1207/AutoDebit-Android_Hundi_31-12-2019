package com.uav.autodebit.Activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
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

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class User_Registration extends AppCompatActivity {
    Button otpgeneratebtn;
    EditText username,userphone,useremail;
    Intent intent=null;


    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user__registration);
        getSupportActionBar().hide();

        disableAutofill();

        username=findViewById(R.id.username);
        userphone=findViewById(R.id.userphone);
        useremail=findViewById(R.id.useremail);
        otpgeneratebtn=findViewById(R.id.otpgeneratebtn);



        otpgeneratebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean inputvalid=true;
                if(username.getText().toString().equals("")){
                    username.setError(ErrorMsg.user_Registration_Filed_Required);
                    inputvalid=false;
                }
                if(userphone.getText().toString().equals("")){
                    userphone.setError(ErrorMsg.user_Registration_Filed_Required);
                    inputvalid=false;
                }
                if(useremail.getText().toString().equals("")){
                    useremail.setError(ErrorMsg.user_Registration_Filed_Required);
                    inputvalid=false;
                }

                if(!useremail.getText().toString().equals("") && Utility.validatePattern(useremail.getText().toString().trim(), ApplicationConstant.EMAIL_VALIDATION)!=null){

                    useremail.setError(Utility.validatePattern(useremail.getText().toString().trim(),ApplicationConstant.EMAIL_VALIDATION));
                    inputvalid=false;
                }
                if (!userphone.getText().toString().equals("") &&  Utility.validatePattern(userphone.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)!=null){
                    userphone.setError(Utility.validatePattern(userphone.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                    inputvalid=false;
                }

                if(!inputvalid) return;

                verifydialog();
                //signuser();


            }
        });
    }








    public static void confirmationDialog(com.uav.autodebit.util.DialogInterface mcxtinter, Context context , JSONArray jsonArray , String Msg , String title, int[] textviewsize, String... buttons){
        String leftButton= (buttons.length==0 ?"Modify":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton=(buttons.length<=1 ?"Next":buttons[1]);//(rightButton==null?"Next":rightButton);

        float titleTextView=(textviewsize.length==0?1:textviewsize[0]);
        float valueTextView=(textviewsize.length==1?1:textviewsize[1]);

        try{
            final com.uav.autodebit.util.DialogInterface dialogInterface =mcxtinter;
            final Dialog var3 = new Dialog(context);
            var3.requestWindowFeature(1);
            var3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            var3.setContentView(R.layout.confirmation_dialog);
            var3.setCanceledOnTouchOutside(false);
            var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            LinearLayout mainlayout =var3.findViewById(R.id.mainlayout);
            TextView dialog_title=var3.findViewById(R.id.dialog_title);
            Button modify=var3.findViewById(R.id.modify);
            Button next=var3.findViewById(R.id.next);
            ImageView canceldialog=var3.findViewById(R.id.canceldialog);

            modify.setText(leftButton);
            next.setText(rightButton);

            dialog_title.setText(title);

            Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinssemibold);


            if(Msg==null){
                for(int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObject =jsonArray.getJSONObject(i);
                    LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_layout));

                    TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                    text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) titleTextView));
                    text.setText(jsonObject.getString("key"));
                    text.setMaxLines(1);
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setTypeface(typeface);
                    text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                    TextView text1 = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                    text1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));
                    text1.setText(" : ");
                    text1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);



                    TextView value = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_value));
                    value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,(float) valueTextView));
                    value.setText(jsonObject.getString("value"));
                    value.setTypeface(typeface);

                    et.addView(text);
                    et.addView(text1);
                    et.addView(value);
                    mainlayout.addView(et);
                }
            }else {
                LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_layout));
                TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_text_flied));
                text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                text.setText(Msg);
                text.setTypeface(typeface);


                et.addView(text);
                mainlayout.addView(et);
            }




            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(var3.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.modify(var3);
                }
            });
            canceldialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.modify(var3);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.confirm(var3);

                }
            });

            if(!var3.isShowing())  var3.show();
            var3.getWindow().setAttributes(lp);

        }catch (Exception e){
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    public void verifydialog(){

        try{
            JSONArray jsonArray=new JSONArray();
            JSONObject object =new JSONObject();
            object.put("key","Name");
            object.put("value",username.getText().toString());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Email");
            object.put("value",useremail.getText().toString());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Mobile No.");
            object.put("value",userphone.getText().toString());
            jsonArray.put(object);

            int textviewsize[] = {1,3};
            confirmationDialog(new DialogInterface() {
                @Override
                public void confirm(Dialog dialog) {
                    dialog.dismiss();
                    signuser();
                }
                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                }

            },User_Registration.this,jsonArray,null,"Please Confirm Detail",textviewsize);
        }catch (Exception e){
            Utility.exceptionAlertDialog(User_Registration.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }




    public  void signuser(){


        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SignUpBO.signuser();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setEmailId(useremail.getText().toString().trim());
        customerVO.setMobileNumber(userphone.getText().toString().trim());
        customerVO.setName(username.getText().toString().trim());

        //28-11-2019
        customerVO.setTokenId(Session.getSessionByKey(this,Session.CACHE_TOKENID));

        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);


        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);


                Log.w("responsesignup",response.toString());
                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(User_Registration.this,"Alert",sb.toString(),false);

                }else {
                    Session.set_Data_Sharedprefence_BoolenvValue(User_Registration.this,Session.CACHE_IS_NEW_USER,false);


                    customerVO.setUserid(userphone.getText().toString().trim());
                    customerVO.setLoginType("mobile");

                    if(customerVO.getStatus().getStatusId().equals(CustomerStatusVO.CREATED)){
                        otpverifyactivity(customerVO);
                    }else{
                        ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }

                        Utility.showSingleButtonDialogconfirmation(User_Registration.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                            Intent intent =new Intent(User_Registration.this, Login.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("user_mobile",userphone.getText().toString());
                            startActivities(new Intent[]{intent});
                        }), "Alert", sb.toString());






                    }
                }
            }
        });
    }

    public void otpverifyactivity(CustomerVO  customerVO){
        Gson gson = new Gson();


        Intent intent=new Intent(User_Registration.this,Verify_OTP.class);
        customerVO.setActionname("verifySignUp");
        customerVO.setAnonymousString(customerVO.getOtpExpiredMobile().toString());

        String json = gson.toJson(customerVO); // myObject - instance of MyObject
        intent.putExtra("resp",json);
        startActivityForResult(intent,100);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==100){
                if(data!=null){
                    if( Integer.parseInt(data.getStringExtra("key"))==(CustomerStatusVO.SIGNUP_MOBILE_OTP_VERIFIED)){
                        Intent intent =new Intent(User_Registration.this,Password.class);
                        intent.putExtra("customerid",data.getStringExtra("value"));
                        intent.putExtra("methodname","setCustomerPassword");
                        startActivity(intent);
                        finish();
                    }else if(Integer.parseInt(data.getStringExtra("key"))==(CustomerStatusVO.CUSTOMER_VERFIED)){
                        startActivity(new Intent(getApplicationContext(),Login.class));
                        finish();
                    }
                }

            }
        }
    }

}

package com.uav.autodebit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
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

public class ChangePassword extends Base_Activity {


    UAVEditText newpassword;
    Button loginbtn;
    String customerid,methodname;
    UAVEditText repeatpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().hide();

        newpassword=findViewById(R.id.newpassword);
        repeatpassword=findViewById(R.id.repeatpassword);
        loginbtn=findViewById(R.id.loginbtn);

        Intent intent =getIntent();
        Gson gson=new Gson();
         customerid=intent.getStringExtra("customerid");

        repeatpassword.setInputType (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        newpassword.setInputType( InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
        repeatpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        newpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());

        methodname=intent.getStringExtra("methodname");

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid=true;

                if(newpassword.getText().toString().equals("") && repeatpassword.getText().toString().equals("")){
                    Utility.showSingleButtonDialog(ChangePassword.this,"Error !","empty password not allow",false);
                    valid=false;
                }else {
                    if(newpassword.getText().toString().equals("")) {
                        newpassword.setError("this filed is required");
                        valid=false;
                    }
                    if(repeatpassword.getText().toString().equals("") ){
                        repeatpassword.setError("this filed is required");
                        valid=false;
                    }


                    if(!newpassword.getText().toString().trim().equals(repeatpassword.getText().toString().trim())){

                        Log.w("value", String.valueOf(newpassword.getText().toString().trim().length()));

                        Utility.showSingleButtonDialog(ChangePassword.this,"Error !"," These passwords don't match",false);
                        valid=false;
                    }else if(newpassword.getText().toString().trim().length()<4){


                        Utility.showSingleButtonDialog(ChangePassword.this,"Error !"," Password must be at least 6 characters",false);
                        valid=false;

                    }
                }

                if(valid)   savepassword();
            }
        });

        repeatpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_showpin, 0);
        newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_showpin, 0);


        repeatpassword.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {

                switch (target) {
                    case RIGHT:

                        if(repeatpassword.getText().toString().equals("")) return;

                       /* if(repeatpassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                            repeatpassword.setInputType( InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            repeatpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_showpin, 0);
                        }else {
                            repeatpassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                            repeatpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_hidepin, 0);
                        }
                        repeatpassword.setSelection(repeatpassword.getText().length());*/


                        if(repeatpassword.getTransformationMethod()==PasswordTransformationMethod.getInstance()){
                            repeatpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_showpin, 0);
                            repeatpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }else {
                            repeatpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_hidepin, 0);
                            repeatpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                        repeatpassword.setSelection(repeatpassword.getText().length());


                        break;
                    default:
                        break;
                }
            }
        });

        newpassword.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {

                switch (target) {
                    case RIGHT:

                      /*  if(newpassword.getText().toString().equals("")) return;

                        if(newpassword.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD){
                            newpassword.setInputType( InputType.TYPE_CLASS_TEXT |
                                    InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_showpin, 0);
                        }else {
                            newpassword.setInputType( InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD );
                            newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_hidepin, 0);
                        }*/

                        if(newpassword.getTransformationMethod()==PasswordTransformationMethod.getInstance()){
                            newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_showpin, 0);
                            newpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        }else {
                            newpassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ava_hidepin, 0);
                            newpassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }

                        newpassword.setSelection(newpassword.getText().length());
                        break;
                    default:
                        break;
                }
            }
        });


        repeatpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                if(newpassword.getText().toString().length()<=1){
                    if(editable.toString().length()>0){
                        repeatpassword.setText(null);
                    }
                }
            }
            //...
        });


    }


    public void savepassword(){


        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SignUpBO.setCustomerPassword();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(customerid));
        customerVO.setPassword(newpassword.getText().toString().trim());
        connectionVO.setMethodName(methodname);
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
                    Utility.alertDialog(ChangePassword.this,"Alert",sb.toString(),"Ok");
                }else {
                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(ChangePassword.this,Session.CACHE_CUSTOMER,json);
                    Intent intent =new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });

    }
}


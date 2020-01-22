package com.uav.autodebit.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.BO.UberBO;
import com.uav.autodebit.R;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Uber extends AppCompatActivity implements View.OnClickListener {
    EditText name,lastname,email;
    Button proceed;
    ImageView back_activity_button;

    LinearLayout main;
    EditText [] edittextArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber);
        getSupportActionBar().hide();
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        lastname=findViewById(R.id.lname);
        proceed=findViewById(R.id.proceed);
        back_activity_button=findViewById(R.id.back_activity_button);
        main=findViewById(R.id.main);

        proceed.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        edittextArray= new EditText[]{name, lastname, email};

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.proceed:
                if(!Utility.setErrorOnEdittext(edittextArray))return;

                uberSaveDetail();
                break;
            case R.id.back_activity_button :
                finish();
                break;
        }
    }

    private void uberSaveDetail() {
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = UberBO.saveUberCustomerDetails();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Uber.this)));
        customerVO.setName(name.getText().toString());
        customerVO.setEmailId(email.getText().toString());
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("setBankForService",params.toString());
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
                    Utility.showSingleButtonDialog(Uber.this,"Alert",sb.toString(),false);
                }else {
                    //set session customer or local cache
                    Toast.makeText(Uber.this, "sfsdfsdf", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

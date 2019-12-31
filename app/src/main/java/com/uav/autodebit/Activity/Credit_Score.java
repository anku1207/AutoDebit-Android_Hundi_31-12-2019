package com.uav.autodebit.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.PanCardBO;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;


import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Array;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Credit_Score extends AppCompatActivity {

    EditText pannumber,customername,date1,pin,city,state,permanentaddress,mobileno;
    Button update;
    ImageView back_activity_button1 ;

    ArrayList errolist;
    String errormsz;
    TextView textview;
    String [] mobilenumber;
    String actiontype;
    CustomerVO customercache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.activity_credit__score);

        getSupportActionBar().hide();

        pannumber=findViewById(R.id.pannumber);
        customername=findViewById(R.id.username);
        date1=findViewById(R.id.date);
        pin=findViewById(R.id.pin);
        city=findViewById(R.id.city);
        state=findViewById(R.id.state);
        permanentaddress=findViewById(R.id.permanentaddress);
        update=findViewById(R.id.update);
        back_activity_button1=findViewById(R.id.back_activity_button1);
        mobileno=findViewById(R.id.mobileno);
        textview=findViewById(R.id.textview);

        pannumber.setEnabled(false);
        customername.setEnabled(false);
        date1.setEnabled(false);
        pin.setEnabled(false);
        city.setEnabled(false);
        state.setEnabled(false);
        permanentaddress.setEnabled(false);
        mobileno.setEnabled(false);



        Gson gson = new Gson();
        customercache = gson.fromJson( Session.getSessionByKey(this, Session.CACHE_CUSTOMER), CustomerVO.class);
        pannumber.setText(customercache.getPanNo());
        customername.setText(customercache.getPanHolderName());
        Date date=new Date(customercache.getDateOfBirth());
        String datestring1 =Utility.convertDate2String(date,"dd/MM/yyyy");
        date1.setText(datestring1);
        pin.setText(customercache.getPincode());
        city.setText(customercache.getCity().getCityName());
        state.setText(customercache.getStateRegion().getStateRegionName());
        permanentaddress.setText(customercache.getAddress1());


       /* if(customerVO.getcIRMobileNumber()!=null){
            mobileno.setText(customerVO.getcIRMobileNumber());
        }else {
            mobileno.setText(customerVO.getMobileNumber());
        }*/

        Intent intent =getIntent();

        String type = intent.getAction();
        errormsz=intent.getStringExtra("msg");

        textview.setText(errormsz);

        if(type!=null && type.equals("Mobilenomatch")){
            mobileno.setEnabled(true);

            actiontype="Mobilenomatch";
            errolist = (ArrayList) intent.getStringArrayListExtra("value");
            String respmobileno[] = errolist.get(0).toString().split(":");
            String resptype = respmobileno[0];
            String arrnumber = respmobileno[1];
            mobilenumber = arrnumber.split("\\|");
        }else if(type!=null && type.equals("Customernotfound")){
            actiontype="Customernotfound";
            customername.setEnabled(true);
        }


        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                      if(actiontype.equals("Mobilenomatch") && mobileno.getText().toString().equals("")){
                          mobileno.setError("this filed is required");
                          return;
                      }
                      if(!mobileno.getText().toString().equals("") && Utility.validatePattern(mobileno.getText().toString().trim(), ApplicationConstant.MOBILENO_VALIDATION)!=null){
                          mobileno.setError(Utility.validatePattern(mobileno.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                          return;
                      }


                      String mobilelastdigits=mobileno.getText().toString().substring(mobileno.getText().toString().length()-5);
                      boolean numbermatch=false;
                      for(String array:mobilenumber){
                           String lastfiveDigits = !array.equals("")  ? array.substring(array.length()-5) : "";
                           if(mobilelastdigits.equals(lastfiveDigits)){
                               numbermatch=true;
                           }
                      }

                      if(!numbermatch){
                          Utility.showSingleButtonDialog(Credit_Score.this,"Error !", "Mobile Number is not matching with credit bureau records.",false);
                          return;
                      }


                       updatemobilenumber(mobileno.getText().toString().trim());
            }
        });

    }

    public void updatemobilenumber(String value){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = PanCardBO.updatedetails();

        CustomerVO customerdata =new CustomerVO();
        customerdata.setCirMobileNumber(value);
        customerdata.setCustomerId(customercache.getCustomerId());

        Gson gson = new Gson();
        String json = gson.toJson(customerdata);
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
                    Utility.showSingleButtonDialog(Credit_Score.this,"Error !",sb.toString(),false);

                }else {

                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(Credit_Score.this,Session.CACHE_CUSTOMER,json);

                    startActivity(new Intent(Credit_Score.this,Credit_Score_Report.class));
                    finish();
                }
            }
        });
    }

}

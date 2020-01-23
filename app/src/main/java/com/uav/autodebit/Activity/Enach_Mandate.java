package com.uav.autodebit.Activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonArrayRequest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceChargesVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Enach_Mandate extends AppCompatActivity{

    EditText acholdername,acno;
    Button mandatebtn;
    TextView textbox;
    UAVEditText maxamount;
    AutoCompleteTextView ifsc;
    String bankshortname;
    String errormsz;

    ImageView back_activity_button1;
    int minamt=0;

    Integer customerAuthId;

    boolean foractivity=false,disAmountEdittext=false;

    List<String> paths = new ArrayList<String>();

    HashMap<String,String> selectbank=new HashMap<>();

    ArrayList<Integer> selectServiceIds=new ArrayList<>();


    Spinner select_drop;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enach__mandate);
        getSupportActionBar().hide();

        foractivity=getIntent().getBooleanExtra("foractivity",true);
        selectServiceIds=getIntent().getIntegerArrayListExtra("selectservice");
        disAmountEdittext=getIntent().getBooleanExtra("disAmountEdittext",false);

        banklist();

        customerAuthId=null;
        bankshortname=null;

        textbox=findViewById(R.id.textbox);


        acholdername=findViewById(R.id.acholdername);
        acno=findViewById(R.id.acno);
        maxamount=findViewById(R.id.maxamount);
        mandatebtn=findViewById(R.id.mandatebtn);
        select_drop=findViewById(R.id.select_drop);

        if(disAmountEdittext)maxamount.setEnabled(false);

        back_activity_button1=findViewById(R.id.back_activity_button1);
        back_activity_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        /*maxamount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bankicon, 0, R.drawable.edit, 0);
        maxamount.setEnabled(false);*/

      //  ifsc = findViewById(R.id.ifsc);

        acholdername.setText(Session.getCustomerName(Enach_Mandate.this));


      /*  ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this,android.R.layout.select_dialog_item,paths);*/

      /*  ifsc.setThreshold(1);//will start working from first character
        ifsc.setAdapter(adapter);//setting the adapter data into the AutoCompleteTextView
        ifsc.setTextColor(Color.BLACK);

        ifsc.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                String selection = (String) parent.getItemAtPosition(position);
                bankshortname=selectbank.get(selection);
            }
        });
*/

        select_drop.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String selection = (String) adapterView.getItemAtPosition(i);
                bankshortname=selectbank.get(selection);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });






        maxamount.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        maxamount.setEnabled(true);
                       break;
                    default:
                       break;
                }
            }
        });




        mandatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean validation=true;

                if(acno.getText().toString().equals("")){
                    acno.setError("this filed is required");
                    validation=false;
                }

                if(acholdername.getText().toString().equals("")){
                    acholdername.setError("this filed is required");
                    validation=false;
                }

                if(maxamount.getText().toString().equals("")){
                    maxamount.setError("this filed is required");
                    validation=false;
                }
/*
                if(ifsc.getText().toString().equals("")){
                    ifsc.setError("this filed is required");
                    validation=false;
                }*/

                if( bankshortname==null){
                    Utility.alertDialog(Enach_Mandate.this,"Alert","Bank is not selected","Ok");
                    validation=false;
                }
                if(!maxamount.getText().toString().equals("")  && Integer.parseInt(maxamount.getText().toString())< minamt){
                    Utility.showSingleButtonDialog(Enach_Mandate.this,"Alert",errormsz,false);
                    validation=false;
                }

                if(acno.getText().toString().length()<5){
                    acno.setError("Minimum length is 5");
                    validation=false;
                }
                if(!validation) return;

                try {
                    verifydialog();
                }catch (Exception e){
                    Log.w("error_enach",e.getMessage());
                }
              //  mandatebank();
            }
        });

    }

    public void verifydialog() throws Exception{

        try{
            JSONArray jsonArray=new JSONArray();
            JSONObject object =new JSONObject();
            object.put("key","Account No");
            object.put("value",acno.getText().toString());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Holder Name");
            object.put("value",acholdername.getText().toString());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Max Amount");
            object.put("value",maxamount.getText().toString());
            jsonArray.put(object);

            Utility.confirmationDialog(new DialogInterface() {
                @Override
                public void confirm(Dialog dialog) {
                    dialog.dismiss();

                    mandatebank();
                }

                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                }
            },Enach_Mandate.this,jsonArray,null,"Please Confirm Detail");



        }catch (Exception e){
            Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    public void mandatebank(){


        Gson gson=new Gson();
        LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(Enach_Mandate.this, Session.LOCAL_CACHE), LocalCacheVO.class);

        String customerId=Session.getCustomerId(Enach_Mandate.this);

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.enachMandate();

        params.put("ifsc",bankshortname);
        params.put("accountNumber",acno.getText().toString().trim());
        params.put("maxAmount",maxamount.getText().toString().trim());
        params.put("accountHolderName",acholdername.getText().toString().trim());
        params.put("returnURL",localCacheVO.geteNachReturnURL());
        params.put("customerid",customerId);
        connectionVO.setParams(params);

        Log.w("sendrequest",params.toString());




        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                Log.w("responsesignup",response.toString());


                if((response.has("status") && response.get("status").equals("fail")) || (response.has("statusCode") && response.get("statusCode").equals("400"))){
                   // Utility.alertDialog(Enach_Mandate.this,"Alert",response.getString("errorMsg"),"Ok");
                    JSONArray error =response.has("errorMsg")?new JSONArray().put(response.getString("errorMsg")):response.getJSONArray("errorMsgs");
                    Utility.showSingleButtonDialog(Enach_Mandate.this,"Error !",error.get(0).toString(),false);
                }else {

                    JSONObject object = new JSONObject(response.getString("result"));
                    JSONObject object1=object.getJSONObject("redirect");
                    String url=object1.getString("url");

                    customerAuthId=response.getInt("customerAuthId");

                    Log.w("urlcreate",url);


                    Intent intent = new Intent(Enach_Mandate.this, Webview.class);
                    intent.putExtra("url", url);
                    startActivityForResult(intent,100);

                    /*Intent i = new Intent(Enach_Mandate.this, LotusPay.class);
                    i.putExtra("url", url);
                    startActivityForResult(i, 101);*/
                }
            }
        });
    }


    public void banklist(){


        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.enachBankList();

        ServiceChargesVO serviceChargesVO=new ServiceChargesVO();
        serviceChargesVO.setAnonymousInteger(Integer.valueOf(Session.getCustomerId(this)));
        serviceChargesVO.setAnonymousString(selectServiceIds!=null?selectServiceIds.toString():null);


        Gson gson =new Gson();
        String json = gson.toJson(serviceChargesVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("addBankForService",params.toString());




        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                Log.w("responsesignup",response.toString());
                if(response.get("status").equals("fail")){
                    Utility.showSingleButtonDialog(Enach_Mandate.this,"Error !",response.getString("errorMsg"),true);

                }else {

                    if(response.has("customerJson")){
                        CustomerVO customerVO = gson.fromJson(response.getString("customerJson"), CustomerVO.class);
                        String json = gson.toJson(customerVO);
                        Session.set_Data_Sharedprefence(Enach_Mandate.this,Session.CACHE_CUSTOMER,json);
                    }

                    JSONObject object = new JSONObject(response.getString("result"));
                    JSONArray jsonArray=object.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object1=(jsonArray.getJSONObject(i));
                        selectbank.put(object1.getString("name"),object1.getString("id"));
                        paths.add(object1.getString("name"));
                    }


                    ArrayAdapter<String>adapter = new ArrayAdapter<String>(Enach_Mandate.this,
                            android.R.layout.simple_spinner_item,paths);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    select_drop.setAdapter(adapter);

                    maxamount.setText((int)(Double.parseDouble(object.getString("minMandateAmt")))+"");
                    minamt=(int)(Double.parseDouble(object.getString("minMandateAmt")));
                    errormsz=object.getString("minMandateAmtFailedMsg");

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if(requestCode==100){

                if(data!=null && data.getStringExtra("url")!=null){

                    try {
                        JSONArray jsonArray =new JSONArray(data.getStringExtra("url"));

                        String name=(jsonArray.getJSONObject(2)).getString("mandate_id");
                        Log.w("mandateid",jsonArray.toString());
                        setEnachMandateId((jsonArray.getJSONObject(2)).getString("mandate_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    }

    private void setEnachMandateId(String id){


        String customerId=Session.getCustomerId(Enach_Mandate.this);

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.setEnachMandateId();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(customerId));

        CustomerAuthServiceVO  customerAuthServiceVO=new CustomerAuthServiceVO();
        customerAuthServiceVO.setProviderTokenId(id);
        customerAuthServiceVO.setCustomer(customerVO);
        customerAuthServiceVO.setCustomerAuthId(customerAuthId);
        customerAuthServiceVO.setAnonymousString(selectServiceIds!=null?selectServiceIds.toString():null);
        Gson gson = new Gson();
        String json = gson.toJson(customerAuthServiceVO);

        Log.w("resp",json);
        params.put("volley", json);


        connectionVO.setParams(params);



        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
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
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    //Utility.alertDialog(Enach_Mandate.this,"Alert",sb.toString(),"Ok");
                    Utility.showSingleButtonDialog(Enach_Mandate.this,"Error !",sb.toString(),false);
                }else {
                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(Enach_Mandate.this,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(Enach_Mandate.this, Session.LOCAL_CACHE,customerVO.getLocalCache());

                    // startActivity(new Intent(Enach_Mandate.this,Paynimo_HDFC.class));
                    if (!foractivity) {
                        startActivity(new Intent(Enach_Mandate.this,SI_First_Data.class));
                    }else {
                        Intent intent =new Intent();
                        intent.putExtra("selectservice",selectServiceIds);
                        intent.putExtra("msg",customerVO.getAnonymousString());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    finish();




                }
            }
        });
    }
}

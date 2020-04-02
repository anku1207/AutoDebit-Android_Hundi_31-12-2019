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
import com.uav.autodebit.Interface.VolleyResponse;
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

public class Enach_Mandate extends Base_Activity implements View.OnClickListener {

    EditText acholdername,acno;
    Button mandatebtn;
    TextView textbox;
    UAVEditText maxamount;
    AutoCompleteTextView ifsc;
    String bankshortname,accountTypeValue;
    String errormsz;

    ImageView back_activity_button1;
    int minamt=0;

    Integer customerAuthId;

    boolean foractivity=false,disAmountEdittext=false;



    HashMap<String,String> selectbank=new HashMap<>();

    ArrayList<Integer> selectServiceIds=new ArrayList<>();


    Spinner select_drop,account_type;
    JSONArray accountTypeJsonArray;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enach__mandate);
        getSupportActionBar().hide();


        setElementId();
        foractivity=getIntent().getBooleanExtra("forresutl",true);//success finish activity
        selectServiceIds=getIntent().getIntegerArrayListExtra("selectservice"); // get select service list for get mandate amount and set bank id again serviceid
        disAmountEdittext=getIntent().getBooleanExtra("disAmountEdittext",false); //disable mandate amount edittext filed
        if(disAmountEdittext)maxamount.setEnabled(false);



        accountTypeJsonArray=new JSONArray();
        customerAuthId=null;
        bankshortname=null;
        accountTypeValue=null;

        back_activity_button1.setOnClickListener(this);
        mandatebtn.setOnClickListener(this);


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


        banklist(new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
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

            account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try{
                        JSONObject jsonObject =accountTypeJsonArray.getJSONObject(i);
                        accountTypeValue=jsonObject.getString("value");
                    }catch (Exception e){
                        Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            //add autofill Enach bank details
            if(getIntent().getBooleanExtra("addMoreServiceMandate",false)){
                try {
                    acholdername.setText(getIntent().getStringExtra("accountHolderName"));
                    acno.setText(getIntent().getStringExtra("accountNumber"));
                    JSONObject selectBankJson =new JSONObject(getIntent().getStringExtra("bankJson"));

                    // set bank list value by text
                    ArrayAdapter myAdap = (ArrayAdapter) select_drop.getAdapter(); //cast to an ArrayAdapter
                    int spinnerPosition = myAdap.getPosition(selectBankJson.getString("name"));
                    select_drop.setSelection(spinnerPosition);

                    JSONObject accountTypeJson =new JSONObject(getIntent().getStringExtra("accountType"));
                    myAdap = (ArrayAdapter) account_type.getAdapter(); //cast to an ArrayAdapter
                    spinnerPosition = myAdap.getPosition(accountTypeJson.getString("key"));
                    account_type.setSelection(spinnerPosition);

                    acholdername.setEnabled(false);
                    acno.setEnabled(false);
                    select_drop.setEnabled(false);
                    account_type.setEnabled(false);

                }catch (Exception e){
                    Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                }
            }
        }));
        //show edittable icon on right side
        /*maxamount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.bankicon, 0, R.drawable.edit, 0);
        maxamount.setEnabled(false);*/

      //  ifsc = findViewById(R.id.ifsc);

        if(acholdername.getText().toString().equals("")) acholdername.setText(Session.getCustomerName(Enach_Mandate.this));
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
    }

    private  void setElementId(){
        textbox=findViewById(R.id.textbox);
        acholdername=findViewById(R.id.acholdername);
        acno=findViewById(R.id.acno);
        maxamount=findViewById(R.id.maxamount);
        mandatebtn=findViewById(R.id.mandatebtn);
        select_drop=findViewById(R.id.select_drop);
        account_type=findViewById(R.id.account_type);
        back_activity_button1=findViewById(R.id.back_activity_button1);

    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.back_activity_button1){
            finish();
        }else if(view.getId()==R.id.mandatebtn){
            boolean validation=true;

            if(acno.getText().toString().trim().equals("")){
                acno.setError("this filed is required");
                validation=false;
            }

            if(acholdername.getText().toString().trim().equals("")){
                acholdername.setError("this filed is required");
                validation=false;
            }

            if(maxamount.getText().toString().trim().equals("")){
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
            if( accountTypeValue==null){
                Utility.alertDialog(Enach_Mandate.this,"Alert","Account Type is not selected","Ok");
                validation=false;
            }
            if(!maxamount.getText().toString().trim().equals("")  && Integer.parseInt(maxamount.getText().toString().trim())< minamt){
                Utility.showSingleButtonDialog(Enach_Mandate.this,"Alert",errormsz,false);
                validation=false;
            }

            if(acno.getText().toString().trim().length()<5){
                acno.setError("Minimum length is 5");
                validation=false;
            }
            if(!validation) return;

            try {
                verifydialog();
            }catch (Exception e){
                Log.w("error_enach",e.getMessage());
            }

        }

    }



    public void verifydialog(){

        try{
            JSONArray jsonArray=new JSONArray();
            JSONObject object =new JSONObject();
            object.put("key","Account No");
            object.put("value",acno.getText().toString().trim());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Holder Name");
            object.put("value",acholdername.getText().toString().trim());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Max Amount");
            object.put("value",maxamount.getText().toString().trim());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Account Type");
            object.put("value",accountTypeValue);
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
            e.printStackTrace();
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
        params.put("accountType",accountTypeValue);
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


    public void banklist(VolleyResponse volleyResponse){

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
                if( (response.has("status") && response.get("status").equals("fail")) || (response.has("statusCode") && response.get("statusCode").equals("400"))){
                    Utility.showSingleButtonDialog(Enach_Mandate.this,"Error !",response.has("errorMsg") ?response.getString("errorMsg"):response.getJSONArray("errorMsgs").get(0).toString(),true);

                }else {

                    if(response.has("customerJson")){
                        CustomerVO customerVO = gson.fromJson(response.getString("customerJson"), CustomerVO.class);
                        String json = gson.toJson(customerVO);
                        Session.set_Data_Sharedprefence(Enach_Mandate.this,Session.CACHE_CUSTOMER,json);
                    }

                    JSONObject object = new JSONObject(response.getString("result"));
                    JSONArray jsonArray=object.getJSONArray("data");

                    List<String> paths = new ArrayList<String>();
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject object1=(jsonArray.getJSONObject(i));
                        selectbank.put(object1.getString("name"),object1.getString("id"));
                        paths.add(object1.getString("name"));
                    }

                    ArrayAdapter<String>adapter = new ArrayAdapter<String>(Enach_Mandate.this,
                            R.layout.spinner_item,paths);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    select_drop.setAdapter(adapter);

                    accountTypeJsonArray =new JSONArray(response.getString("accountType"));
                    ArrayList accountList=new ArrayList();
                    for(int i=0;i< accountTypeJsonArray.length();i++){
                        JSONObject object1=(accountTypeJsonArray.getJSONObject(i));
                        accountList.add(object1.getString("key"));
                    }

                    adapter = new ArrayAdapter<String>(Enach_Mandate.this,
                            R.layout.spinner_item,accountList);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    account_type.setAdapter(adapter);

                    maxamount.setText((int)(Double.parseDouble(object.getString("minMandateAmt")))+"");
                    minamt=(int)(Double.parseDouble(object.getString("minMandateAmt")));
                    errormsz=object.getString("minMandateAmtFailedMsg");

                    volleyResponse.onSuccess(response);

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
                        String name=(jsonArray.getJSONObject(2)).getString("source_id");
                        Log.w("mandateid",jsonArray.toString());
                        setEnachMandateId((jsonArray.getJSONObject(2)).getString("source_id"));
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
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
                        intent.putExtra("mandate_status",customerVO.isEventIs());
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                    finish();
                }
            }
        });
    }
}

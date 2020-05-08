package com.uav.autodebit.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceChargesVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ManualEnachMandate extends Base_Activity implements View.OnClickListener  {

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

    ArrayList<Integer> selectServiceIds=new ArrayList<>();


    Spinner account_type;
    JSONArray accountTypeJsonArray;

    EditText bankname;

    ArrayList<DataAdapterVO> datalist;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manual_enach_mandate);

        getSupportActionBar().hide();

        setElementId();
        selectServiceIds=getIntent().getIntegerArrayListExtra("selectservice"); // get select service list for get mandate amount and set bank id again serviceid



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

            bankname.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(MotionEvent.ACTION_UP == motionEvent.getAction()) {

                        Layout layout = ((EditText) view).getLayout();
                        int  x = (int) motionEvent.getX();
                        int  y = (int) motionEvent.getY();

                        Intent intent =new Intent(ManualEnachMandate.this, PopapSimpleList.class);
                        intent.putExtra("datalist", new Gson().toJson( datalist));
                        intent.putExtra("title","Operator");
                        intent.putExtra("x",x);
                        intent.putExtra("y",y);
                        startActivityForResult(intent, ApplicationConstant.REQ_POPAPACTIVITYRESULT);

                    }
                    return false;
                }
            });


            account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try{
                        JSONObject jsonObject =accountTypeJsonArray.getJSONObject(i);
                        accountTypeValue=jsonObject.getString("value");
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(ManualEnachMandate.this , Utility.getStackTrace(e));
                        //Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
       }));
        if(acholdername.getText().toString().equals("")) acholdername.setText(Session.getCustomerName(this));
    }

    private  void setElementId(){
        textbox=findViewById(R.id.textbox);
        acholdername=findViewById(R.id.acholdername);
        acno=findViewById(R.id.acno);
        maxamount=findViewById(R.id.maxamount);
        mandatebtn=findViewById(R.id.mandatebtn);
        account_type=findViewById(R.id.account_type);
        back_activity_button1=findViewById(R.id.back_activity_button1);
        bankname=findViewById(R.id.bankname);
        bankname.setClickable(false);

        datalist = new ArrayList<>();

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
            if(bankname.getText().toString().equals("")){
                bankname.setError("this filed is required");
                validation=false;
            }

            if( bankshortname==null && !bankname.getText().toString().equals("")){
                Utility.alertDialog(ManualEnachMandate.this,"Alert","Bank is not selected","Ok");
                validation=false;
            }
            if( accountTypeValue==null){
                Utility.alertDialog(ManualEnachMandate.this,"Alert","Account Type is not selected","Ok");
                validation=false;
            }
            if(!maxamount.getText().toString().trim().equals("")  && Integer.parseInt(maxamount.getText().toString().trim())< minamt){
                Utility.showSingleButtonDialog(ManualEnachMandate.this,"Alert",errormsz,false);
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
                ExceptionsNotification.ExceptionHandling(ManualEnachMandate.this , Utility.getStackTrace(e));
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
            },ManualEnachMandate.this,jsonArray,null,"Please Confirm Detail");



        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(ManualEnachMandate.this , Utility.getStackTrace(e));
            // Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    public void mandatebank(){


        Gson gson=new Gson();
        LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(ManualEnachMandate.this, Session.LOCAL_CACHE), LocalCacheVO.class);

        String customerId=Session.getCustomerId(ManualEnachMandate.this);

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
                    Utility.showSingleButtonDialog(ManualEnachMandate.this,"Error !",error.get(0).toString(),false);
                }else {

                    JSONObject object = new JSONObject(response.getString("result"));
                    JSONObject object1=object.getJSONObject("redirect");
                    String url=object1.getString("url");

                    customerAuthId=response.getInt("customerAuthId");

                    Log.w("urlcreate",url);


                    Intent intent = new Intent(ManualEnachMandate.this, Webview.class);
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
                    Utility.showSingleButtonDialog(ManualEnachMandate.this,"Error !",response.has("errorMsg") ?response.getString("errorMsg"):response.getJSONArray("errorMsgs").get(0).toString(),true);

                }else {

                    JSONObject object = new JSONObject(response.getString("result"));
                    JSONArray jsonArray=object.getJSONArray("data");

                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject bankjson=(jsonArray.getJSONObject(i));
                        DataAdapterVO dataAdapterVO = new DataAdapterVO();
                        dataAdapterVO.setText(bankjson.getString("name"));
                        dataAdapterVO.setAssociatedValue(bankjson.getString("id"));
                        datalist.add(dataAdapterVO);
                    }



                    accountTypeJsonArray =new JSONArray(response.getString("accountType"));
                    ArrayList accountList=new ArrayList();
                    for(int i=0;i< accountTypeJsonArray.length();i++){
                        JSONObject object1=(accountTypeJsonArray.getJSONObject(i));
                        accountList.add(object1.getString("key"));
                    }

                    ArrayAdapter adapter = new ArrayAdapter<String>(ManualEnachMandate.this,
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
                        ExceptionsNotification.ExceptionHandling(ManualEnachMandate.this , Utility.getStackTrace(e));
                        // Utility.exceptionAlertDialog(Enach_Mandate.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                    }
                }
            }else if(requestCode==ApplicationConstant.REQ_POPAPACTIVITYRESULT){
                if(data!=null){
                    bankname.setError(null);
                    bankname.setText(data.getStringExtra("operatorname"));
                    bankshortname=data.getStringExtra("operator");
                }else {
                    Toast.makeText(this, "Bank Name Is Null Selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setEnachMandateId(String id){

        String customerId=Session.getCustomerId(ManualEnachMandate.this);
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.setEnachMandateId();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(customerId));

        CustomerAuthServiceVO customerAuthServiceVO=new CustomerAuthServiceVO();
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
                    Utility.showSingleButtonDialog(ManualEnachMandate.this,"Error !",sb.toString(),false);
                }else {
                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(ManualEnachMandate.this,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(ManualEnachMandate.this, Session.LOCAL_CACHE,customerVO.getLocalCache());


                    Intent intent =new Intent();
                    intent.putExtra("selectservice",selectServiceIds);
                    intent.putExtra("msg",customerVO.getAnonymousString());
                    intent.putExtra("mandate_status",customerVO.isEventIs());
                    if(customerVO.isEventIs()){
                        intent.putExtra("bankMandateId",customerVO.getAnonymousInteger().toString());
                    }

                    setResult(RESULT_OK,intent);
                    finish();


                }
            }
        });
    }
}

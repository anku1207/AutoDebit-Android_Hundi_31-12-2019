package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.CustomTextWatcherLengthAction;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenPlanVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Mobile_Prepaid_Recharge_Service extends AppCompatActivity implements View.OnClickListener{
    UAVEditText mobilenumber;
    EditText operator,amount;
    ImageView back_activity_button;
    String operatorcode,regioncode,operatorname,regionname=null;
    TextView browseplan;

    Button proceed;

    String serviceid;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_mobile__prepaid__recharge__service);

        mobilenumber=findViewById(R.id.mobilenumber);
        operator=findViewById(R.id.operator);
        amount=findViewById(R.id.amount);
        proceed=findViewById(R.id.proceed);
        browseplan=findViewById(R.id.browseplan);

        browseplan.setVisibility(View.GONE);
        back_activity_button=findViewById(R.id.back_activity_button1);
        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        proceed.setOnClickListener(this);
        browseplan.setOnClickListener(this);
        operator.setClickable(false);

        Intent intent =getIntent();
        serviceid=intent.getStringExtra("serviceid");



        Drawable drawable = getResources().getDrawable(R.drawable.contacts);
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.appbar));

        mobilenumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mobile,0 , R.drawable.contacts, 0);
        mobilenumber.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        if(mobilenumber.getError() == null){

                            if(PermissionHandler.contactpermission(Mobile_Prepaid_Recharge_Service.this)){
                                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                startActivityForResult(intent, 101);
                            }
                        }
                        break;
                    default:
                        break;
                }
            }
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!operator.getText().toString().equals("")){
                    if(operator.getError()==null){
                        browseplan.setVisibility(View.VISIBLE);
                    }else {
                        browseplan.setVisibility(View.GONE);
                    }
                }

            }
        });



        mobilenumber.addTextChangedListener(new  CustomTextWatcherLengthAction(mobilenumber,10,operator,"touch"));

        operator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    //startActivity(new Intent(Mobile_Prepaid_Recharge_Service.this,Listview_With_Image.class));

                    Intent intent =new Intent(Mobile_Prepaid_Recharge_Service.this, Listview_With_Image.class);

                    Gson gson = new Gson();

                    String data = gson.toJson(getDataList());
                    intent.putExtra("datalist", data);
                    intent.putExtra("title","Mobile Operator");
                    startActivityForResult(intent,100);
                }
                return false;
            }
        });

    }

    public ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator= Session.getSessionByKey(Mobile_Prepaid_Recharge_Service.this,Session.MOBILE_OPERATOR_LIST);
        try {
            JSONObject objectoperator =new JSONObject(operator);
            JSONArray jsonArray=objectoperator.getJSONArray("dataList");
            for(int i=0;i<jsonArray.length();i++){
                DataAdapterVO dataAdapterVO = new DataAdapterVO();
                JSONObject object =jsonArray.getJSONObject(i);
                dataAdapterVO.setText(object.getString("OperatorName"));
                dataAdapterVO.setImagename(object.getString("OperatorAlias").toLowerCase());
                dataAdapterVO.setAssociatedValue(object.getString("OperatorAlias"));
                datalist.add(dataAdapterVO);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  datalist;
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode) {
                case 100:
                    operatorname =data.getStringExtra("operatorname");
                    operatorcode=data.getStringExtra("operator");

                    ConnectionVO connectionVO = new ConnectionVO();
                    connectionVO.setTitle("Select State");
                    connectionVO.setSharedPreferenceKey(Session.MOBILE_STATE_LIST);
                    connectionVO.setEntityIdKey("RegionAlias");
                    connectionVO.setEntityTextKey("RegionName");
                    Intent intent = new Intent(getApplicationContext(),ListViewSingleText.class);
                    intent.putExtra(ApplicationConstant.INTENT_EXTRA_CONNECTION,  connectionVO);
                    startActivityForResult(intent,1000);

                    break;
                case 1000 :
                        amount.requestFocus();
                        browseplan.setVisibility(View.VISIBLE);
                        regionname=data.getStringExtra("valueName");
                        regioncode=data.getStringExtra("valueId");
                        operator.setText(operatorcode+"-"+data.getStringExtra("valueName"));
                        operator.setError(null);
                        amount.setError(null);
                        if (!mobilenumber.getText().toString().equals("") &&  Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)!=null){
                            mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                        }
                    break;
                case 101:
                        Uri contactData = data.getData();
                        Cursor c = getContentResolver().query(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                            String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            String num = "";
                            if (Integer.valueOf(hasNumber) == 1) {
                                Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                while (numbers.moveToNext()) {
                                    num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+","");
                                }
                                if (num.length()>10) {

                                    num=num.substring(num.length() - 10);
                                }
                                mobilenumber.setText(num);
                            }
                        }
                        break;
                case 102:
                    amount.setText(data.getStringExtra("amount"));
                    amount.setSelection(amount.getText().length());
                    break;

            }
        }
    }
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.proceed :
                rechargeProceed();
                break;
            case R.id.browseplan :

                HashMap<String, Object> params = new HashMap<String, Object>();

                ConnectionVO connectionVO =OxigenPlanBO.getPlan();


                OxigenPlanVO oxigenPlanVO =new OxigenPlanVO();
                ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                serviceTypeVO.setServiceTypeId(Integer.parseInt(serviceid));
                oxigenPlanVO.setServiceType(serviceTypeVO);
                oxigenPlanVO.setOperatorAlias(operatorcode);
                oxigenPlanVO.setStateAlias(regioncode);
                Gson gson=new Gson();
                String json = gson.toJson(oxigenPlanVO);
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
                        OxigenPlanVO oxigenPlanVO = gson.fromJson(response.toString(), OxigenPlanVO.class);


                        if(oxigenPlanVO.getStatusCode().equals("400")){

                            StringBuffer stringBuffer= new StringBuffer();

                            for(int i=0;i<oxigenPlanVO.getErrorMsgs().size();i++){
                                stringBuffer.append(oxigenPlanVO.getErrorMsgs().get(i));
                            }
                            Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this,"Error !",stringBuffer.toString(),false);
                        }else {
                            String json = gson.toJson(oxigenPlanVO);
                            Session.set_Data_Sharedprefence(Mobile_Prepaid_Recharge_Service.this,Session.CACHE_BROWSE_DATA,json);

                            startActivityForResult(new Intent(Mobile_Prepaid_Recharge_Service.this,Browse_Plan.class),102);

                        }
                    }
                });

                break;
        }

    }


    public void rechargeProceed(){
        try {
            boolean valid=true;

            if(mobilenumber.getText().toString().equals("")){
                mobilenumber.setError("this filed is required");
                valid=false;
            }else if (!mobilenumber.getText().toString().equals("") &&  Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)!=null){
                mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                valid=false;
            }

            if(operator.getText().toString().equals("")){
                operator.setError("this filed is required");
                valid=false;
            }
            if(amount.getText().toString().equals("")){
                amount.setError("this filed is required");
                browseplan.setVisibility(View.GONE);
                valid=false;
            }

            if(valid){
                String btn[]={"Cancel","Ok"};

                JSONArray jsonArray =new JSONArray();

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("key","Operator");
                jsonObject.put("value",operator.getText().toString());
                jsonArray.put(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("key","Number");
                jsonObject.put("value",mobilenumber.getText().toString());
                jsonArray.put(jsonObject);

                jsonObject = new JSONObject();
                jsonObject.put("key","Amount");
                jsonObject.put("value",amount.getText().toString());
                jsonArray.put(jsonObject);

                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                        try {
                            proceedToRecharge();
                        }catch (Exception e){
                            e.printStackTrace();
                            Utility.exceptionAlertDialog(Mobile_Prepaid_Recharge_Service.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                        }


                    }

                    @Override
                    public void modify(Dialog dialog) {
                        dialog.dismiss();

                    }
                },Mobile_Prepaid_Recharge_Service.this,jsonArray,null,"Confirmation",btn);


            }
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Mobile_Prepaid_Recharge_Service.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }

    }

    public void proceedToRecharge() throws Exception{
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =OxigenPlanBO.oxiMobileRecharge();

        OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
        oxigenTransactionVO.setReferenceName("mobile");
        oxigenTransactionVO.setReferenceValue(mobilenumber.getText().toString().trim());
        oxigenTransactionVO.setAmount(Double.parseDouble(amount.getText().toString().trim()));
        oxigenTransactionVO.setStateRegion(regionname);
        oxigenTransactionVO.setOperateName(operatorname);




        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Mobile_Prepaid_Recharge_Service.this)));
        oxigenTransactionVO.setCustomer(customerVO);

        ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
        serviceTypeVO.setServiceTypeId(Integer.parseInt(serviceid));
        oxigenTransactionVO.setServiceType(serviceTypeVO);


        Gson gson=new Gson();
        String json = gson.toJson(oxigenTransactionVO);

        Log.w("request",json);
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
                    OxigenTransactionVO oxigenPlanresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);


                    if(oxigenPlanresp.getStatusCode().equals("400")){
                        StringBuffer stringBuffer= new StringBuffer();
                        for(int i=0;i<oxigenPlanresp.getErrorMsgs().size();i++){
                            stringBuffer.append(oxigenPlanresp.getErrorMsgs().get(i));
                        }
                        Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this,"Error !",stringBuffer.toString(),false);
                    }else {
                        Utility.showSingleButtonDialog(Mobile_Prepaid_Recharge_Service.this,"Success !",oxigenPlanresp.getAnonymousString(),true);;
                    }


            }
        });
    }




}

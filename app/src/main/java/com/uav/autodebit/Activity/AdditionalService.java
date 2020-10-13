package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewItemCheckboxBaseAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AdditionalService extends Base_Activity implements View.OnClickListener {
    ExpandableHeightListView listview;
    Button btnadd,btnskip;
    ListViewItemCheckboxBaseAdapter myAdapter;
    List<ServiceTypeVO> utilityServices;
    List<ServiceTypeVO> servicelist;

    ServiceTypeVO selectServiceTypeVo=null;
    ImageView back_activity_button ;
    BottomNavigationView navigation;

    List<ServiceTypeVO> serviceTypeVOS;
    LinearLayout buttonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_service);
        getSupportActionBar().hide();


        listview=findViewById(R.id.listview);
        btnadd=findViewById(R.id.btnadd);
        btnskip=findViewById(R.id.btnskip);
        back_activity_button=findViewById(R.id.back_activity_button);
        buttonLayout=findViewById(R.id.buttonLayout);





        navigation = findViewById(R.id.navigation);
        navigation.getMenu().setGroupCheckable(0, false, true);
        BottomNavigationView  navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnskip.setOnClickListener(this);
        btnadd.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        getServiceList(new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            buttonLayout.setVisibility(View.VISIBLE);
        }));
   }



    private void getServiceList(VolleyResponse volleyResponse){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.getBankListAndAccountType();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("banklist",params.toString());
        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson=new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(AdditionalService.this,"Alert",sb.toString(),true);
                }else {

                    JSONObject jsonObject =new JSONObject(customerVO.getAnonymousString());

                    ArrayList<ServiceTypeVO> serviceTypeVOS= (ArrayList<ServiceTypeVO>) new Gson().fromJson(jsonObject.getString("services"), new TypeToken<ArrayList<ServiceTypeVO>>() { }.getType());
                    myAdapter=new ListViewItemCheckboxBaseAdapter(AdditionalService.this, serviceTypeVOS, R.layout.checkbox_with_text);
                    listview.setAdapter(myAdapter);
                    listview.setExpanded(true);

                    volleyResponse.onSuccess(customerVO);
                }
            }
        });
    }

    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent;
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    intent = new Intent(getApplicationContext(), Home.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case R.id.bottom_profile:
                    intent = new Intent(getApplicationContext(), Profile_Activity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case R.id.bottom_history:
                    startActivity(new Intent(AdditionalService.this,History.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
                case R.id.bottom_help:
                    startActivity(new Intent(AdditionalService.this,Help.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backbuttonfun();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void saveServiceAdd(ArrayList arrayList){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.saveAdditionalService();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(AdditionalService.this)));
            customerVO.setAnonymousString(arrayList.toString());
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);

            Log.w("request",json);
            params.put("volley", json);
            connectionVO.setParams(params);
            VolleyUtils.makeJsonObjectRequest(AdditionalService.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    CustomerVO customerVO1 = gson.fromJson(response.toString(), CustomerVO.class);
                    if (customerVO1.getStatusCode().equals("400")) {
                        //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                        ArrayList error = (ArrayList) customerVO1.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < error.size(); i++) {
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(AdditionalService.this, "Error !", sb.toString(), false);
                    } else if (customerVO1.getStatusCode().equals("ap102")) {
                        // 12/04/2020
                        MyDialog.showWebviewAlertDialog(AdditionalService.this, customerVO1.getHtmlString(),false,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(dialog)->{
                            dialog.dismiss();
                            startActivityForResult(new Intent(AdditionalService.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice",arrayList), ApplicationConstant.REQ_ENACH_MANDATE);
                        },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                            cancel.dismiss();
                        }));



                    } else if (customerVO1.getStatusCode().equals("ap103")) {

                        MyDialog.showWebviewAlertDialog(AdditionalService.this, customerVO1.getHtmlString(),false,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(dialog)->{
                            dialog.dismiss();

                            String[] buttons = {"New Bank","Existing Bank"};
                            Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                                @Override
                                public void confirm(Dialog dialog) {
                                    dialog.dismiss();
                                    try {
                                        JSONArray arryjson = new JSONArray(customerVO1.getAnonymousString());
                                        ArrayList<CustomerAuthServiceVO> customerAuthServiceArry = new ArrayList<>();
                                        for (int i = 0; i < arryjson.length(); i++) {
                                            JSONObject jsonObject = arryjson.getJSONObject(i);
                                            CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                                            customerAuthServiceVO.setBankName(jsonObject.getString("bankName"));
                                            customerAuthServiceVO.setProviderTokenId(jsonObject.getString("mandateId"));
                                            customerAuthServiceVO.setCustomerAuthId(jsonObject.getInt("id"));
                                            customerAuthServiceVO.setAnonymousString(jsonObject.getString("status"));
                                            customerAuthServiceArry.add(customerAuthServiceVO);
                                        }

                                        CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                                        customerAuthServiceVO.setBankName(null);
                                        customerAuthServiceVO.setProviderTokenId("Add New Mandate");
                                        customerAuthServiceVO.setCustomerAuthId(0);
                                        customerAuthServiceVO.setAnonymousString(null);
                                        customerAuthServiceArry.add(customerAuthServiceVO);

                                        Utility.alertselectdialog(AdditionalService.this, AuthServiceProviderVO.ENACHIDFC, customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (s) -> {
                                            if (!s.equals("0")) {
                                                Log.w("Home_value", s);
                                                setBankForServiceList(Integer.parseInt(Session.getCustomerId(AdditionalService.this)), arrayList,Integer.parseInt(s));
                                            } else {
                                                startActivityForResult(new Intent(AdditionalService.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice",arrayList), ApplicationConstant.REQ_ENACH_MANDATE);
                                            }
                                        }));
                                    } catch (Exception e) {
                                        ExceptionsNotification.ExceptionHandling(AdditionalService.this , Utility.getStackTrace(e));
                                    }
                                }
                                @Override
                                public void modify(Dialog dialog) {
                                    dialog.dismiss();
                                    startActivityForResult(new Intent(AdditionalService.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice",arrayList), ApplicationConstant.REQ_ENACH_MANDATE);
                                }
                            }, AdditionalService.this, customerVO1.getErrorMsgs().get(0), "", buttons);
                        },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                            cancel.dismiss();
                        }));
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(AdditionalService.this , Utility.getStackTrace(e));

        }
    }

    private  void setBankForServiceList(int customerId,List serviceTypeVOS,int bankId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.setBankForServiceList();

        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(customerId);
        customerVO.setAnonymousString(Utility.toJson(serviceTypeVOS));
        customerVO.setAnonymousInteger(bankId);
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
                    Utility.showSingleButtonDialog(AdditionalService.this,"Alert",sb.toString(),false);
                }else {
                    //set session customer or local cache
                    String json = new Gson().toJson(customerVO);
                    Session.set_Data_Sharedprefence(AdditionalService.this,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(AdditionalService.this, Session.LOCAL_CACHE,customerVO.getLocalCache());

                    Intent intent =new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
                if(enachMandateStatus){
                    Intent intent =new Intent();
                    setResult(RESULT_OK,intent);
                    finish();
                }else{
                    Utility.showSingleButtonDialog(AdditionalService.this,"Alert",data.getStringExtra("msg"),false);
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnskip:
               /* startActivity(new Intent(AdditionalService.this,Home.class));
                finish();*/
                backbuttonfun();

                break;
            case R.id.btnadd :
                ArrayList<Integer> addservice=new ArrayList<>();
                SparseBooleanArray checked = myAdapter.mCheckStates;
                int size = checked.size(); // number of name-value pairs in the array
                for (int i = 0; i < size; i++) {
                    int key = checked.keyAt(i);
                    boolean value = checked.get(key);
                    if (value){
                        addservice.add(myAdapter.mCheckStates.keyAt(i));
                    }
                }
                if(addservice.size()==0){
                    Toast.makeText(this, " no any new  service selected ", Toast.LENGTH_LONG).show();
                }else {
                    saveServiceAdd(addservice);
                }
                break;
            case R.id.back_activity_button :
                backbuttonfun();
                break;
        }
    }
    public void backbuttonfun(){
       finish();
    }
}

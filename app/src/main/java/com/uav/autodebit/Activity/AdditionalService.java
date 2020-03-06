package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.BannerBO;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewItemCheckboxBaseAdapter;
import com.uav.autodebit.androidFragment.Profile;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AdditionalService extends AppCompatActivity implements View.OnClickListener {
    ExpandableHeightListView listview;
    Button btnadd,btnskip;
    ListViewItemCheckboxBaseAdapter myAdapter;
    List<ServiceTypeVO> utilityServices;
    List<ServiceTypeVO> servicelist;

    ServiceTypeVO selectServiceTypeVo=null;


    ImageView back_activity_button ;
    BottomNavigationView navigation;

    UAVProgressDialog pd;
    List<ServiceTypeVO> newList;

    boolean onActivityResult=false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_additional_service);
        getSupportActionBar().hide();

        pd=new UAVProgressDialog(this);


        listview=findViewById(R.id.listview);
        btnadd=findViewById(R.id.btnadd);
        btnskip=findViewById(R.id.btnskip);
        back_activity_button=findViewById(R.id.back_activity_button);


        selectServiceTypeVo=null;

        onActivityResult=getIntent().getBooleanExtra("onactivityresult",false);
        selectServiceTypeVo=(ServiceTypeVO) getIntent().getSerializableExtra("servicelist");



        BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,false, new BackgroundServiceInterface() {
            @Override
            public void doInBackGround() {

                Gson  gson =new Gson();

                LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(AdditionalService.this, Session.LOCAL_CACHE), LocalCacheVO.class);
                utilityServices = localCacheVO.getUtilityBills();
                servicelist=localCacheVO.getSerives();

                newList = new ArrayList<ServiceTypeVO>();
                newList.addAll(utilityServices);
                newList.addAll(servicelist);

                if(selectServiceTypeVo!=null){
                   newList.clear();
                   newList.add(selectServiceTypeVo);

                   ArrayList<ServiceTypeVO> serviceTypeVOS =new ArrayList<>();
                   serviceTypeVOS.addAll(utilityServices);
                   serviceTypeVOS.addAll(servicelist);
                   for(ServiceTypeVO serviceTypeVO :serviceTypeVOS){
                       if(!serviceTypeVO.getServiceTypeId().equals(selectServiceTypeVo.getServiceTypeId()) && serviceTypeVO.getLevel().getLevelId()<=Session.getCustomerLevel(AdditionalService.this  ) && serviceTypeVO.getAdopted()==0){
                           newList.add(serviceTypeVO);
                       }
                   }
                }
            }
            @Override
            public void doPostExecute() {
                myAdapter=new ListViewItemCheckboxBaseAdapter(AdditionalService.this, newList, R.layout.checkbox_with_text,(selectServiceTypeVo!=null?selectServiceTypeVo.getServiceTypeId():null));
                listview.setAdapter(myAdapter);
                listview.setExpanded(true);
            }
        });

        backgroundAsyncService.execute();







        navigation = findViewById(R.id.navigation);
        navigation.getMenu().setGroupCheckable(0, false, true);
        BottomNavigationView  navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        btnskip.setOnClickListener(this);
        btnadd.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

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
                    Toast.makeText(AdditionalService.this, "bottom_history", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.bottom_help:
                    Toast.makeText(AdditionalService.this, "bottom_help", Toast.LENGTH_SHORT).show();
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
            // your code
           /* Intent intent=new Intent(AdditionalService.this,Home.class);
            startActivity(intent);
            finish();*/
            backbuttonfun();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void saveServiceAdd(ArrayList arrayList){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = BannerBO.saveCustomerService();

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
                        String[] buttons = {"OK"};
                        Utility.showSingleButtonDialogconfirmation(AdditionalService.this, new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) (ok) -> {
                            ok.dismiss();
                            startActivityForResult(new Intent(AdditionalService.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice",arrayList), ApplicationConstant.REQ_ENACH_MANDATE);
                        }), "Alert", customerVO1.getErrorMsgs().get(0), buttons);
                    } else if (customerVO1.getStatusCode().equals("ap103")) {
                        String[] buttons = {"New Mandate", "Choose Bank"};
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

                                    Utility.alertselectdialog(AdditionalService.this, "Choose from existing Bank", customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (s) -> {
                                        if (!s.equals("0")) {
                                            Log.w("Home_value", s);
                                            //setBankForService(--, Integer.parseInt(Session.getCustomerId(AdditionalService.this)), Integer.parseInt(s));
                                        } else {
                                            startActivityForResult(new Intent(AdditionalService.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice",arrayList), ApplicationConstant.REQ_ENACH_MANDATE);
                                        }
                                    }));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utility.exceptionAlertDialog(AdditionalService.this, "Alert!", "Something went wrong, Please try again!", "Report", Utility.getStackTrace(e));
                                }
                            }
                            @Override
                            public void modify(Dialog dialog) {
                                dialog.dismiss();
                                startActivityForResult(new Intent(AdditionalService.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice",arrayList), ApplicationConstant.REQ_ENACH_MANDATE);
                            }
                        }, AdditionalService.this, customerVO1.getErrorMsgs().get(0), "Alert", buttons);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                if(!onActivityResult){
                    saveServiceAdd(addservice);
                }else {
                    Intent intent =new Intent();
                    intent.putIntegerArrayListExtra("selectservice",addservice);
                    setResult(RESULT_OK,intent);
                    finish();
                }
                break;
            case R.id.back_activity_button :
                /*startActivity(new Intent(AdditionalService.this,Home.class));
                finish();*/
                backbuttonfun();
                break;
        }
    }


    public void backbuttonfun(){
       finish();
    }
}

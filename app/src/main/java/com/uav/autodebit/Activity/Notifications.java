package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.adpater.NotificationAdapter;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.CallBackInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.SQlLite.DataBaseHelper;
import com.uav.autodebit.SQlLite.GetSqlLiteData;
import com.uav.autodebit.SQlLite.InsertDateOnSqlLite;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerNotificationVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Notifications extends Base_Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener  {
    RecyclerView recyclerView;
    ImageView back_activity_button,clear_notification;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout no_notification;
    List<CustomerNotificationVO> customerNotificationVOS;
    NotificationAdapter notificationAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide();
        back_activity_button=findViewById(R.id.back_activity_button);
        recyclerView=findViewById(R.id.recyclerView);
        no_notification=findViewById(R.id.no_notification);
        clear_notification=findViewById(R.id.clear_notification);


        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        back_activity_button.setOnClickListener(this);
        clear_notification.setOnClickListener(this);

        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
       // recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));



        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                // Fetching data from server
                // loadRecyclerViewData();
                loadRecyclerViewData();
            }
        });



    }

    private void loadRecyclerViewData(){
        mSwipeRefreshLayout.setRefreshing(true);

        getdata(new CallBackInterface((CallBackInterface.OnSuccess)(Success)->{
            List<CustomerNotificationVO> customerNotificationVOS = (List<CustomerNotificationVO>) Success;

            if(customerNotificationVOS.size()>0){
                notificationIsExist(true);
            }else {
                notificationIsExist(false);
            }
            notificationAdapter=new NotificationAdapter(Notifications.this, customerNotificationVOS);
            recyclerView.setAdapter(notificationAdapter);
            recyclerView.setLayoutAnimation(Utility.getRunLayoutAnimation(Notifications.this));
            recyclerView.getAdapter().notifyDataSetChanged();
            recyclerView.scheduleLayoutAnimation();
            mSwipeRefreshLayout.setRefreshing(false);
        }));
    }

    private void  getdata(CallBackInterface  callBackInterface) {
        customerNotificationVOS =new ArrayList<>();
        try {
            if(DataBaseHelper.checkDataBase() && ((GetSqlLiteData.getNotification(Notifications.this)).size()>0 || Session.getSessionByKey_BoolenValue(Notifications.this,Session.CACHE_IS_CLEAR_NOTIFICATION))){
                customerNotificationVOS= GetSqlLiteData.getNotification(Notifications.this);
                callBackInterface.onSuccess(customerNotificationVOS);
            }else {
                getNotificationList(new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                    CustomerNotificationVO responseVO = (CustomerNotificationVO)success;

                    ArrayList<CustomerNotificationVO> datalist= (ArrayList<CustomerNotificationVO>) new Gson().fromJson(responseVO.getAnonymousString(), new TypeToken<ArrayList<CustomerNotificationVO>>() { }.getType());
                    for(CustomerNotificationVO customerNotificationVO:datalist){
                        InsertDateOnSqlLite.insertNotification(Notifications.this,customerNotificationVO);
                    }
                    customerNotificationVOS= GetSqlLiteData.getNotification(Notifications.this);
                    callBackInterface.onSuccess(customerNotificationVOS);
                }));

            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(Notifications.this , Utility.getStackTrace(e));
           // Utility.exceptionAlertDialog(Notifications.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    private void getNotificationList(VolleyResponse volleyResponse){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = CustomerBO.getCustomerNotification();
        connectionVO.setLoaderAvoided(true);

        CustomerNotificationVO customerNotificationVO =new CustomerNotificationVO();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Notifications.this)));

        customerNotificationVO.setCustomer(customerVO);

        Gson gson =new Gson();
        String json = gson.toJson(customerNotificationVO);
        Log.w("request",json);
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
                CustomerNotificationVO responseVO = gson.fromJson(response.toString(), CustomerNotificationVO.class);

                if(responseVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) responseVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Notifications.this,responseVO.getDialogTitle(),sb.toString(),true);
                }else {
                    //update customer cache
                    volleyResponse.onSuccess(responseVO);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_activity_button) {
            finish();
        }else if(view.getId() == R.id.clear_notification){
            Utility.enableDisableView(view,false);

            String [] btn={"No","Yes"};
            Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                @Override
                public void confirm(Dialog dialog) {
                    notificationIsExist(false);
                    Utility.dismissDialog(Notifications.this,dialog);
                    customerNotificationVOS.clear(); // clear list
                    recyclerView.getAdapter().notifyDataSetChanged();  // let your adapter know about the changes and reload view
                    GetSqlLiteData.deleteNotificationData(Notifications.this,"notification");
                    Utility.enableDisableView(view,true);
                }
                @Override
                public void modify(Dialog dialog) {
                    Utility.dismissDialog(Notifications.this,dialog);
                    Utility.enableDisableView(view,true);
                }
            }, Notifications.this,"Do you want to remove all notifications ?" ,null,btn);
        }
    }

    public void notificationIsExist(boolean isNotificationExist){

        if(!isNotificationExist){
            clear_notification.setVisibility(View.GONE);
            no_notification.setVisibility(View.VISIBLE);
        }else {
            clear_notification.setVisibility(View.VISIBLE);
            no_notification.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRefresh() {
        // Fetching data from server
        loadRecyclerViewData();
    }
    public void setActionOnNotificationClick(String data)throws Exception{
        JSONObject jsonObject =new JSONObject(data);
        Class <?>clazz = Class.forName(getApplicationContext().getPackageName()+".Activity."+jsonObject.getString("key"));
        Intent intent =new Intent(this, clazz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(ApplicationConstant.NOTIFICATION_ACTION,jsonObject.toString());
        startActivity(intent);
    }
}

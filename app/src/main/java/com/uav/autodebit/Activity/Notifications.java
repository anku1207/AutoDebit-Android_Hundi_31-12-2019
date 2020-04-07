package com.uav.autodebit.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.NotificationAdapter;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerNotificationVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.LocalCacheVO;

import java.util.ArrayList;
import java.util.List;

public class Notifications extends Base_Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    ImageView back_activity_button;
    SwipeRefreshLayout mSwipeRefreshLayout;
    LinearLayout no_notification;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide();
        back_activity_button=findViewById(R.id.back_activity_button);
        recyclerView=findViewById(R.id.recyclerView);
        no_notification=findViewById(R.id.no_notification);
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        back_activity_button.setOnClickListener(this);

        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(true);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);

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
        NotificationAdapter notificationAdapter=new NotificationAdapter(Notifications.this, getdata());
        recyclerView.setAdapter(notificationAdapter);
        recyclerView.setLayoutAnimation(Utility.getRunLayoutAnimation(Notifications.this));
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private List<CustomerNotificationVO>  getdata() {
        List<CustomerNotificationVO> customerNotificationVOS =new ArrayList<>();
        try {
            if(Session.check_Exists_key(Notifications.this,Session.CACHE_NOTIFICATION)){
                customerNotificationVOS= (List<CustomerNotificationVO>) new Gson().fromJson(Session.getSessionByKey(Notifications.this, Session.CACHE_NOTIFICATION), new TypeToken<ArrayList<CustomerNotificationVO>>() { }.getType());

            }else {
                Gson gson =new Gson();
                CustomerVO customerVO = gson.fromJson(Session.getSessionByKey(Notifications.this,Session.CACHE_CUSTOMER), CustomerVO.class);
                LocalCacheVO localCacheVO = gson.fromJson(customerVO.getLocalCache(), LocalCacheVO.class);
                customerNotificationVOS=  localCacheVO.getCustomerNotification();
                Session.set_Data_Sharedprefence(Notifications.this, Session.CACHE_NOTIFICATION, Utility.toJson(customerNotificationVOS));
            }


        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Notifications.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }

        if(customerNotificationVOS.size()>0){
            no_notification.setVisibility(View.GONE);
        }else {
            no_notification.setVisibility(View.VISIBLE);
        }

        return customerNotificationVOS;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_activity_button) {
            finish();
        }
    }

    @Override
    public void onRefresh() {
        // Fetching data from server
        loadRecyclerViewData();
    }
}

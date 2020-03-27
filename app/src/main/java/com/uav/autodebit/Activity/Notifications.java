package com.uav.autodebit.Activity;

import android.app.Notification;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.ServiceClick;
import com.uav.autodebit.Notification.FCMService;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.History_List_Adapter;
import com.uav.autodebit.adpater.NotificationAdapter;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Notifications extends Base_Activity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    RecyclerView recyclerView;
    ImageView back_activity_button;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide();
        back_activity_button=findViewById(R.id.back_activity_button);
        recyclerView=findViewById(R.id.recyclerView);
        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);

        back_activity_button.setOnClickListener(this);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);




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

    private List<DataAdapterVO>  getdata() {
        List<DataAdapterVO> dataAdapterVOS =new ArrayList<>();
        try {
            if (Session.check_Exists_key(Notifications.this, Session.CACHE_NOTIFICATION)) {
                JSONArray notificationarry = new JSONArray(Session.getSessionByKey(Notifications.this, Session.CACHE_NOTIFICATION));
                for(int i=0 ; i < notificationarry.length(); i++){
                    JSONObject jsonObject =notificationarry.getJSONObject(i);
                    DataAdapterVO dataAdapterVO =new DataAdapterVO();
                    dataAdapterVO.setText(jsonObject.has("title")?jsonObject.getString("title"):"");
                    dataAdapterVO.setText2(jsonObject.has("message")?jsonObject.getString("message"):"");
                    dataAdapterVO.setTxnDate(jsonObject.has("timestamp")?jsonObject.getString("timestamp"):new Date().getTime()+"");
                    dataAdapterVO.setImageUrl(jsonObject.has("smallImageUrl") && !jsonObject.getString("smallImageUrl").equals("")?jsonObject.getString("smallImageUrl"):null);
                    dataAdapterVO.setImagename(jsonObject.has("imageUrl")&& !jsonObject.getString("imageUrl").equals("")?jsonObject.getString("imageUrl"):null);
                    dataAdapterVOS.add(dataAdapterVO);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Notifications.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
        return dataAdapterVOS;
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

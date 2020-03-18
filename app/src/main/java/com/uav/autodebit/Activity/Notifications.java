package com.uav.autodebit.Activity;

import android.app.Notification;
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

public class Notifications extends Base_Activity implements View.OnClickListener {
    RecyclerView recyclerView;
    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        getSupportActionBar().hide();
        back_activity_button=findViewById(R.id.back_activity_button);
        recyclerView=findViewById(R.id.recyclerView);

        back_activity_button.setOnClickListener(this);

        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(false);




        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(false);
        linearLayoutManager.setStackFromEnd(false);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(linearLayoutManager);


        getdata(new ServiceClick((ServiceClick.OnSuccess)(s)->{
            if(((List<DataAdapterVO>) s).size()>0){

                NotificationAdapter notificationAdapter=new NotificationAdapter(Notifications.this, (List<DataAdapterVO>) s);
                recyclerView.setAdapter(notificationAdapter);
                recyclerView.setLayoutAnimation(Utility.getRunLayoutAnimation(Notifications.this));
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();


            }

        }));
    }



    private void getdata(ServiceClick serviceClick) {
        try{

            List<DataAdapterVO> dataAdapterVOS =new ArrayList<>();
            DataAdapterVO dataAdapterVO ;

            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 1");
            dataAdapterVO.setText2("text 1");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://image.shutterstock.com/image-photo/mountains-during-sunset-beautiful-natural-260nw-407021107.jpg");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);

            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 2");
            dataAdapterVO.setText2("text 2");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://image.shutterstock.com/image-photo/bright-spring-view-cameo-island-260nw-1048185397.jpg");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 3");
            dataAdapterVO.setText2("text 3");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl(null);
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 4");
            dataAdapterVO.setText2("text 4");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl(null);
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 5");
            dataAdapterVO.setText2("text 5");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);

            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 6");
            dataAdapterVO.setText2("text 6");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 7");
            dataAdapterVO.setText2("text 7");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);

            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 8");
            dataAdapterVO.setText2("text 8");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 9");
            dataAdapterVO.setText2("text 9");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl(null);
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            dataAdapterVO =new DataAdapterVO();
            dataAdapterVO.setText("text 10");
            dataAdapterVO.setText2("text 10");
            dataAdapterVO.setTxnDate(new Date()+"");
            dataAdapterVO.setImageUrl("https://image.shutterstock.com/image-photo/bright-spring-view-cameo-island-260nw-1048185397.jpg");
            dataAdapterVO.setImagename("https://www.google.com//images/branding/googlelogo/2x/googlelogo_color_272x92dp.png");
            dataAdapterVOS.add(dataAdapterVO);


            serviceClick.onSuccess(dataAdapterVOS);
        } catch (Exception e) {
            e.printStackTrace();
            Utility.exceptionAlertDialog(Notifications.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_activity_button) {
            finish();
        }
    }
}

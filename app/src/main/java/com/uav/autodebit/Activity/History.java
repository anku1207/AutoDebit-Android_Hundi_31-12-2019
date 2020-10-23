package com.uav.autodebit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.ServiceClick;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.History_List_Adapter;
import com.uav.autodebit.exceptions.ExceptionsNotification;
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
import java.util.HashMap;
import java.util.List;

public class History extends Base_Activity implements View.OnClickListener {
    RecyclerView recyclerView;
    ImageView back_activity_button;
    TextView emptymsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        getSupportActionBar().hide();

        recyclerView=findViewById(R.id.recyclerView);
        back_activity_button=findViewById(R.id.back_activity_button);
        emptymsg=findViewById(R.id.emptymsg);
        back_activity_button.setOnClickListener(this);

        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(false);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);


       // recyclerView.setLayoutManager(new LinearLayoutManager(History.this, LinearLayoutManager.VERTICAL, true));

        getdata(new ServiceClick((ServiceClick.OnSuccess)(s)->{
          if(((List<DataAdapterVO>) s).size()>0){
              emptymsg.setVisibility(View.GONE);
              History_List_Adapter history_list_adapter=new History_List_Adapter(History.this, (List<DataAdapterVO>) s,R.layout.design_history_card_list);
              recyclerView.setAdapter(history_list_adapter);
              recyclerView.setLayoutAnimation(Utility.getRunLayoutAnimation(History.this));
              recyclerView.getAdapter().notifyDataSetChanged();
              recyclerView.scheduleLayoutAnimation();
            }else {
              emptymsg.setVisibility(View.VISIBLE);
          }

        }));

    }

    private void getdata(ServiceClick serviceClick) {
        ArrayList<DataAdapterVO> dataAdapterVOS=new ArrayList<>();
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.getCustomerhistory();
            CustomerVO request_data =new CustomerVO();
            request_data.setCustomerId(Integer.parseInt(Session.getCustomerId(History.this)));
            Gson gson = new Gson();
            String json = gson.toJson(request_data);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(History.this,connectionVO, new VolleyResponseListener() {
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
                        Utility.showSingleButtonDialog(History.this,customerVO.getDialogTitle(),
                                sb.toString(),false);
                    }else {
                          JSONArray jsonArray =new JSONArray(customerVO.getAnonymousString());
                          for(int i=0;i<jsonArray.length();i++) {

                              JSONObject jsonObject =jsonArray.getJSONObject(i);
                              DataAdapterVO dataAdapterVO =new DataAdapterVO();

                              JSONObject dataJson=jsonObject.getJSONObject("data");

                              dataAdapterVO.setServiceName(dataJson.has("ServiceName")? dataJson.getString("ServiceName"):"");
                              dataAdapterVO.setNumber(dataJson.has("no")?dataJson.getString("no"):"");
                              dataAdapterVO.setStatus(dataJson.has("status")?dataJson.getString("status"):"");
                              dataAdapterVO.setCustmerPassBookId(dataJson.has("customerPassBookId")?dataJson.getInt("customerPassBookId"):null);
                              dataAdapterVO.setQuestionsData(jsonObject.has("chargesarray")? jsonObject.getJSONArray("chargesarray").toString():null);
                              dataAdapterVO.setLink(dataJson.has("link")?dataJson.getString("link"):"");
                              dataAdapterVOS.add(dataAdapterVO);
                          }
                        serviceClick.onSuccess(dataAdapterVOS);
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                backbuttonfun();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backbuttonfun();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void backbuttonfun(){
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }




    public void getHistoryDetailById(Integer id , VolleyResponse volleyResponse){

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.getCustomerhistory();
            CustomerVO request_data =new CustomerVO();
            request_data.setCustomerId(Integer.parseInt(Session.getCustomerId(History.this)));
            Gson gson = new Gson();
            String json = gson.toJson(request_data);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(History.this,connectionVO, new VolleyResponseListener() {
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
                        Utility.showSingleButtonDialog(History.this,customerVO.getDialogTitle(),sb.toString(),false);
                    }else {


                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }


}

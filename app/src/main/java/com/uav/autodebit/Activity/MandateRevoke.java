package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewItemCheckboxBaseAdapter;
import com.uav.autodebit.adpater.Revoke_ListViewItemCheckbox;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MandateRevoke extends Base_Activity implements View.OnClickListener {

    ExpandableHeightListView listview;
    Button btnadd;
    Revoke_ListViewItemCheckbox myAdapter;
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
        setContentView(R.layout.activity_mandate_revoke);
        getSupportActionBar().hide();


        listview=findViewById(R.id.listview);
        btnadd=findViewById(R.id.btnadd);
        back_activity_button=findViewById(R.id.back_activity_button);
        buttonLayout=findViewById(R.id.buttonLayout);

        btnadd.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        getMandateRevokeList(new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            buttonLayout.setVisibility(View.VISIBLE);
        }));
    }

    private void getMandateRevokeList(VolleyResponse volleyResponse){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.getServiceMandateList();
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
                    Utility.showSingleButtonDialog(MandateRevoke.this,"Alert",sb.toString(),true);
                }else {
                    ArrayList<ServiceTypeVO> serviceTypeVOS= (ArrayList<ServiceTypeVO>) new Gson().fromJson(customerVO.getAnonymousString(), new TypeToken<ArrayList<ServiceTypeVO>>() { }.getType());
                    myAdapter=new Revoke_ListViewItemCheckbox(MandateRevoke.this, serviceTypeVOS, R.layout.checkbox_with_text);
                    listview.setAdapter(myAdapter);
                    listview.setExpanded(true);

                    volleyResponse.onSuccess(customerVO);
                }
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){

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

                    Log.w("CheckServiceListIds",addservice.toString());
                    //saveServiceAdd(addservice);
                }
                break;
            case R.id.back_activity_button :
                finish();
                break;
        }

    }
}
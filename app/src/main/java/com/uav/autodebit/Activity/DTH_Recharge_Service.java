package com.uav.autodebit.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class DTH_Recharge_Service extends AppCompatActivity {
    EditText operator,amount,subscriberid;

    String operatorname,operatorcode,serviceid;
    Button proceed;
    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_dth__recharge__service);
        getSupportActionBar().hide();

        operator=findViewById(R.id.operator);
        amount=findViewById(R.id.amount);
        proceed=findViewById(R.id.proceed);
        subscriberid=findViewById(R.id.subscriberid);
        back_activity_button=findViewById(R.id.back_activity_button1);

        Intent intent =getIntent();
        serviceid=intent.getStringExtra("serviceid");



        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        operator.setClickable(false);

        operator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    //startActivity(new Intent(Mobile_Prepaid_Recharge_Service.this,Listview_With_Image.class));
                    Intent intent =new Intent(DTH_Recharge_Service.this, Listview_With_Image.class);
                    Gson gson = new Gson();
                    String data = gson.toJson(getDataList());
                    intent.putExtra("datalist", data);
                    intent.putExtra("title","DTH Operator");
                    startActivityForResult(intent,100);
                }
                return false;
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid=true;

                if(operator.getText().toString().equals("")){
                    operator.setError("this filed is required");
                    valid=false;
                }
                if(subscriberid.getText().toString().equals("")){
                    subscriberid.setError("this filed is required");
                    valid=false;
                }

                if(amount.getText().toString().equals("")){
                    amount.setError("this filed is required");
                    valid=false;
                }

                if(valid){
                    dthrecharge();
                }


            }
        });


    }

    public void  dthrecharge(){
        HashMap<String, Object> params = new HashMap<String, Object>();

        ConnectionVO connectionVO = OxigenPlanBO.oxiDTHRechargeTransaction();

        OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
        oxigenTransactionVO.setReferenceName("DTH");
        oxigenTransactionVO.setReferenceValue(subscriberid.getText().toString().trim());
        oxigenTransactionVO.setAmount(Double.parseDouble(amount.getText().toString().trim()));
        oxigenTransactionVO.setOperateName(operatorname);
        oxigenTransactionVO.setAnonymousString(operatorcode);

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(DTH_Recharge_Service.this)));
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
                    Utility.showSingleButtonDialog(DTH_Recharge_Service.this,"Error !",stringBuffer.toString(),false);
                }else {
                    Utility.showSingleButtonDialog(DTH_Recharge_Service.this,"Success !",oxigenPlanresp.getAnonymousString(),true);;

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            switch (requestCode) {
                case 100:
                    operatorname = data.getStringExtra("operatorname");
                    operatorcode = data.getStringExtra("operator");
                    operator.setText(operatorname);
                    operator.setError(null);
                    amount.setError(null);
                    subscriberid.setError(null);
                    subscriberid.requestFocus();
                    subscriberid.setSelection(subscriberid.length());
                    break;
            }

        }



    }

    public ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator= Session.getSessionByKey(DTH_Recharge_Service.this,Session.DTH_OPERATOR_LIST);
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
}

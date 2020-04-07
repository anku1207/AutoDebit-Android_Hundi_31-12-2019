package com.uav.autodebit.Activity;

import android.graphics.Color;
import android.graphics.Typeface;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.StatusVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class HistorySummary extends Base_Activity implements View.OnClickListener {
    LinearLayout main,payment_detail_Layout;
    TextView service_name,number,order_id,status;
    ImageView service_Icon,back_activity_button;
    HashMap<Integer,JSONObject> colorhash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_summary);
        getSupportActionBar().hide();

        main=findViewById(R.id.main);
        service_name=findViewById(R.id.service_name);
        number=findViewById(R.id.number);
        order_id=findViewById(R.id.order_id);
        service_Icon=findViewById(R.id.service_Icon);
        payment_detail_Layout=findViewById(R.id.payment_detail_Layout);
        back_activity_button=findViewById(R.id.back_activity_button);
        status=findViewById(R.id.status);

        back_activity_button.setOnClickListener(this);

        String historyId=getIntent().getStringExtra("historyId");

        try {
           colorhash=new HashMap<>();

            JSONObject jsonObject =new JSONObject();
            jsonObject.put("color","#ff0000");
            jsonObject.put("name","panding");
            colorhash.put(StatusVO.PENDING,jsonObject);

            jsonObject =new JSONObject();
            jsonObject.put("color","#00b300");
            jsonObject.put("name","success");
            colorhash.put(StatusVO.SUCCESSFULL,jsonObject);

            jsonObject =new JSONObject();
            jsonObject.put("color","#00b300");
            jsonObject.put("name","");
            colorhash.put(0,jsonObject);

            jsonObject =new JSONObject();
            jsonObject.put("color","#ff0000");
            jsonObject.put("name","fail");
            colorhash.put(6,jsonObject);



        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(HistorySummary.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }



        getHistorySummaryById(historyId,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            try {
                main.setVisibility(View.VISIBLE);
                CustomerVO customerVO =(CustomerVO)s;

                Picasso.with(this).load(customerVO.getImage()).into(service_Icon);


                JSONObject respjsonObject  =new JSONObject(customerVO.getAnonymousString());

                service_name.setText(respjsonObject.getJSONObject("data").getString("serviceName"));
                number.setText(respjsonObject.getJSONObject("data").getString("no"));
                order_id.setText(respjsonObject.getJSONObject("data").getString("txnId"));

                JSONObject  jsonObject1 =colorhash.get(respjsonObject.getJSONObject("data").getInt("status"));
                status.setText(jsonObject1.getString("name"));
                status.setTextColor(Color.parseColor(jsonObject1.getString("color")));

                JSONArray jsonArray =respjsonObject.getJSONArray("chargesarray");

                Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);


                LinearLayout.LayoutParams layoutparams ;
                int marginInDp = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5, this.getResources()
                                .getDisplayMetrics());

                for(int i=0 ;i<jsonArray.length();i++){
                    JSONObject jsonObject =jsonArray.getJSONObject(i);

                    LinearLayout et = new LinearLayout(new ContextThemeWrapper(HistorySummary.this,R.style.confirmation_dialog_layout));

                    TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));

                    layoutparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float)1 );

                    layoutparams.setMargins(0,marginInDp,0,marginInDp);

                    text.setLayoutParams(layoutparams);
                    text.setText(jsonObject.getString("key"));
                    text.setMaxLines(1);
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setTypeface(typeface);
                    text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);


                    TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));

                    layoutparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float).5);
                    layoutparams.setMargins(0,marginInDp,0,marginInDp);

                    value.setLayoutParams(layoutparams);
                    value.setText(jsonObject.getString("value"));
                    value.setTypeface(typeface);
                    value.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

                    et.addView(text);
                    et.addView(value);
                    payment_detail_Layout.addView(et);
                }
            }catch (Exception e){
                e.printStackTrace();
                Utility.exceptionAlertDialog(HistorySummary.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
            }
        }));
    }

    private void getHistorySummaryById(String id, VolleyResponse volleyResponse) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.getHistorySumarryById();
            CustomerVO request_data =new CustomerVO();
            request_data.setCustomerId(Integer.parseInt(Session.getCustomerId(HistorySummary.this)));
            request_data.setAnonymousInteger(Integer.parseInt(id));
            Gson gson = new Gson();
            String json = gson.toJson(request_data);
            params.put("volley", json);

            Log.w("getHistorySummaryById",params.toString());
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(HistorySummary.this,connectionVO, new VolleyResponseListener() {
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
                        Utility.showSingleButtonDialog(HistorySummary.this,"Error !",sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(customerVO);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utility.exceptionAlertDialog(HistorySummary.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                finish();
                break;
        }
    }
}

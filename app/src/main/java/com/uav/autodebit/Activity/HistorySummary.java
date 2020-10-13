package com.uav.autodebit.Activity;

import android.content.res.ColorStateList;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
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
    LinearLayout main,payment_detail_Layout,payment_amt_date;
    TextView service_name,number,order_id,status;
    ImageView service_Icon,back_activity_button;

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
        payment_amt_date=findViewById(R.id.payment_amt_date);
        payment_detail_Layout=findViewById(R.id.payment_detail_Layout);
        back_activity_button=findViewById(R.id.back_activity_button);
        status=findViewById(R.id.status);

        back_activity_button.setOnClickListener(this);

        String historyId=getIntent().getStringExtra("historyId");


        getHistorySummaryById(historyId,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            try {
                main.setVisibility(View.VISIBLE);
                CustomerVO customerVO =(CustomerVO)s;

                Picasso.with(this).load(customerVO.getImage()).into(service_Icon);

                JSONObject respjsonObject  =new JSONObject(customerVO.getAnonymousString());

                service_name.setText(respjsonObject.getJSONObject("data").getString("serviceName"));
                number.setText(respjsonObject.getJSONObject("data").getString("no"));
                order_id.setText(respjsonObject.getJSONObject("data").getString("txnId"));


                status.setText(respjsonObject.getJSONObject("data").getString("statusName"));
                status.setTextColor(Color.parseColor(respjsonObject.getJSONObject("data").getString("statusColor")));

                //dynamic view for paymentAmountAndDate
                if(respjsonObject.has("header")&& respjsonObject.getJSONArray("header").length()>0) {
                    JSONArray jsonArrayAD = respjsonObject.getJSONArray("header");


                    Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);


                    LinearLayout.LayoutParams layoutparams;
                    int marginInDp = (int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP, 5, this.getResources()
                                    .getDisplayMetrics());

                    for (int i = 0; i < jsonArrayAD.length(); i++) {
                        JSONObject jsonObject = jsonArrayAD.getJSONObject(i);

                        LinearLayout et = new LinearLayout(new ContextThemeWrapper(HistorySummary.this, R.style.confirmation_dialog_layout));

                        TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));

                        layoutparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1);

                        layoutparams.setMargins(0, marginInDp, 0, marginInDp);

                        text.setLayoutParams(layoutparams);
                        text.setText(jsonObject.getString("key"));
                        text.setMaxLines(2);
                        text.setEllipsize(TextUtils.TruncateAt.END);
                        text.setTypeface(typeface);
                        text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);


                        TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));

                        layoutparams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) .5);
                        layoutparams.setMargins(0, marginInDp, 0, marginInDp);

                        value.setLayoutParams(layoutparams);
                        value.setText(jsonObject.getString("value"));
                        value.setTypeface(typeface);
                        value.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

                        et.addView(text);
                        et.addView(value);
                        payment_amt_date.addView(et);
                    }
                }
                else{
                    payment_amt_date.setVisibility(View.GONE);
                    findViewById(R.id.tvPS).setVisibility(View.GONE);
                }
                //dynamic view for paymentDetails
                JSONArray jsonArray =respjsonObject.getJSONArray("chargesarray");

                Typeface tf = ResourcesCompat.getFont(this, R.font.poppinssemibold);


                LinearLayout.LayoutParams lp ;
                int margin = (int) TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP, 5, this.getResources()
                                .getDisplayMetrics());

                for(int i=0 ;i<jsonArray.length();i++){
                    JSONObject jsonObject =jsonArray.getJSONObject(i);

                    LinearLayout et = new LinearLayout(new ContextThemeWrapper(HistorySummary.this,R.style.confirmation_dialog_layout));

                    TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));

                    lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float)1 );

                    lp.setMargins(0,margin,0,margin);

                    text.setLayoutParams(lp);
                    text.setText(jsonObject.getString("key"));
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setTypeface(tf);
                    text.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                    TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));

                    lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float).5);
                    lp.setMargins(0,margin,0,margin);

                    value.setLayoutParams(lp);
                    value.setText(jsonObject.getString("value"));
                    value.setTypeface(tf);
                    value.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);

                    et.addView(text);
                    et.addView(value);
                    payment_detail_Layout.addView(et);
                }
            }catch (Exception e){
                ExceptionsNotification.ExceptionHandling(HistorySummary.this , Utility.getStackTrace(e));
                //Utility.exceptionAlertDialog(HistorySummary.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
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
                        Utility.showSingleButtonDialog(HistorySummary.this,customerVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(customerVO);
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(HistorySummary.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(HistorySummary.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
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

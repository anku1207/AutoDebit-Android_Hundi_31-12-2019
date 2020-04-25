package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.OxigenPlanVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PayBillByNotification extends AppCompatActivity implements View.OnClickListener {
    LinearLayout dynamic_layout,fetchbilllayout;
    Button bill_pay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill_by_notification);
        getSupportActionBar().hide();
        dynamic_layout=findViewById(R.id.dynamic_layout);
        fetchbilllayout=findViewById(R.id.fetchbilllayout);
        bill_pay=findViewById(R.id.bill_pay);
        bill_pay.setOnClickListener(this);
        try {
            JSONObject jsonObject =new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));

            Toast.makeText(this, ""+jsonObject.getString("key") + "==== "+ jsonObject.getString("value"), Toast.LENGTH_SHORT).show();


            for(int i=0; i<=Integer.parseInt(jsonObject.getString("value")); i++){
                CardView cardView = Utility.getCardViewStyle(this);
                UAVEditText et = Utility.getUavEditText(this);
                et.setId(View.generateViewId());
                et.setHint("text" + i);
                cardView.addView(et);
                dynamic_layout.addView(cardView);





                    LinearLayout et1 = new LinearLayout(new ContextThemeWrapper(this,R.style.confirmation_dialog_layout));

                    et1.setPadding(Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10));

                    TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));
                    text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                    text.setText("key" +1);
                    text.setMaxLines(1);
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                    TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));
                    value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                    value.setText("value"+1);
                    value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                    et1.addView(text);
                    et1.addView(value);
                    fetchbilllayout.addView(et1);



            }






        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.bill_pay){
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = OxigenPlanBO.getPlan();

            OxigenPlanVO oxigenPlanVO =new OxigenPlanVO();
            ServiceTypeVO serviceTypeVO =new ServiceTypeVO();

            Gson gson=new Gson();
            String json = gson.toJson(oxigenPlanVO);
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
                    OxigenPlanVO oxigenPlanVO = gson.fromJson(response.toString(), OxigenPlanVO.class);


                    if(oxigenPlanVO.getStatusCode().equals("400")){

                        StringBuffer stringBuffer= new StringBuffer();

                        for(int i=0;i<oxigenPlanVO.getErrorMsgs().size();i++){
                            stringBuffer.append(oxigenPlanVO.getErrorMsgs().get(i));
                        }
                        Utility.showSingleButtonDialog(PayBillByNotification.this,"Error !",stringBuffer.toString(),false);
                    }else {

                    }
                }
            });
        }

    }
}

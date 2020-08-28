package com.uav.autodebit.Activity;

import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Confirm_Bank_Details extends Base_Activity implements View.OnClickListener {
    LinearLayout mainlayout,buttonLayout;
    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm__bank__details);
        getSupportActionBar().hide();

        back_activity_button=findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);


        mainlayout=findViewById(R.id.mainlayout);
        buttonLayout=findViewById(R.id.buttonLayout);
        getBankDetails(getIntent().getIntExtra("bankid",0));
    }

    private void getBankDetails(int bankid) {
        if(bankid==0){
            Toast.makeText(this, "Bank id is Null", Toast.LENGTH_SHORT).show();
        }else {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = CustomerBO.cutomerBankDetails();
            CustomerVO customerVO =new CustomerVO();
            customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(this)));

            CustomerAuthServiceVO customerAuthServiceVO=new CustomerAuthServiceVO();
            customerAuthServiceVO.setCustomerAuthId(bankid);
            customerAuthServiceVO.setCustomer(customerVO);

            Gson gson = new Gson();
            String json = gson.toJson(customerAuthServiceVO);
            params.put("volley", json);

            connectionVO.setParams(params);
            Log.w("getBankDetails",params.toString());
            VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    CustomerAuthServiceVO customerAuthServiceVO = gson.fromJson(response.toString(), CustomerAuthServiceVO.class);
                    if(customerAuthServiceVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) customerAuthServiceVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(Confirm_Bank_Details.this,customerAuthServiceVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        try {
                            crateBankDetailsLayout(customerAuthServiceVO);
                        }catch (Exception e){
                            ExceptionsNotification.ExceptionHandling(Confirm_Bank_Details.this , Utility.getStackTrace(e));
                            //  Utility.exceptionAlertDialog(Confirm_Bank_Details.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                        }
                    }
                }
            });

        }
    }

    private void crateBankDetailsLayout(CustomerAuthServiceVO customerAuthServiceVO){
        try{
            JSONArray jsonArray=new JSONArray();
            JSONObject object =new JSONObject();
            object.put("key","Bank Name");
            object.put("value",customerAuthServiceVO.getBankName());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Account No");
            object.put("value",customerAuthServiceVO.getAccountNumber());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Status");
            object.put("value",customerAuthServiceVO.getAuthStatus().getStatusName());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Mandate Amount");
            object.put("value",customerAuthServiceVO.getMandateAmount());
            jsonArray.put(object);


            Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);

            TextView bankAccountTitle = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_text_flied));
            bankAccountTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            bankAccountTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            bankAccountTitle.setText("Bank Account");
            bankAccountTitle.setTypeface(typeface);
            bankAccountTitle.setTextColor(Utility.getColorWrapper(this,R.color.colorPrimaryDark));
            bankAccountTitle.setTextSize(18);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0,Utility.dpToPx(this,20),0,Utility.dpToPx(this,20));
            bankAccountTitle.setLayoutParams(params);


            mainlayout.addView(bankAccountTitle);

            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject =jsonArray.getJSONObject(i);
                LinearLayout et = new LinearLayout(new ContextThemeWrapper(this,R.style.confirmation_dialog_layout));

                TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));
                text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                text.setText(jsonObject.getString("key"));
                text.setMaxLines(1);
                text.setEllipsize(TextUtils.TruncateAt.END);
                text.setTypeface(typeface);

                TextView text1 = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));
                text1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));
                text1.setText(" : ");
                text1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

                TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));
                value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                value.setText(jsonObject.getString("value"));
                value.setTypeface(typeface);

                et.addView(text);
                et.addView(text1);
                et.addView(value);
                mainlayout.addView(et);
            }

            TextView serviceTitle = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_text_flied));
            serviceTitle.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
            serviceTitle.setText("Service Selected");
            serviceTitle.setTypeface(typeface);
            serviceTitle.setTextColor(Utility.getColorWrapper(this,R.color.colorPrimaryDark));
            serviceTitle.setTextSize(18);
            serviceTitle.setLayoutParams(params);
            serviceTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            serviceTitle.setGravity(View.TEXT_ALIGNMENT_CENTER);

            mainlayout.addView(serviceTitle);

            GridView gridView =new GridView(this);
            gridView.setNumColumns(2);

            ArrayList<String> strings= (ArrayList<String>) new Gson().fromJson(customerAuthServiceVO.getAnonymousString(), new TypeToken<ArrayList<String>>() { }.getType());
            // Create a new ArrayAdapter
            final ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                    (this,android.R.layout.simple_list_item_1, strings);
            // Data bind GridView with ArrayAdapter (String Array elements)
            gridView.setAdapter(gridViewArrayAdapter);
            mainlayout.addView(gridView);
            buttonLayout.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(Confirm_Bank_Details.this, Content_Message.error_message, Toast.LENGTH_SHORT).show();
            ExceptionsNotification.ExceptionHandling(Confirm_Bank_Details.this , Utility.getStackTrace(e));

        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_activity_button) {
            finish();
        }
    }
}

package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.ClickableViewPager;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.CardTypeVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.vo.StatusVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Dmrc_NewAndExist_Card_Dialog extends Base_Activity implements View.OnClickListener {
    Button proceed;
    TextView dialog_title;
    Gson gson;
    LinearLayout main_layout;
    RadioGroup radiogroup ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmrc__new_and_exist__card__dialog);


        main_layout=findViewById(R.id.main_layout);
        proceed=findViewById(R.id.proceed);
        dialog_title=findViewById(R.id.dialog_title);
        radiogroup =findViewById(R.id.radiogroup);

        gson=new Gson();

        getDmrcCardTypes(Dmrc_NewAndExist_Card_Dialog.this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            try {
                BaseVO baseVO = (BaseVO) success;
                main_layout.setVisibility(View.VISIBLE);
                proceed.setText(baseVO.getAnonymousString1());
                dialog_title.setText(baseVO.getDialogTitle());

                JSONArray jsonArray = new JSONArray(baseVO.getAnonymousString());

                for(int i=0;i<jsonArray.length();i++){
                    CardTypeVO cardTypeVO1 = gson.fromJson(jsonArray.getJSONObject(i).toString(), CardTypeVO.class);
                    RadioButton rdbtn = new RadioButton(this);
                    rdbtn.setId(cardTypeVO1.getCardTypeId());
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        rdbtn.setText(Html.fromHtml(cardTypeVO1.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        rdbtn.setText(Html.fromHtml(cardTypeVO1.getDescription()));
                    }
                    rdbtn.setChecked(false);
                    rdbtn.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.setMargins(15, 15, 15, 15);
                    rdbtn.setLayoutParams(params);
                    rdbtn.setPadding(10,0,0,0);
                    rdbtn.setGravity(Gravity.TOP);
                    rdbtn.setTag(cardTypeVO1);
                    radiogroup.addView(rdbtn);
                }
                ((RadioButton)radiogroup.getChildAt(0)).setChecked(true);
            }catch ( Exception e){
                ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
            }
        }));

       //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =getWindow().getAttributes();
        params.gravity = Gravity.CENTER_VERTICAL;
        getWindow().setAttributes(params);

        proceed.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        try {
            Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageviewclickeffect);
            switch (v.getId()){
                case R.id.proceed:
                    int selectedId = radiogroup.getCheckedRadioButtonId();
                    if(selectedId!=-1){
                        // find the radiobutton by returned id
                        RadioButton radioButton = (RadioButton) findViewById(selectedId);
                        CardTypeVO cardTypeVO = (CardTypeVO) radioButton.getTag();
                        proceed.startAnimation(animation);
                        getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                            DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                            finish();
                            try {
                                Class<?> clazz = Class.forName(getApplicationContext().getPackageName() + ".Activity." + cardTypeVO.getActivityName());
                                Intent intent =new Intent(this,clazz);
                                intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                                intent.putExtra("isdisable",false);
                                intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                                intent.putExtra("cardTypeVO",cardTypeVO);
                                startActivity(intent);
                            }catch (Exception e){
                                e.printStackTrace();
                                ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                            }
                        }),cardTypeVO.getCardTypeId());
                    }else {
                        Toast.makeText(this, "Please pick the type of card you want to buy ", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }

    }


    private void getDmrcCardTypes(Context context , VolleyResponse volleyResponse){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.getCardTypeList();

            CardTypeVO cardTypeVO=new CardTypeVO();

            ServiceTypeVO serviceTypeVO = new ServiceTypeVO();
            serviceTypeVO.setServiceTypeId(ApplicationConstant.Dmrc);

            StatusVO statusVO = new StatusVO();
            statusVO.setStatusId(StatusVO.ACTIVE);

            cardTypeVO.setServiceTypeVO(serviceTypeVO);
            cardTypeVO.setStatusVO(statusVO);
            Gson gson =new Gson();
            String json = gson.toJson(cardTypeVO);
            params.put("volley", json);
            Log.w("getDmrcCardTypes",json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    BaseVO baseVO = gson.fromJson(response.toString(), BaseVO.class);

                    if(baseVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) baseVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        //pd.dismiss();
                        Utility.showSingleButtonDialog(context,baseVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(baseVO);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }

    private void getDmrcCardListByCardType(Context context , VolleyResponse volleyResponse ,Integer cardTypeId) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.getDmrcCustomerList();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));
            customerVO.setAnonymousInteger(cardTypeId);
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);

            System.out.println(json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    DMRC_Customer_CardVO dmrc_customer_cardVO = gson.fromJson(response.toString(), DMRC_Customer_CardVO.class);

                    if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        //pd.dismiss();
                        Utility.showSingleButtonDialog(context,dmrc_customer_cardVO.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(dmrc_customer_cardVO);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
            //   Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }
    }
}
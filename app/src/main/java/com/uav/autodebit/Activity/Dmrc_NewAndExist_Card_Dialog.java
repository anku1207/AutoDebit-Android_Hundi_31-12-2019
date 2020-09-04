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
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.ClickableViewPager;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.LocalCacheVO;
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


        gson = new Gson();

        LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);
        if(!localCacheVO.isDmrcExistingCard()){
            getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                finish();
                Session.set_Data_Sharedprefence(this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());
                Intent intent =new Intent(this,Dmrc_Card_Request.class);
                intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                intent.putExtra("isdisable",false);
                intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                startActivity(intent);
            }),true);
        }else {
            getDmrcCardTypes(Dmrc_NewAndExist_Card_Dialog.this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                try {
                    CustomerVO customerVO = (CustomerVO) success;

                    if(customerVO.getShowDialog()){
                        main_layout.setVisibility(View.VISIBLE);
                        proceed.setText(customerVO.getAnonymousString1());
                        dialog_title.setText(customerVO.getDialogTitle());
                        JSONArray jsonArray = new JSONArray(customerVO.getAnonymousString());

                        for(int i=0;i<jsonArray.length();i++){
                            JSONObject object = jsonArray.getJSONObject(i);
                            RadioButton rdbtn = new RadioButton(this);
                            rdbtn.setId(object.getInt("id"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                rdbtn.setText(Html.fromHtml(object.getString("message"), Html.FROM_HTML_MODE_COMPACT));
                            } else {
                                rdbtn.setText(Html.fromHtml(object.getString("message")));
                            }
                            rdbtn.setChecked(false);
                            rdbtn.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));
                            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.setMargins(15, 15, 15, 15);
                            rdbtn.setLayoutParams(params);
                            rdbtn.setPadding(10,0,0,0);
                            rdbtn.setGravity(Gravity.TOP);
                            radiogroup.addView(rdbtn);
                        }
                        ((RadioButton)radiogroup.getChildAt(0)).setChecked(true);

                    }else {
                      if(customerVO.getActionname().equalsIgnoreCase("Autope")){
                          getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                              DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                              finish();
                              Session.set_Data_Sharedprefence(this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());
                              Intent intent =new Intent(this,Dmrc_Card_Request.class);
                              intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                              intent.putExtra("isdisable",false);
                              intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                              startActivity(intent);
                          }),true);
                      }else {
                          getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                              DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                              finish();
                              Session.set_Data_Sharedprefence(this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());
                              Intent intent =new Intent(this,AddOldDmrcCardAutoPe.class);
                              intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                              intent.putExtra("isdisable",false);
                              intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                              startActivity(intent);
                          }),false);
                      }
                    }



                }catch ( Exception e){
                    ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                }


            }));
        }




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
                    proceed.startAnimation(animation);
                    switch (radiogroup.getCheckedRadioButtonId()){
                        case 1:
                        case 2:
                            getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                                DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                                finish();
                                Session.set_Data_Sharedprefence(this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());
                                Intent intent =new Intent(this,Dmrc_Card_Request.class);
                                intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                                intent.putExtra("isdisable",false);
                                intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                                startActivity(intent);
                            }),true);
                            break;
                        case 3:
                            getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                                DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                                finish();
                                Session.set_Data_Sharedprefence(this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());
                                Intent intent =new Intent(this,AddOldDmrcCardAutoPe.class);
                                intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                                intent.putExtra("isdisable",false);
                                intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                                startActivity(intent);
                            }),false);
                            break;
                        case -1:
                            Toast.makeText(this, "Please pick the type of card you want to buy ", Toast.LENGTH_SHORT).show();
                            break;
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
            ConnectionVO connectionVO = MetroBO.dmrcCardTypes();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    CustomerVO customerVOResp = gson.fromJson(response.toString(), CustomerVO.class);

                    if(customerVOResp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) customerVOResp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        //pd.dismiss();
                        Utility.showSingleButtonDialog(context,customerVOResp.getDialogTitle(),sb.toString(),false);
                    }else {
                        volleyResponse.onSuccess(customerVOResp);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }

    private void getDmrcCardListByCardType(Context context , VolleyResponse volleyResponse ,boolean cardListType) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.getDmrcCustomerList();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));
            customerVO.setEventIs(cardListType);
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
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
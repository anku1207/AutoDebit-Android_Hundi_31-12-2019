package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Dmrc_NewAndExist_Card_Dialog extends Base_Activity implements View.OnClickListener {
    Button newcard,existcard;
    TextView textview;
    Gson gson;
    LinearLayout main_layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmrc__new_and_exist__card__dialog);


        main_layout=findViewById(R.id.main_layout);
        newcard=findViewById(R.id.newcard);
        existcard=findViewById(R.id.existcard);
        textview=findViewById(R.id.textview);

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
            main_layout.setVisibility(View.VISIBLE);
        }
       //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

        getWindow().setLayout((int)(width*.9),(int)(height*.7));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);

        newcard.setOnClickListener(this);
        existcard.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageviewclickeffect);
        switch (v.getId()){
            case R.id.newcard:
                newcard.startAnimation(animation);
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
            case R.id.existcard:
                existcard.startAnimation(animation);
                getDmrcCardListByCardType(Dmrc_NewAndExist_Card_Dialog.this , new VolleyResponse((VolleyResponse.OnSuccess)(newCardSuccess)->{
                    DMRC_Customer_CardVO dmrc_customer_cardVO = (DMRC_Customer_CardVO) newCardSuccess;
                    finish();
                    Intent intent =new Intent(this,AddOldDmrcCardAutoPe.class);
                    intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                    intent.putExtra("isdisable",false);
                    intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                    startActivity(intent);
                }),false);
                break;
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
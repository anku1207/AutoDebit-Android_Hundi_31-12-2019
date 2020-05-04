package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.UberBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.CustomPagerAdapter;
import com.uav.autodebit.adpater.Uber_PagerAdapter;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncServiceGetList;
import com.uav.autodebit.util.BackgroundAsyncServiceGetListInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.TransientCustomerVO;
import com.uav.autodebit.vo.UberVO;
import com.uav.autodebit.vo.UberVoucherVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Uber extends Base_Activity implements View.OnClickListener {
    TextView addVoucher;
    ImageView back_activity_button;

    LinearLayout main,addcardlistlayout;
    ViewPager viewPager;
    TabLayout tabLayout;
    UAVProgressDialog pd;
    Gson gson;

    LinearLayout customerDetailLayout;

    boolean isAddVoucherBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber);
        getSupportActionBar().hide();


        addVoucher=findViewById(R.id.addVoucher);
        back_activity_button=findViewById(R.id.back_activity_button);
        main=findViewById(R.id.main);
        addcardlistlayout=findViewById(R.id.addcardlistlayout);
        customerDetailLayout=findViewById(R.id.customerDetailLayout);

        gson=new Gson();

        pd=new UAVProgressDialog(this);
        tabLayout =findViewById(R.id.indicator);

        addVoucher.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        String customerDetails  = getIntent().getStringExtra("customerDetails");

        List<UberVoucherVO> uberVoucherVOS = (ArrayList<UberVoucherVO>) gson.fromJson(getIntent().getStringExtra("voucherList"), new TypeToken<ArrayList<UberVoucherVO>>() { }.getType());
        isAddVoucherBtn=getIntent().getBooleanExtra("showAddVoucherButton",false);
        if(isAddVoucherBtn){
            addVoucher.setVisibility(View.VISIBLE);
            addVoucher.setPaintFlags(addVoucher.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }else{
            addVoucher.setVisibility(View.GONE);
        }
        addVoucherList(uberVoucherVOS);
        ShowCustomerDetail(customerDetails);

    }

    public void addVoucherList( List<UberVoucherVO> uberVoucherVOS ){
        viewPager=Utility.getViewPager(Uber.this);
        //viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        getdata(uberVoucherVOS);
    }

    public void ShowCustomerDetail(String customerDetails){
        try {

            JSONArray jsonArray = new JSONArray(customerDetails);

            Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
            for(int i=0 ;i<jsonArray.length();i++){
                JSONObject jsonObject =jsonArray.getJSONObject(i);

                LinearLayout et = new LinearLayout(new ContextThemeWrapper(this,R.style.confirmation_dialog_layout));

                et.setPadding(Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10),Utility.getPixelsFromDPs(this,10));

                TextView text = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_filed));
                text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                text.setText(jsonObject.getString("key"));
                text.setMaxLines(1);
                text.setEllipsize(TextUtils.TruncateAt.END);
                text.setTypeface(typeface);
                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                TextView value = new TextView(new ContextThemeWrapper(this, R.style.confirmation_dialog_value));
                value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                value.setText(jsonObject.getString("value"));
                value.setTypeface(typeface);
                value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                et.addView(text);
                et.addView(value);
                customerDetailLayout.addView(et);
            }

        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }






    public void getdata(List<UberVoucherVO> uberVoucherVOS ){
        BackgroundAsyncServiceGetList backgroundAsyncServiceGetList =new BackgroundAsyncServiceGetList(pd, false, new BackgroundAsyncServiceGetListInterface.BackgroundServiceInterface() {
            @Override
            public List doInBackGround(BackgroundAsyncServiceGetListInterface backgroundAsyncServiceGetListInterface) {

                ArrayList<UberVoucherVO> uberVoucherVOArrayList=new ArrayList<>();
                for(UberVoucherVO uberVoucherVO :uberVoucherVOS ){
                    UberVoucherVO voucherVO =new UberVoucherVO();

                    voucherVO.setVoucherNo(uberVoucherVO.getVoucherNo());
                    voucherVO.setIssueAt(uberVoucherVO.getIssueAt());
                    voucherVO.setVoucherExprieDate(uberVoucherVO.getVoucherExprieDate());
                    uberVoucherVOArrayList.add(voucherVO);

                }
                return backgroundAsyncServiceGetListInterface.doInBackGround.doInBackGround(uberVoucherVOArrayList);
            }
            @Override
            public void doPostExecute(List list) {
                Uber_PagerAdapter uber_pagerAdapter =new Uber_PagerAdapter(list,Uber.this);
                viewPager.setAdapter(uber_pagerAdapter);
                viewPager.setPadding(0,0,0,0);
                tabLayout.setupWithViewPager(viewPager, false);
                Utility.disable_Tab(tabLayout);
                addcardlistlayout.addView(viewPager);

                View current = getCurrentFocus();
                if (current != null) current.clearFocus();
            }
        });
        backgroundAsyncServiceGetList.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addVoucher:
                UberApiCall.getUberVoucher(this,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                    UberVoucherVO getUberVoucherResp = (UberVoucherVO) s;
                    try {
                        String uri =getUberVoucherResp.getVoucherLinke();
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(uri));
                        startActivity(intent);
                        getCard();
                     } catch (Exception e) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.ubercab")));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=com.ubercab")));
                        }finally {
                            getCard();
                        }
                    }
                }));
                break;
            case R.id.back_activity_button :
                finish();
                break;
        }
    }


    public void getCard(){
        UberApiCall.CheckUberVaucherByCustomerId(this,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            UberVoucherVO uberVoucherVO = (UberVoucherVO) success;
            if(!uberVoucherVO.isEventIs()){
                addcardlistlayout.removeAllViews();
                List<UberVoucherVO> uberVoucherVOS = (ArrayList<UberVoucherVO>) gson.fromJson(uberVoucherVO.getAnonymousString(), new TypeToken<ArrayList<UberVoucherVO>>() { }.getType());
                addVoucherList(uberVoucherVOS);
            }
        }));
    }




}

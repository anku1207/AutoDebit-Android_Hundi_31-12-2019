package com.uav.autodebit.Activity;

import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.DMRC_List_Adpater;
import com.uav.autodebit.adpater.ImageSliderAdapter;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DMRC_Cards_List extends Base_Activity implements View.OnClickListener {

    RecyclerView recyclerView;
    ArrayList<DMRC_Customer_CardVO> listforcard=new ArrayList<>();

    DMRC_Customer_CardVO dmrc_customer_cardVO;

    TextView addoncard;
    ImageView back_activity_button;
    List<ServiceTypeVO> serviceTypeVOS;
    ViewPager viewPager;

    List<String> imageUrls;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmrc__cards__list);
        getSupportActionBar().hide();
        Gson gson = new Gson();

        LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);

        /* Banner code started here*/
        viewPager=(ViewPager) findViewById(R.id.viewPager);
        TabLayout bannerIndicator=(TabLayout) findViewById(R.id.indicator);

        imageUrls =gson.fromJson(localCacheVO.getDmrc(), new TypeToken<List<String>>(){}.getType());

        Log.w("banners",imageUrls.toString());

        viewPager.setAdapter(new ImageSliderAdapter(this, imageUrls ));
        /*viewPager.setClipToPadding(false);
        // set padding manually, the more you set the padding the more you see of prev & next page
        viewPager.setPadding(200, 0, 200, 0);
        // sets a margin b/w individual pages to ensure that there is a gap b/w them
        viewPager.setPageMargin(20);*/

        bannerIndicator.setupWithViewPager(viewPager, true);

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);

       //listforcard= (ArrayList<DMRC_Customer_CardVO>) Utility.fromJson(getIntent().getStringExtra("dmrccard"), new TypeToken<ArrayList<DMRC_Customer_CardVO>>() { }.getType());

        dmrc_customer_cardVO=gson.fromJson(getIntent().getStringExtra("dmrccard"), DMRC_Customer_CardVO.class);
        listforcard= (ArrayList<DMRC_Customer_CardVO>) dmrc_customer_cardVO.getDmrcCustomerList();

        addoncard=findViewById(R.id.addoncard);
        back_activity_button=findViewById(R.id.back_activity_button);

        addoncard.setText(dmrc_customer_cardVO.getActionname());

        addoncard.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(DMRC_Cards_List.this, LinearLayoutManager.HORIZONTAL, false));

        DMRC_List_Adpater dmrc_list_adpater=new DMRC_List_Adpater(DMRC_Cards_List.this,getdata() ,R.layout.design_dmrc_card_list);
        recyclerView.setAdapter(dmrc_list_adpater);

    }

    /*Banner slider*/
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < imageUrls.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }
    public ArrayList<DMRC_Customer_CardVO> getdata(){
        ArrayList<DMRC_Customer_CardVO> dmrc_customer_cardVOS=new ArrayList<>();
        for(DMRC_Customer_CardVO dmrc_customer_cardVOS1 :listforcard ){
            DMRC_Customer_CardVO dmrc_customer_cardVO =new DMRC_Customer_CardVO();

            dmrc_customer_cardVO.setCustomerName(dmrc_customer_cardVOS1.getCustomerName());
            dmrc_customer_cardVO.setCardNo(dmrc_customer_cardVOS1.getCardNo());
            DmrcCardStatusVO dmrcCardStatusVO =new DmrcCardStatusVO();
            dmrcCardStatusVO.setStatusName(dmrc_customer_cardVOS1.getDmrccardStaus().getStatusName());
            dmrc_customer_cardVO.setIssueDate(dmrc_customer_cardVOS1.getIssueDate());

            dmrc_customer_cardVO.setDmrccardStaus(dmrcCardStatusVO);
            dmrc_customer_cardVO.setImage(dmrc_customer_cardVOS1.getImage());

            dmrc_customer_cardVOS.add(dmrc_customer_cardVO);
        }
        return dmrc_customer_cardVOS;
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addoncard:
                if(dmrc_customer_cardVO.isEventIs()){
                    startActivity(new Intent(DMRC_Cards_List.this,Dmrc_Card_Request.class).putExtra("isdisable",false));
                    finish();
                    break;
                }else{
                    Utility.showSingleButtonDialog(DMRC_Cards_List.this,"Alert",dmrc_customer_cardVO.getActionname(),false);
                    break;
                }
            case R.id.back_activity_button:
                finish();
                break;
        }
    }
}

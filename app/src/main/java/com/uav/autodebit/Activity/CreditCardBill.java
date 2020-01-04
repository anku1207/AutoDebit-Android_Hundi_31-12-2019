package com.uav.autodebit.Activity;

import android.animation.ArgbEvaluator;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.uav.autodebit.R;
import com.uav.autodebit.adpater.CustomPagerAdapter;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;

import java.util.ArrayList;
import java.util.List;

public class CreditCardBill extends AppCompatActivity {

    ViewPager viewPager;
    CustomPagerAdapter adapter;

    Integer[] color=null;
    ArgbEvaluator argbEvaluator =new ArgbEvaluator();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_bill);



       List<DMRC_Customer_CardVO> dmrc_customer_cardVOS =new ArrayList<>();

        DMRC_Customer_CardVO dmrc_customer_cardVO =new DMRC_Customer_CardVO();
        dmrc_customer_cardVO.setCustomerName("sfsdsdf");
        dmrc_customer_cardVOS.add(dmrc_customer_cardVO);
        dmrc_customer_cardVO =new DMRC_Customer_CardVO();
        dmrc_customer_cardVO.setCustomerName("sfsdsdf");
        dmrc_customer_cardVOS.add(dmrc_customer_cardVO);



        dmrc_customer_cardVO.setCustomerName("sfsdsdf");
        dmrc_customer_cardVOS.add(dmrc_customer_cardVO);


        dmrc_customer_cardVO.setCustomerName("sfsdsdf");
        dmrc_customer_cardVOS.add(dmrc_customer_cardVO);


        adapter=new CustomPagerAdapter(dmrc_customer_cardVOS,CreditCardBill.this);
        viewPager=findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);
        viewPager.setPadding(130,0,130,0);







    }


}

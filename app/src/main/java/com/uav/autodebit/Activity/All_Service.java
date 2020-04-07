package com.uav.autodebit.Activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.SingleImageRecyclerViewAdapter;
import com.uav.autodebit.adpater.UitilityAdapter;
import com.uav.autodebit.androidFragment.Profile;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class All_Service extends Base_Activity {

    RecyclerView service,utilityservie;

    Context context;
    BottomNavigationView navigation;
    ImageView back_activity_button;
    UAVProgressDialog pd ;

    @Override
    protected void onStop() {
        super.onStop();
        if(pd.isShowing()) pd.dismiss();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all__service);
        context=All_Service.this;
        getSupportActionBar().hide();

        pd=new UAVProgressDialog(this);

        service=findViewById(R.id.service);
        utilityservie=findViewById(R.id.utilityservie);
        back_activity_button=findViewById(R.id.back_activity_button);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Gson gson =new Gson();

        LocalCacheVO localCacheVO = gson.fromJson( Session.getSessionByKey(All_Service.this, Session.LOCAL_CACHE), LocalCacheVO.class);
        List<ServiceTypeVO> utilityServices = localCacheVO.getUtilityBills();
        List<ServiceTypeVO> serviceautope = localCacheVO.getSerives();




        navigation = findViewById(R.id.navigation);
        navigation.getMenu().getItem(0).setCheckable(false);

        BottomNavigationView  navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);




        service.setHasFixedSize(false);
        service.setLayoutManager(new GridLayoutManager(context, 2));
        service.setNestedScrollingEnabled(true);
        service.addItemDecoration(new DividerItemDecorator(2,2,false));
        SingleImageRecyclerViewAdapter servicelist=new SingleImageRecyclerViewAdapter(context,serviceautope ,R.layout.activity_singleimage);
        service.setAdapter(servicelist);



        List<ServiceTypeVO> addservice =new ArrayList<>();
        int[] my_array = {15};
        Arrays.sort(my_array);
        for(ServiceTypeVO utility:utilityServices){

            int index = Arrays.binarySearch(my_array,utility.getServiceTypeId() );
            if(index==-1){
                addservice.add(utility);
            }
        }


        utilityservie.setHasFixedSize(true);
        utilityservie.setLayoutManager(new GridLayoutManager(context, 3));
        utilityservie.setNestedScrollingEnabled(true);
        utilityservie.addItemDecoration(new DividerItemDecorator(3,2,false));
        UitilityAdapter utility=new UitilityAdapter(context,addservice ,R.layout.two_tailes,pd);
        utilityservie.setAdapter(utility);
    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    Intent intent = new Intent(getApplicationContext(), Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    break;
                case R.id.bottom_profile:
                    startActivity(new Intent(getApplicationContext(), Profile_Activity.class));
                    finish();
                    break;
                case R.id.bottom_history:
                    Toast.makeText(All_Service.this, "bottom_history", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.bottom_help:
                    Toast.makeText(All_Service.this, "bottom_help", Toast.LENGTH_SHORT).show();
                    break;
            }
            return true;
        }
    };

}

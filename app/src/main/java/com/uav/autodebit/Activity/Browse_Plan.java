package com.uav.autodebit.Activity;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.BrowsePlanAdapter;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenPlanVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Browse_Plan extends Base_Activity {



     static JSONArray planarray;
     static JSONObject tariff;

    /**
     * The {@link androidx.core.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link androidx.core.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse__plan);

        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.


        ImageView back_activity_button1=findViewById(R.id.back_activity_button1);
        back_activity_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);


        Gson gson = new Gson();
        OxigenPlanVO oxigenPlanVO=gson.fromJson( Session.getSessionByKey(this, Session.CACHE_BROWSE_DATA), OxigenPlanVO.class);


        try {
            JSONObject object=new JSONObject(oxigenPlanVO.getPlan());

            planarray= new JSONArray(object.getString("DenominationCategory"));
            tariff=new JSONObject(oxigenPlanVO.getTariff());

            for(int i=0;i<planarray.length();i++){
                    JSONObject object1=planarray.getJSONObject(i);
                    tabLayout.addTab(tabLayout.newTab().setText(object1.getString("CategoryName")));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));



    }




    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */


        JSONObject tabplan=null;
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public  PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_browse__plan, container, false);

            RecyclerView recyclerView=rootView.findViewById(R.id.recyclerview);
            recyclerView.setHasFixedSize(true);
            recyclerView.setNestedScrollingEnabled(true);
            LinearLayoutManager verticalLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(verticalLayoutManager);


            try {
                tabplan=planarray.getJSONObject(getArguments().getInt(ARG_SECTION_NUMBER));
                List<DataAdapterVO> addservice =new ArrayList<>();
                JSONObject object1=new JSONObject(tariff.getString(tabplan.getString("CategoryId")));
                JSONArray array=new JSONArray(object1.getString("Plans"));
                for(int i=0;i<array.length();i++){
                    DataAdapterVO dataAdapterVO=new DataAdapterVO();
                    JSONObject object =array.getJSONObject(i);
                    dataAdapterVO.setText2(object.getString("description"));
                    dataAdapterVO.setText(object.getString("Talktime"));
                    dataAdapterVO.setMop(object.getString("Amount"));
                    dataAdapterVO.setValidity(object.getString("Validity"));
                    addservice.add(dataAdapterVO);
                }

                BrowsePlanAdapter utility=new BrowsePlanAdapter(getContext(),addservice ,R.layout.browse_plan_design);
                recyclerView.setAdapter(utility);

                recyclerView.notify();






            } catch (Exception e) {
                e.printStackTrace();
            }




            return  rootView;
        }


    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
           // return PlaceholderFragment.newInstance(position + 1);


            PlaceholderFragment placeholderFragment=new PlaceholderFragment();

            return placeholderFragment.newInstance(position);

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return planarray.length();
        }
    }
}

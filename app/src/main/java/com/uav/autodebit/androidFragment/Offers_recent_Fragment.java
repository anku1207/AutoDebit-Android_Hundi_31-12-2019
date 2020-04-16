package com.uav.autodebit.androidFragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.Activity.Mobile_Prepaid_Recharge_Service;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.BrowsePlanAdapter;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenPlanVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Offers_recent_Fragment {


    public static  void createOfferAndRecentLayout(Context context){
        List<String> CategoryName= Arrays.asList("Offers", "Recent", "Help");
        ViewPager mViewPager;

        TabLayout tabLayout =  ((Activity) context).findViewById(R.id.tabs);
        try {
            for (String s : CategoryName) {
                tabLayout.addTab(tabLayout.newTab().setText(s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager)  ((Activity) context).findViewById(R.id.container);
        mViewPager.setAdapter(new Offers_recent_Fragment.SectionsPagerAdapter(((FragmentActivity) context).getSupportFragmentManager(),CategoryName));
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));
    }



    public static class PlaceholderFragment1 extends Fragment {

        JSONObject tabplan=null;
        private static final String ARG_SECTION_NUMBER = "section_number";
        List CategoryName;


        public PlaceholderFragment1() {
        }


        public PlaceholderFragment1(List CategoryName) {
            this.CategoryName=CategoryName;
        }
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public PlaceholderFragment1 newInstance(int sectionNumber) {
            PlaceholderFragment1 fragment = new PlaceholderFragment1();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            args.putString("tablist",CategoryName.toString());
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            TextView textView =new TextView(getContext());

            List<String> objectList =  (ArrayList<String>) new Gson().fromJson(getArguments().getString("tablist"), new TypeToken<ArrayList<String>>() { }.getType());
            textView.setText(objectList.get(getArguments().getInt(ARG_SECTION_NUMBER)));

            textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(View.TEXT_ALIGNMENT_CENTER);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


            return  textView;
        }


    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {
       List CategoryName;

        public SectionsPagerAdapter(FragmentManager fm, List CategoryName) {
            super(fm);
            this.CategoryName=CategoryName;

        }

        @Override
        public Fragment getItem(int position) {
            PlaceholderFragment1 placeholderFragment=new PlaceholderFragment1(CategoryName);
            return placeholderFragment.newInstance(position);
        }

        @Override
        public int getCount() {
            return CategoryName.size();
        }
    }


}

package com.uav.autodebit.androidFragment;



import android.app.Dialog;
import android.content.Context;
import android.content.Intent;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.uav.autodebit.Activity.AdditionalService;
import com.uav.autodebit.Activity.All_Service;
import com.uav.autodebit.Activity.Credit_Score_Report;
import com.uav.autodebit.Activity.DMRC_Cards_List;
import com.uav.autodebit.Activity.DividerItemDecorator;
import com.uav.autodebit.Activity.Dmrc_Card_Request;
import com.uav.autodebit.Activity.Enach_Mandate;
import com.uav.autodebit.Activity.Hyd_Metro;
import com.uav.autodebit.Activity.IRCTC;
import com.uav.autodebit.Activity.Mum_Metro;
import com.uav.autodebit.Activity.PanVerification;
import com.uav.autodebit.Activity.SI_First_Data;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.BannerAdapter;
import com.uav.autodebit.adpater.UitilityAdapter;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home_Menu extends Fragment {

    ViewPager viewPager;
    TabLayout bannerIndicator;

    List<BannerVO> banners;
    Integer level=null;

    ImageView allutilityservice;

    Context cntxt;
    UAVProgressDialog pd;
    ServiceTypeVO selectedService ;


    public Home_Menu() {
        // Required empty public constructor

    }

    @Override
    public void onStop() {
        super.onStop();
        if(pd.isShowing()) pd.dismiss();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_home__menu, container, false);

    }




    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Gson gson = new Gson();


        cntxt = getContext();
        pd = new UAVProgressDialog(cntxt);


        LocalCacheVO  localCacheVO = gson.fromJson( Session.getSessionByKey(getContext(), Session.LOCAL_CACHE), LocalCacheVO.class);
        RecyclerView recyclerView=view.findViewById(R.id.recyclerview);
        allutilityservice=view.findViewById(R.id.allutilityservice);




        /* Banner code started here*/
        viewPager=(ViewPager) view.findViewById(R.id.viewPager);
        bannerIndicator=(TabLayout) view.findViewById(R.id.indicator);

        banners = localCacheVO.getBanners();
//        banners.add(new BannerVO("https://java.sogeti.nl/JavaBlog/wp-content/uploads/2009/04/android_icon_256.png"));
        //  banners.add(new BannerVO("gauge"));

        viewPager.setAdapter(new BannerAdapter(getContext(), banners ));
        bannerIndicator.setupWithViewPager(viewPager, true);

       /* Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);
*/



        recyclerView.setHasFixedSize(true);


        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        recyclerView.setNestedScrollingEnabled(true);



        recyclerView.addItemDecoration(new DividerItemDecorator(4,2,false));

        //servicesrecy.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
       //creating recyclerview adapter
        List<ServiceTypeVO> utilityServices = localCacheVO.getUtilityBills();
        List<ServiceTypeVO> addservice =new ArrayList<>();




        String utilityService =localCacheVO.getUitiyServiceHomePage();
        String[] utilServiceArr =  utilityService.split(",");

        for(ServiceTypeVO utility:utilityServices){
            for(int c=0; c<utilServiceArr.length; c++ ){
                if( utilServiceArr[c].equals( utility.getServiceTypeId().toString()) ){
                    addservice.add(utility);
                }
            }
        }
        UitilityAdapter utility=new UitilityAdapter(getActivity(),addservice ,R.layout.two_tailes, pd);
        recyclerView.setAdapter(utility);


        /* Banner code ended here*/

        /*Services code started here*/
       /* int[] mImgIds=new int[] { R.drawable.irctc, R.drawable.dmrc, R.drawable.hydmetro,R.drawable.mummetro };
        String[] mTitles= new String[]{"IRCTC" , "Del Metro", "Hyd Metro" , "Mum Metro"};
        setHorizontalScrollView(mImgIds,mTitles, view,R.id.id_servicegallery ,R.layout.services_gallery);*/

        List<ServiceTypeVO> mImgIds=new ArrayList<>();
        mImgIds=localCacheVO.getSerives();
        setHorizontalScrollView(mImgIds, view,R.id.id_servicegallery ,R.layout.services_gallery);



        allutilityservice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), All_Service.class));
            }
        });


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }



    /*Banner slider*/
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (viewPager.getCurrentItem() < banners.size() - 1) {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                    } else {
                        viewPager.setCurrentItem(0);
                    }
                }
            });
        }
    }


    /*set horizontal scroll view layout elementes*/
    private void setHorizontalScrollView(final List<ServiceTypeVO> dataList, final View view, final int layout, final int activity ){
        LinearLayout mGallery = (LinearLayout) view.findViewById(layout);
        LayoutInflater mInflater = LayoutInflater.from(getContext());

        level= Session.getCustomerLevel(getContext());
        for (final ServiceTypeVO serviceTypeVO: dataList){

            View galView = mInflater.inflate( activity ,  mGallery, false);
            final LinearLayout activitylayout=galView.findViewById(R.id.layout_servicesgallery);
            activitylayout.setTag(serviceTypeVO.getServiceTypeId());
            ImageView img = (ImageView) galView.findViewById(R.id.id_index_gallery_item_image);
            ImageView activeservice=galView.findViewById(R.id.serviceactive);
            img.setImageDrawable(Utility.GetImage(getActivity(),serviceTypeVO.getAppIcon()));


            if(serviceTypeVO.getAdopted()==1){
                activeservice.setVisibility(View.VISIBLE);
               /* DrawableCompat.setTint(
                        activeservice.getBackground(),
                        ContextCompat.getColor(getContext(), R.color.serviceactive)
                );*/
            }else {
                activeservice.setVisibility(View.GONE);
            }

            TextView txt = (TextView) galView.findViewById(R.id.id_index_gallery_item_text);
            txt.setText(serviceTypeVO.getTitle());

            mGallery.addView(galView);

            activitylayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                        activitylayout.setEnabled(false);

                       // LoadingService loadingService = new LoadingService(activitylayout.getTag().toString(),activitylayout );
                        //loadingService.execute();

                    BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                        @Override
                        public void doInBackGround() {
                            Gson gson = new Gson();
                            LocalCacheVO  localCacheVO = gson.fromJson( Session.getSessionByKey(getContext(), Session.LOCAL_CACHE), LocalCacheVO.class);
                            List<ServiceTypeVO> serviceTypeVOS = localCacheVO.getSerives();

                            level= Session.getCustomerLevel(getContext());


                            for(ServiceTypeVO serviceTypeVO :  serviceTypeVOS){

                                Log.w("re",serviceTypeVO.getServiceTypeId()+"==="+ activitylayout.getTag().toString());
                                if(serviceTypeVO.getServiceTypeId()== Integer.parseInt(activitylayout.getTag().toString() ) ){
                                    selectedService=serviceTypeVO;
                                    break;
                                }
                            }

                        }

                        @Override
                        public void doPostExecute() {

                            activitylayout.setEnabled(true);

                            if(selectedService !=null && selectedService.getAdopted()!=null && selectedService.getAdopted()==1){

                                if(level< selectedService.getLevel().getLevelId()){
                                    pd.dismiss();
                                    level++;
                                    switch (level){
                                        case 2:
                                            startActivity(new Intent(getActivity(), PanVerification.class));

                                            break;
                                        case 3:
                                            startActivity(new Intent(getActivity(), Credit_Score_Report.class));
                                            break;
                                        case 4:

                                            Utility.confirmationDialog(new DialogInterface() {
                                                @Override
                                                public void confirm(Dialog dialog) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getActivity(), Enach_Mandate.class));
                                                }
                                                @Override
                                                public void modify(Dialog dialog) {
                                                    dialog.dismiss();
                                                }
                                            }, getContext(), null, "Bank Verification","Mandate");


                                            break;
                                        case 5:
                                            Utility.confirmationDialog(new DialogInterface() {
                                                @Override
                                                public void confirm(Dialog dialog) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getActivity(), Enach_Mandate.class));
                                                }
                                                @Override
                                                public void modify(Dialog dialog) {
                                                    dialog.dismiss();
                                                }
                                            }, getContext(), null, "Bank Verification","Mandate");
                                            break;
                                        case 6:

                                            Utility.confirmationDialog(new DialogInterface() {
                                                @Override
                                                public void confirm(Dialog dialog) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getActivity(), SI_First_Data.class));
                                                }
                                                @Override
                                                public void modify(Dialog dialog) {
                                                    dialog.dismiss();
                                                }
                                            }, getContext(), null, "SI Verification","Mandate");

                                            break;
                                    }
                                }else{
                                    startService(activitylayout.getTag().toString());
                                }

                            }else {
                                getActivity().startActivityForResult(new Intent(getActivity(), AdditionalService.class),200);
                            }
                        }
                    });
                    backgroundAsyncService.execute();
                }
            });
        }
    }

    public  void startService(String id){
        switch (id){
            case "1":
                startActivity(new Intent(getContext(), IRCTC.class));
                break;
            case "2":
                dmrcCardRequest();
                break;
            case  "3":
                startActivity(new Intent(getContext(), Hyd_Metro.class));
                break;
            case  "4":
                startActivity(new Intent(getContext(), Mum_Metro.class));
                break;
        }
    }
    public void dmrcCardRequest(){

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.getDmrcCustomerList();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(getContext())));
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(getContext(),connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    DMRC_Customer_CardVO dmrc_customer_cardVO = gson.fromJson(response.toString(), DMRC_Customer_CardVO.class);

                    if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        pd.dismiss();
                        // Utility.alertDialog(PanVerification.this,"Alert",sb.toString(),"Ok");
                        Utility.showSingleButtonDialog(getContext(),dmrc_customer_cardVO.getDialogTitle(),sb.toString(),false);
                    }else {

                        Session.set_Data_Sharedprefence(getContext(),Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());

                        if(dmrc_customer_cardVO.getDmrcCustomerList().size()>0){


                            Intent intent =new Intent(getActivity(),DMRC_Cards_List.class);
                            intent.putExtra("dmrccard",Utility.toJson(dmrc_customer_cardVO.getDmrcCustomerList()));
                            startActivity(intent);
                        }else {
                            Intent intent =new Intent(getContext(),Dmrc_Card_Request.class);
                            intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                            intent.putExtra("isdisable",true);
                            startActivity(intent);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

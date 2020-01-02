package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.ServiceClick;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.BannerAdapter;
import com.uav.autodebit.adpater.UitilityAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthStatusVO;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Home extends AppCompatActivity
        implements View.OnClickListener {

    SharedPreferences sharedPreferences;
    TextView mainwallet,profile,linked,all,wallet,regular,
            delinkservice,faqs,reportbug,condition,setting,logoutbtn;
    ImageView closemenuactivity;
    DrawerLayout drawer;
    BottomNavigationView navigation;
    public static ProgressDialog dialog;
    UAVProgressDialog uavProgressDialog ;
    /////19-10-2019

    ViewPager viewPager;
    TabLayout bannerIndicator;

    List<BannerVO> banners;
    Integer level=null;

    ImageView  allutilityservice;

    Context cntxt;
    UAVProgressDialog pd ;
    ServiceTypeVO selectedService ;

    RecyclerView recyclerView;
    public static String clickServiceId;
    ServiceTypeVO selectServiceType;

    JSONObject activity_json=new JSONObject();
    JSONObject json_Service =new JSONObject();



    @Override
    protected void onRestart() {
        super.onRestart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        String customername=Session.getCustomerName(Home.this);
        toolbar.setTitle(Utility.capitalize(customername));
        setSupportActionBar(toolbar);


        dialog=new ProgressDialog(Home.this);
        pd = new UAVProgressDialog(this);

        selectedService=null;
        level=null;

        try {

            activity_json.put("1","IRCTC");
            activity_json.put("2","Dmrc_Card_Request");
            activity_json.put("3","Hyd_Metro");
            activity_json.put("4","Mum_Metro");
            activity_json.put("5","Mobile_Prepaid_Recharge_Service");
            activity_json.put("13","DTH_Recharge_Service");
            activity_json.put("7","LandlineBill");
            activity_json.put("8","Broadband");
            activity_json.put("9","Mum_Metro");
            activity_json.put("14","Mobile_Postpaid");
            activity_json.put("12","Water");
            activity_json.put("11","Gas_Bill");
            activity_json.put("10","Electricity_Bill");
            activity_json.put("15","All_Service");
            activity_json.put("17","D2H");
            activity_json.put("6","PNG");

            json_Service.put("L_2","PanVerification");
            json_Service.put("L_3","Credit_Score_Report");
            json_Service.put("L_4","Enach_Mandate");
            json_Service.put("L_5","Enach_Mandate");
            json_Service.put("L_6","SI_First_Data");



       //check customer level and start activity
        Gson gson =new Gson();
        CustomerVO customerVO = gson.fromJson(Session.getSessionByKey(Home.this,Session.CACHE_CUSTOMER), CustomerVO.class);
        if(customerVO.getLevel().getLevelId()<=2){
            if(customerVO.getLevel().getLevelId()==1){
                startActivity(new Intent(Home.this,PanVerification.class));
                finish();
                return;
            }else if(customerVO.getLevel().getLevelId()==2){
                startActivity(new Intent(Home.this,Credit_Score_Report.class));
                finish();
                return;
            }
        }

        // override local cache
             overrideLocalCache(customerVO);
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }

        mainwallet=findViewById(R.id.mainwallet);
        profile=findViewById(R.id.profile);
        linked=findViewById(R.id.linked);
        all=findViewById(R.id.all);


        //19-10-2019
        recyclerView=findViewById(R.id.recyclerview);
        allutilityservice=findViewById(R.id.allutilityservice);


        logoutbtn=findViewById(R.id.logoutbtn);


        wallet=findViewById(R.id.wallet);
        regular=findViewById(R.id.regular);
        delinkservice=findViewById(R.id.delinkservice);
        faqs=findViewById(R.id.faqs);

        reportbug=findViewById(R.id.reportbug);
        condition=findViewById(R.id.condition);
        setting=findViewById(R.id.setting);
        closemenuactivity=findViewById(R.id.closemenuactivity);

        mainwallet.setOnClickListener(this);
        profile.setOnClickListener(this);
        linked.setOnClickListener(this);
        all.setOnClickListener(this);
        wallet.setOnClickListener(this);
        regular.setOnClickListener(this);
        delinkservice.setOnClickListener(this);
        faqs.setOnClickListener(this);
        reportbug.setOnClickListener(this);
        condition.setOnClickListener(this);
        setting.setOnClickListener(this);
        closemenuactivity.setOnClickListener(this);



        logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove(Session.CACHE_CUSTOMER);
                editor.remove(Session.CACHE_USER_LOGINID);
                editor.commit();
                startActivity(new Intent(Home.this,Login.class));
                finish();
            }
        });


        sharedPreferences = getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


       // loadFragment(new Home_Menu());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //show png image in bottom menu bar
       // navigation.setItemIconTintList(null);
       // navigation.setSelectedItemId(R.id.bottom_home);

        //19-10-2018
        loadDateInRecyclerView();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.addItemDecoration(new DividerItemDecorator(4,2,false));

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new SliderTimer(), 4000, 6000);


    }


    public void loadDateInRecyclerView(){


        //19-10-2018
        Gson gson = new Gson();
        LocalCacheVO  localCacheVO = gson.fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);

        /* Banner code started here*/
        viewPager=(ViewPager) findViewById(R.id.viewPager);
        bannerIndicator=(TabLayout) findViewById(R.id.indicator);

        banners = localCacheVO.getBanners();

        viewPager.setAdapter(new BannerAdapter(this, banners));
        bannerIndicator.setupWithViewPager(viewPager, true);
        Utility.disable_Tab(bannerIndicator);



        //view pager change position change text view msg
/*

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
              TextView textView=  (TextView)findViewById(R.id.textview);

              TranslateAnimation animObj= new TranslateAnimation(0,100, 0, 0);
              animObj.setDuration(500);
              textView.startAnimation(animObj);

              textView.setText(position+"sdfsdfsdfdsfsdfdsfs");

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
*/





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
        UitilityAdapter utility=new UitilityAdapter(this,addservice ,R.layout.two_tailes, pd);
        recyclerView.setAdapter(utility);

        List<ServiceTypeVO> mImgIds=new ArrayList<>();
        mImgIds=localCacheVO.getSerives();
        setHorizontalScrollView(mImgIds,R.id.id_servicegallery ,R.layout.services_gallery);
    }




    /*Banner slider*/
    private class SliderTimer extends TimerTask {

        @Override
        public void run() {
            Home.this.runOnUiThread(new Runnable() {
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
    private void setHorizontalScrollView(final List<ServiceTypeVO> dataList,  final int layout, final int activity ){
        LinearLayout mGallery = (LinearLayout) findViewById(layout);
        mGallery.removeAllViewsInLayout();
        level= Session.getCustomerLevel(Home.this);
        for (final ServiceTypeVO serviceTypeVO: dataList){

            //View galView = mInflater.inflate( activity ,  mGallery, false);

            View galView = getLayoutInflater().inflate(activity, null);

            final LinearLayout activitylayout=galView.findViewById(R.id.layout_servicesgallery);
            activitylayout.setTag(serviceTypeVO.getServiceTypeId());
            ImageView img = (ImageView) galView.findViewById(R.id.id_index_gallery_item_image);
            final ImageView activeservice=galView.findViewById(R.id.serviceactive);
            img.setImageDrawable(Utility.GetImage(this,serviceTypeVO.getAppIcon()));

            if(serviceTypeVO.getAdopted()==1 && serviceTypeVO.getServiceAdopteBMA()!=null){
                activeservice.setVisibility(View.VISIBLE);
            }else {
                activeservice.setVisibility(View.GONE);
            }


            TextView txt = (TextView) galView.findViewById(R.id.id_index_gallery_item_text);
            txt.setText(serviceTypeVO.getTitle());

            mGallery.addView(galView);

            activitylayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(serviceTypeVO.getAdopted()!=1){
                        Utility.showSingleButtonDialogconfirmation(Home.this, new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            Utility.enableDisableView(view,false);
                            startUserClickService(activitylayout.getTag().toString(),view);
                            ok.dismiss();
                        }),"Alert",serviceTypeVO.getMessage());
                    }else{
                        Utility.enableDisableView(view,false);
                        startUserClickService(activitylayout.getTag().toString(),view);

                    }
                }
            });
        }
    }

    public void addMoreService(ConfirmationDialogInterface confirmationDialogInterface){
        String[] buttons = {"No","Yes"};
        Utility.confirmationDialog(new DialogInterface() {
            @Override
            public void confirm(Dialog dialog) {
                dialog.dismiss();
                confirmationDialogInterface.onOk(dialog);
            }
            @Override
            public void modify(Dialog dialog) {
                dialog.dismiss();
                confirmationDialogInterface.onCancel(dialog);
            }
        },Home.this,null,"Would you like add more service ?","",buttons);
    }



    public void dmrcCardRequest(){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.getDmrcCustomerList();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Home.this)));
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(Home.this,connectionVO, new VolleyResponseListener() {
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
                        Utility.showSingleButtonDialog(Home.this,"Error !",sb.toString(),false);
                    }else {

                        Session.set_Data_Sharedprefence(Home.this,Session.CACHE_DMRC_MIN_CARD_CHARGE,dmrc_customer_cardVO.getAnonymousString());


                        //if card is empty open add dmrc card activity open
                        if(dmrc_customer_cardVO.getDmrcCustomerList().size()<0){
                            Intent intent =new Intent(Home.this,DMRC_Cards_List.class);

                            intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                            startActivity(intent);
                        }else {
                            Intent intent =new Intent(Home.this,Dmrc_Card_Request.class);
                            intent.putExtra("onetimecharges",dmrc_customer_cardVO.getAnonymousString());
                            intent.putExtra("isdisable",true);
                            startActivity(intent);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }
    }


    public Double getHighestAmtForService(ArrayList<Integer> ids){
        LocalCacheVO  localCacheVO = new Gson().fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);
        List<ServiceTypeVO> serviceTypeVOS =localCacheVO.getSerives();
        List<ServiceTypeVO> utilityService =localCacheVO.getUtilityBills();

        List<ServiceTypeVO> newList = new ArrayList<ServiceTypeVO>();
        newList.addAll(serviceTypeVOS);
        newList.addAll(utilityService);

        Double serviceamt=0.00;
        for(int i=0;i<ids.size();i++){
            for(ServiceTypeVO serviceTypeVO :newList){
                if(serviceTypeVO.getServiceTypeId()==ids.get(i)){
                    if(serviceTypeVO.getMandateAmount()>=serviceamt){
                        serviceamt=serviceTypeVO.getMandateAmount();
                        break;
                    }
                }
            }
        }
        return serviceamt;
    }

    public ServiceTypeVO getServiceForServiceId(String id){
            LocalCacheVO  localCacheVO = new Gson().fromJson( Session.getSessionByKey(this, Session.LOCAL_CACHE), LocalCacheVO.class);
            List<ServiceTypeVO> serviceTypeVOS =localCacheVO.getSerives();
            for(ServiceTypeVO serviceTypeVO :serviceTypeVOS){
                if(serviceTypeVO.getServiceTypeId()==Integer.parseInt(id)){
                       return serviceTypeVO;
                }
            }
            List<ServiceTypeVO> utilityService =localCacheVO.getUtilityBills();
            for(ServiceTypeVO serviceTypeVO :utilityService){
                if(serviceTypeVO.getServiceTypeId()==Integer.parseInt(id)){
                        return serviceTypeVO;
                }
            }
            return null;
    }






    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK) {
            if (requestCode == ApplicationConstant.REQ_ENACH_MANDATE) {
                loadDateInRecyclerView();
                ArrayList<Integer> enachActServiceIds = data.getIntegerArrayListExtra("selectservice");
                int [] showDialogServiceIds={1,2,3,4};
                boolean showDialogValidate=false;
                int i=0;
                do{
                    if(enachActServiceIds.contains(showDialogServiceIds[i])) showDialogValidate = true;
                    i++;
                }while(i<showDialogServiceIds.length);{
                    if(showDialogValidate){
                        Utility.showSingleButtonDialogconfirmation(this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                            startUserClickService(clickServiceId,null);
                        }),"Alert",data.getStringExtra("msg"));
                    }else {
                        startUserClickService(clickServiceId,null);
                    }
                }
            } else if (requestCode == ApplicationConstant.REQ_ALLSERVICE) {
                startUserClickService(clickServiceId,null);
            } else if (requestCode == ApplicationConstant.REQ_AdditionalService_Add_More) {
                ArrayList<Integer> integers = data.getIntegerArrayListExtra("selectservice");

               // double highestAmt = getHighestAmtForService(integers);
                Intent enachMandate = new Intent(Home.this, Enach_Mandate.class);
                enachMandate.putExtra("forresutl", true);
                enachMandate.putExtra("selectservice", integers);
                startActivityForResult(enachMandate, ApplicationConstant.REQ_ENACH_MANDATE);

            }
        }
    }

   public void startUserClickService(String serviceId,View view){
        try {
            clickServiceId=serviceId;
            selectServiceType=new ServiceTypeVO();
            BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                @Override
                public void doInBackGround() {
                    selectServiceType=getServiceForServiceId(serviceId);
                }
                @Override
                public void doPostExecute() {

                    serviceClick(Integer.parseInt(serviceId),new ServiceClick((ServiceClick.OnSuccess)(s)->{
                        try {
                            startActivityServiceClick(Integer.parseInt(serviceId),Class.forName(getPackageName()+".Activity."+activity_json.get(serviceId)),s,selectServiceType.getMandateAmount(),view);
                        } catch (Exception e) {
                            if(view!=null)Utility.enableDisableView(view,true);
                            e.printStackTrace();
                            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                        }
                    },(ServiceClick.OnError)(e)->{
                    }),view);
                }
            });
            backgroundAsyncService.execute();
        }catch (Exception e){
            if(view!=null)Utility.enableDisableView(view,true);
            e.printStackTrace();
            Log.e("error_home",e.getMessage());
            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }
   }




    // add bank for service
    public  void serviceClick(int serviceId , ServiceClick serviceClick,View view){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.addBankForService();

        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
        customerVO.setServiceId(serviceId);
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);

        Log.w("addBankForService",params.toString());

        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
                if(view!=null)Utility.enableDisableView(view,true);
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);

                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Home.this,"Alert",sb.toString(),false);
                    if(view!=null)Utility.enableDisableView(view,true);

                }else {
                    serviceClick.onSuccess(customerVO);
                }
            }
        });
    }


    public void startActivityServiceClick(int serviceId,Class classname,Object o,double mandateamt,View view){
        try {
            CustomerVO customerVO =(CustomerVO) o;


            if(view!=null)Utility.enableDisableView(view,true);


            Toast.makeText(Home.this, ""+customerVO.getStatusCode(), Toast.LENGTH_SHORT).show();

            if(!customerVO.getStatusCode().equals("200")){
                if(customerVO.getStatusCode().equals("ap105") || customerVO.getStatusCode().equals("ap107") ||customerVO.getStatusCode().equals("ap102")){
                    String[] buttons = {"OK"};
                    Utility.showSingleButtonDialogconfirmation(this, new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        ok.dismiss();
                        startActivityForResult(new Intent(Home.this,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))),ApplicationConstant.REQ_ENACH_MANDATE);
                    }),"Alert",customerVO.getErrorMsgs().get(0),buttons);
                }else if(customerVO.getStatusCode().equals("ap106") || customerVO.getStatusCode().equals("ap103") || customerVO.getStatusCode().equals("ap108")){
                    String[] buttons = {"New Mandate","Choose Bank"};
                    Utility.showDoubleButtonDialogConfirmation( new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            try {

                                JSONArray arryjson=new JSONArray(customerVO.getAnonymousString());

                                ArrayList<CustomerAuthServiceVO> customerAuthServiceArry=new ArrayList<>();
                                for(int i=0;i<arryjson.length();i++){
                                    JSONObject jsonObject =arryjson.getJSONObject(i);
                                    CustomerAuthServiceVO customerAuthServiceVO =new CustomerAuthServiceVO();
                                    customerAuthServiceVO.setBankName(jsonObject.getString("bankName"));
                                    customerAuthServiceVO.setProviderTokenId(jsonObject.getString("mandateId"));
                                    customerAuthServiceVO.setCustomerAuthId(jsonObject.getInt("id"));
                                    customerAuthServiceVO.setAnonymousString(jsonObject.getString("status"));
                                    customerAuthServiceArry.add(customerAuthServiceVO);
                                }


                                CustomerAuthServiceVO customerAuthServiceVO =new CustomerAuthServiceVO();
                                customerAuthServiceVO.setBankName(null);
                                customerAuthServiceVO.setProviderTokenId("Add New Mandate");
                                customerAuthServiceVO.setCustomerAuthId(0);
                                customerAuthServiceVO.setAnonymousString(null);
                                customerAuthServiceArry.add(customerAuthServiceVO);


                                Utility.alertselectdialog(Home.this,"Choose from existing Bank",customerAuthServiceArry,new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess)(s)->{
                                    if(!s.equals("0")){
                                        Log.w("Home_value",s);
                                        setBankForService(serviceId,Integer.parseInt(Session.getCustomerId(Home.this)),Integer.parseInt(s));
                                    }else {
                                        startActivityForResult(new Intent(Home.this,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))),ApplicationConstant.REQ_ENACH_MANDATE);
                                    }
                                }));
                            }catch (Exception e){
                                e.printStackTrace();
                                Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                            }
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                            startActivityForResult(new Intent(Home.this,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))),ApplicationConstant.REQ_ENACH_MANDATE);
                        }
                    },this,customerVO.getErrorMsgs().get(0),"Alert",buttons);

                }else {
                    Utility.showSingleButtonDialogconfirmation(Home.this, new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        try {
                            ok.dismiss();
                            if(customerVO.getStatusCode().equals("L_2")){
                                startActivity(new Intent(Home.this,Class.forName(getPackageName()+".Activity."+json_Service.getString("L_2"))));
                                finish();
                            }else if(customerVO.getStatusCode().equals("L_3")){
                                startActivity(new Intent(Home.this,Class.forName(getPackageName()+".Activity."+json_Service.getString("L_3"))));
                                finish();
                            }else if(customerVO.getStatusCode().equals("L_4")){
                                startActivityForResult(new Intent(Home.this,Class.forName(getPackageName()+".Activity."+json_Service.getString("L_5"))).putExtra("onactivityresult",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))),ApplicationConstant.REQ_ENACH_MANDATE);
                            }else if(customerVO.getStatusCode().equals("L_5")){
                                startActivityForResult(new Intent(Home.this,Class.forName(getPackageName()+".Activity."+json_Service.getString("L_5"))).putExtra("onactivityresult",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))),ApplicationConstant.REQ_ENACH_MANDATE);
                            }else if(customerVO.getStatusCode().equals("L_6")){
                                try {
                                    startActivityForResult(new Intent(Home.this,Class.forName(getPackageName()+".Activity."+json_Service.getString("L_6"))).putExtra("onactivityresult",true),ApplicationConstant.REQ_AdditionalService_Add_More);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                                }
                            }else  if(customerVO.getStatusCode().equals("ap101")){
                                startActivityForResult(new Intent(Home.this,AdditionalService.class), ApplicationConstant.REQ_ALLSERVICE);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                        }
                    }),"Alert",customerVO.getErrorMsgs().get(0));
                }
            }else {
                    //set session customer or local cache
                    String json = new Gson().toJson(customerVO);
                    Session.set_Data_Sharedprefence(Home.this,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(Home.this, Session.LOCAL_CACHE,customerVO.getLocalCache());
                    loadDateInRecyclerView();

                    if(serviceId==2){
                        dmrcCardRequest();
                    }else {
                        Intent intent;
                        intent =new Intent(Home.this, classname);
                        intent.putExtra("serviceid",serviceId+"");
                        startActivity(intent);
                    }
            }
        }catch (Exception e){
            if(view!=null)Utility.enableDisableView(view,true);
            e.printStackTrace();
            Log.e("error_serviceClick",e.getMessage());
            Utility.exceptionAlertDialog(Home.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

        }
    }

    //bank list select bank for service
    public void setBankForService( int serviceId,int customerId,int bankId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = ServiceBO.setBankForService();

        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(customerId);
        customerVO.setServiceId(serviceId);
        customerVO.setAnonymousInteger(bankId);
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("setBankForService",params.toString());
        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);

                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Home.this,"Alert",sb.toString(),false);
                }else {
                    startUserClickService(serviceId+"",null);

                    //set session customer or local cache
                    String json = new Gson().toJson(customerVO);
                    Session.set_Data_Sharedprefence(Home.this,Session.CACHE_CUSTOMER,json);
                    Session.set_Data_Sharedprefence(Home.this, Session.LOCAL_CACHE,customerVO.getLocalCache());
                    loadDateInRecyclerView();
                }
            }
        });
    }





    public void overrideLocalCache(CustomerVO customerVO){
        Session.set_Data_Sharedprefence(Home.this, Session.LOCAL_CACHE,customerVO.getLocalCache());
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            String[] buttons = {"Cancel","Ok"};

            Utility.confirmationDialog(new DialogInterface() {
                public String leftButton="Cancel";
                @Override
                public void confirm(Dialog dialog) {
                    dialog.dismiss();
                    // ActivityCompat.finishAffinity( Home.this);
                    finishAffinity();
                }
                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                }
            },Home.this,null,"Do you want to exit","App Exit", buttons);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    //fragment=new Home_Menu();
                    break;
                case R.id.bottom_profile:
                   // fragment=new Profile();
                    startActivity(new Intent(Home.this,Profile_Activity.class));
                    finish();

                    break;
                case R.id.bottom_history:
                    Toast.makeText(Home.this, "bottom_history", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.bottom_help:
                    startActivity(new Intent(Home.this,Help.class));
                    break;
            }
            return loadFragment(fragment);
        }
    };

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.mainwallet:
                Toast.makeText(this, "mainwallet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.profile:
                startActivity(new Intent(this,Profile_Activity.class));
                break;

            case R.id.linked  :
                Toast.makeText(this, "linked", Toast.LENGTH_SHORT).show();
                break;
            case R.id.regular  :
                Toast.makeText(this, "regular", Toast.LENGTH_SHORT).show();
                break;
            case R.id.all  :
                startActivity(new Intent(this,All_Service.class));
                break;
            case R.id.wallet  :
                Toast.makeText(this, "wallet", Toast.LENGTH_SHORT).show();
                break;
            case R.id.delinkservice  :
                Toast.makeText(this, "delinkservice", Toast.LENGTH_SHORT).show();
                break;


            case R.id.faqs  :
                Toast.makeText(this, "faqs", Toast.LENGTH_SHORT).show();
                break;


            case R.id.reportbug  :
                Toast.makeText(this, "reportbug", Toast.LENGTH_SHORT).show();
                break;

            case R.id.condition  :
                Toast.makeText(this, "condition", Toast.LENGTH_SHORT).show();
                break;
            case R.id.setting  :
                Toast.makeText(this, "setting", Toast.LENGTH_SHORT).show();
                break;
            case R.id.closemenuactivity:
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }
}

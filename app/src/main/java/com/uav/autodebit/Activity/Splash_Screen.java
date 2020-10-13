package com.uav.autodebit.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.BannerBO;

import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BitmapInterface;
import com.uav.autodebit.util.DiskLruImageCache;
import com.uav.autodebit.util.DownloadTask;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.util.Utils_Cache;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.BitmapVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.branch.referral.Branch;
import io.branch.referral.BranchError;


public class Splash_Screen extends AppCompatActivity implements BitmapInterface {
    ProgressBar progressBar;

    List<BannerVO> slBannerVos= new ArrayList<>();
    List<BannerVO> plBannerVos =new ArrayList<>();
    List<BitmapVO> imageVos =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__screen);

        ImageView imageView = (ImageView) findViewById( R.id.appstarticon);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

      /*  if(!Utility.isNetworkAvailable(Splash_Screen.this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(Splash_Screen.this);
            builder.setMessage("Sorry, no Internet Connectivity detected. Please reconnect and try again ")
                    .setTitle("No Internet Connection!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        }else {

            FirebaseMessaging.getInstance().subscribeToTopic("global");
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                Log.w( "getInstanceId failed", task.getException());
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_TOKENID,token);
                            Log.w("token",token);
                        }
                    });

            startDownloadCache();
        }*/
    }


    @Override
    public void onStart() {
        super.onStart();
        if(!Utility.isNetworkAvailable(Splash_Screen.this)){
            AlertDialog.Builder builder = new AlertDialog.Builder(Splash_Screen.this);
            builder.setMessage("Sorry, no Internet Connectivity detected. Please reconnect and try again ")
                    .setTitle("No Internet Connection!")
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    });
            AlertDialog alert = builder.create();
            alert.setCanceledOnTouchOutside(false);
            alert.setCancelable(false);
            alert.show();
        }else {
            JSONObject branchSessionJson = (JSONObject) Branch.getInstance().getLatestReferringParams();
            if(branchSessionJson.length()==0){
                Log.w("branchSession","Create Branch Session");
                Branch.sessionBuilder(this).withCallback(branchReferralInitListener).withData(getIntent() != null ? getIntent().getData() : null).init();
            }else {
                Log.w("branchSession","Existing Branch Session");
                startDownloadCache();
            }
        }
    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        // if activity is in foreground (or in backstack but partially visible) launching the same
        // activity will skip onStart, handle this case with reInitSession
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
    }
    private Branch.BranchReferralInitListener branchReferralInitListener = new Branch.BranchReferralInitListener() {
        @Override
        public void onInitFinished(JSONObject linkProperties, BranchError error) {
            // do stuff with deep link data (nav to page, display content, etc)

            if(error==null && linkProperties!=null ){

                Log.w("linkProperties",linkProperties.toString());

                FirebaseMessaging.getInstance().subscribeToTopic("global");
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    Log.w( "getInstanceId failed", task.getException());
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_TOKENID,token);
                                Log.w("token",token);
                            }
                        });

                startDownloadCache();
            }else {
                Log.i("BRANCH SDK", error.getMessage());
                startDownloadCache();
            }
        }
    };



    private  void dowork() throws InterruptedException {
        for(int i=0; i<100;i+=10){
            Thread.sleep(100);
            progressBar.setProgress(i);

        }
    }

    private  void startNextActivity(){

        Log.e("isuse",Session.getSessionByKey_BoolenValue(Splash_Screen.this,Session.CACHE_IS_NEW_USER).toString());
        loadHomeActivity();
    }



    private  void loadHomeActivity(){


        try {
            if(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION)!=null){
                Intent intent=new Intent(Splash_Screen.this,Login.class);
                intent.putExtra(ApplicationConstant.NOTIFICATION_ACTION,getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));
                startActivity(intent);
                finish();
            }else {
                Intent intent=new Intent(Splash_Screen.this,Login.class);
                startActivity(intent);
                finish();
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(Splash_Screen.this , Utility.getStackTrace(e));
        }
    }



    private void startDownloadCache(){
        try {
            VolleyUtils.makeJsonObjectRequest(this, BannerBO.getLocalCache(), new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                    finish();
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Gson gson = new Gson();
                    LocalCacheVO localCacheVO = gson.fromJson(response.toString(), LocalCacheVO.class);
                    slBannerVos=localCacheVO.getBanners();

                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.MOBILE_OPERATOR_LIST,localCacheVO.getMobileOperator());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.MOBILE_STATE_LIST,localCacheVO.getMobileStateRegion());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.DTH_OPERATOR_LIST,localCacheVO.getDthOperator());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_LANDLINE_OPERATOR,localCacheVO.getLandline());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_BROADBAND_OPERATOR,localCacheVO.getBoradBand());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_WATER_OPERATOR,localCacheVO.getWater());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_GAS_OPERATOR,localCacheVO.getGas());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_ELECTRICITY_OPERATOR,localCacheVO.getElectricity());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.LOCAL_CACHE,response.toString());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_POSTPAID_OPERATOR,localCacheVO.getPostPaid());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_PNG_OPERATOR,localCacheVO.getPng());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_INSURANCE_OPERATOR,localCacheVO.getInsuranceRenewal());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_LOAN_OPERATOR,localCacheVO.getLoanRepayment());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_FASTTAG_OPERATOR,localCacheVO.getFastTag());
                    Session.set_Data_Sharedprefence(Splash_Screen.this,Session.CACHE_CABLETV_OPERATOR,localCacheVO.getCableTv());




                    //download image on first time

                    //managecachedata();

                  /*  List<BitmapVO> bitmapVOs=new ArrayList<>();
                    for(int i=0 ; i<slBannerVos.size() ; i++){
                        BitmapVO bitmapVO =new BitmapVO();
                        bitmapVO.setURL(slBannerVos.get(i).getBannerImage());
                        bitmapVO.setFileName(Utils_Cache.BANNER_PREFIX+i);
                        bitmapVO.setImageView(null);
                        bitmapVO.setLocalCacheFolder( Utils_Cache.CACHE_FILEPATH_BANNER);
                        bitmapVO.setLocalCacheFolderSize(Utils_Cache.CACHE_FILE_SIZE);
                        bitmapVO.setImageFormat(Bitmap.CompressFormat.PNG);
                        bitmapVO.setImageQuality(100);
                        bitmapVOs.add(bitmapVO);
                    }
                    */
                    if(imageVos.size()>0){
                        new DownloadTask(  Splash_Screen.this, Splash_Screen.this ,imageVos);
                    }else {
                        startNextActivity();
                    }

                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }
    public void managecachedata(){

        new DiskLruImageCache(Splash_Screen.this, Utils_Cache.CACHE_FILEPATH_BANNER,Utils_Cache.CACHE_FILE_SIZE,null ,100);


        JsonArray bannerList = new JsonArray();
        if( !Session.check_Exists_key(Splash_Screen.this,Session.BANNER_LIST)){
            for (BannerVO bannerVO : slBannerVos){
                 setBitmap(bannerVO);
            }

        }else{
           List<BannerVO>  sessBannerVOs = (List<BannerVO>) new Gson().fromJson(Session.getSessionByKey(this,Session.BANNER_LIST),new TypeToken<List<BannerVO>>() {}.getType());
            for (BannerVO bannerVO : slBannerVos){
                    boolean isfound= false;
                    for(BannerVO sessBannerVO: sessBannerVOs){
                        if(bannerVO.getBannerId() == sessBannerVO.getBannerId()){
                            isfound=true;
                            if(!bannerVO.getUpdateAt().equals(sessBannerVO.getUpdateAt())){
                                setBitmap(bannerVO);
                            }else if( !DiskLruImageCache.containsKey(Utils_Cache.BANNER_PREFIX+sessBannerVO.getBannerId())) {
                                setBitmap(bannerVO);
                            }
                        }
                    }
                    if(!isfound){
                        setBitmap(bannerVO);

                    }
            }

        }

         bannerList = (JsonArray) new Gson().toJsonTree(slBannerVos,
                new TypeToken<List<BannerVO>>() {
                }.getType());
        Session.set_Data_Sharedprefence(this,Session.BANNER_LIST,bannerList.toString());
    }


    @Override
    public void downloadComplete(List<BitmapVO> bitmapVOs) {
            startNextActivity();
    }

    @Override
    public void error(String error) {

    }
    private void setBitmap(BannerVO bannerVO){
        BitmapVO bitmapVO =new BitmapVO();
        bitmapVO.setURL(bannerVO.getBannerImage());
        bitmapVO.setFileName(Utils_Cache.BANNER_PREFIX+bannerVO.getBannerId());
        bitmapVO.setImageView(null);
        bitmapVO.setLocalCacheFolder( Utils_Cache.CACHE_FILEPATH_BANNER);
        bitmapVO.setLocalCacheFolderSize(Utils_Cache.CACHE_FILE_SIZE);
        bitmapVO.setImageFormat(Bitmap.CompressFormat.PNG);
        bitmapVO.setImageQuality(100);
        imageVos.add(bitmapVO);
    }
}

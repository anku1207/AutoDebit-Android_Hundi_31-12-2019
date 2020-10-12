package com.uav.autodebit.util;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.uav.autodebit.Activity.AdditionalService;
import com.uav.autodebit.Activity.Credit_Score_Report;
import com.uav.autodebit.Activity.Enach_Mandate;
import com.uav.autodebit.Activity.Hyd_Metro;
import com.uav.autodebit.Activity.IRCTC;
import com.uav.autodebit.Activity.PanVerification;
import com.uav.autodebit.Activity.SI_First_Data;
import com.uav.autodebit.adpater.UitilityAdapter;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import java.util.List;

public class BackgroundAsyncService extends AsyncTask<Void, Void, Void> {

    UAVProgressDialog pd ;
    BackgroundServiceInterface backgroundServiceInterface;
    boolean isShowLoader;

    public BackgroundAsyncService(UAVProgressDialog pd , boolean isShowLoader,

                                  BackgroundServiceInterface backgroundServiceInterface){
       this.pd= pd;
       this.backgroundServiceInterface = backgroundServiceInterface;
       this.isShowLoader=isShowLoader;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

       if(isShowLoader) pd.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        // Do your request
           backgroundServiceInterface.doInBackGround();

        return null;

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        pd.dismiss();
        backgroundServiceInterface.doPostExecute();


    }

}

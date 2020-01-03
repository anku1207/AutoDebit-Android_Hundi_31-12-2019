package com.uav.autodebit.util;

import android.os.AsyncTask;

import com.uav.autodebit.override.UAVProgressDialog;

import java.util.List;

public class BackgroundAsyncServiceGetList extends AsyncTask<Void, Void, List> {
    UAVProgressDialog pd ;
    BackgroundAsyncServiceGetListInterface.BackgroundServiceInterface backgroundAsyncServiceGetListInterface;
    boolean isShowLoader;

    public BackgroundAsyncServiceGetList(UAVProgressDialog pd , boolean isShowLoader, BackgroundAsyncServiceGetListInterface.BackgroundServiceInterface backgroundAsyncServiceGetListInterface){
        this.pd= pd;
        this.backgroundAsyncServiceGetListInterface = backgroundAsyncServiceGetListInterface;
        this.isShowLoader=isShowLoader;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if(isShowLoader) pd.show();
    }
    @Override
    protected List doInBackground(Void... voids) {
       return backgroundAsyncServiceGetListInterface.doInBackGround(new BackgroundAsyncServiceGetListInterface((BackgroundAsyncServiceGetListInterface.DoInBackGround)(list)->{
                        return list;
        }));
    }
    @Override
    protected void onPostExecute(List tasks) {
        super.onPostExecute(tasks);
        backgroundAsyncServiceGetListInterface.doPostExecute(tasks);
    }
}

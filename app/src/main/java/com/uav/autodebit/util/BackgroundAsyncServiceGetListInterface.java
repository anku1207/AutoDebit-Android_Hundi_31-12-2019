package com.uav.autodebit.util;

import com.uav.autodebit.Interface.ServiceClick;

import java.util.List;

public class BackgroundAsyncServiceGetListInterface {

    public DoInBackGround doInBackGround;
    public DoPostExecute doPostExecute;

    public BackgroundAsyncServiceGetListInterface(DoInBackGround doInBackGround) {
        this.doInBackGround=doInBackGround;
    }

    public BackgroundAsyncServiceGetListInterface(DoInBackGround doInBackGround,DoPostExecute doPostExecute) {
        this.doInBackGround=doInBackGround;
        this.doPostExecute=doPostExecute;
    }

    public interface DoInBackGround {
        List doInBackGround(List s);
    }

    public interface DoPostExecute {
        void doPostExecute();
    }

    public interface BackgroundServiceInterface {
        public List doInBackGround(BackgroundAsyncServiceGetListInterface backgroundAsyncServiceGetListInterface);
        public void doPostExecute(List list);
    }
}

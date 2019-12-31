package com.uav.autodebit.volley;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleySingleton {

    private  static VolleySingleton mysingleton;
    private RequestQueue requestQueue;
    private static Context mCtx;

    VolleySingleton(Context context){
       mCtx = context;
       requestQueue=getRequestQueue();
   }
    private RequestQueue getRequestQueue() {
        if (requestQueue==null){
            requestQueue= Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return requestQueue;
    }


    public static synchronized VolleySingleton getInstance(Context context){
        if(mysingleton==null ){
            mysingleton=new VolleySingleton(context);

        }
        return mysingleton;
    }

    public<T> void addTorequestque(Request<T> request){

        requestQueue.add(request);

    }






}

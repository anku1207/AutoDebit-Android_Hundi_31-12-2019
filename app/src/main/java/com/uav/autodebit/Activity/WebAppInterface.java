package com.uav.autodebit.Activity;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class WebAppInterface {

    Context mContext;
    String data;

    WebAppInterface(Context ctx){
        this.mContext=ctx;
    }


    @JavascriptInterface
    public void sendData(String data) {
        //Get the string value to process
        this.data=data;

        Toast.makeText(mContext, ""+data, Toast.LENGTH_SHORT).show();
    }
}

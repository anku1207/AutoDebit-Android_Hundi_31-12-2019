package com.uav.autodebit.Activity;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;

import java.util.ArrayList;
import java.util.Date;

public class Base_Activity extends AppCompatActivity {
    final static String CONNECTIVITY_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
    IntentFilter intentFilter;
    CheckInternetReceiver receiver;
    AlertDialog alert;
    FirebaseAnalytics firebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        //GlobalApplication.dialog_List =  new ArrayList<>();

        Log.w("onCreate","onCreate");
        disableAutofill();

        intentFilter = new IntentFilter();
        intentFilter.addAction(CONNECTIVITY_ACTION);
        receiver = new CheckInternetReceiver();
        firebaseAnalytics =FirebaseAnalytics.getInstance(this);
        showInternetDialog();

    }


    private void showInternetDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(Base_Activity.this);
        builder.setMessage("Sorry, no Internet Connectivity detected. Please reconnect and try again ")
                .setTitle("No Internet Connection!")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                        Utility.dismissDialog(Base_Activity.this,alert);
                    }
                });
        alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.w("onStart","onStart");
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.w("onResume","onResume");
        registerReceiver(receiver, intentFilter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("onPause","onPause");
        unregisterReceiver(receiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.w("onStop","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        Log.w("onRestart","onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.w("onDestroy","onDestroy");
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }

    }

    private class CheckInternetReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String actionOfIntent = intent.getAction();
            boolean isConnected = Utility.isNetworkAvailable(Base_Activity.this);
            if(actionOfIntent.equals(CONNECTIVITY_ACTION)){
                if(isConnected){
                    if(!Base_Activity.this.isFinishing() && alert!=null && alert.isShowing()){
                        alert.dismiss();
                    }
                }else{
                    if(!Base_Activity.this.isFinishing() && alert!=null && !alert.isShowing()){
                        alert.show();
                    }

                }
            }
        }
    }

}

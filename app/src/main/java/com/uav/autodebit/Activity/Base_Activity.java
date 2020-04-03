package com.uav.autodebit.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;


import com.uav.autodebit.util.ExceptionHandler;

import java.util.Date;

public class Base_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        Log.w("onCreate","onCreate");
        disableAutofill();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.w("onPause","onPause");
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

}

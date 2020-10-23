package com.uav.autodebit.constant;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import io.branch.referral.Branch;

public class GlobalApplication extends Application {

    public static int notificationCount=0;
    public static List<Dialog> dialog_List =  new ArrayList<>();
    public static String updateMsg =" Kindly update the app now ";

    @Override
    public void onCreate() {
        super.onCreate();
        // Branch logging for debugging
        Branch.enableLogging();

        // Branch object initialization
        Branch.getAutoInstance(this);
    }

}

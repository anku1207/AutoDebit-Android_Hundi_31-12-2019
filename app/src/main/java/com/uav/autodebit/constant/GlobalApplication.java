package com.uav.autodebit.constant;

import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;

import java.util.ArrayList;
import java.util.List;

public class GlobalApplication extends Application {

    public static int notificationCount=0;
    public static List<Dialog> dialog_List =  new ArrayList<>();

}

package com.uav.autodebit.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.uav.autodebit.Activity.MainActivity;
import com.uav.autodebit.Activity.PanVerification;

import java.util.ArrayList;
import java.util.List;

public class PermissionHandler {

    public static String permissiontype;
    public static Context context1;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    static String[] mPermission = {
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.READ_SMS,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.READ_CONTACTS};


    public static void checkpermission(Context context) {
        context1=context;
        try {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (ActivityCompat.checkSelfPermission(context, mPermission[0])
                        != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[1])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[2])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[3])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[4])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[5])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[6])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[7])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[8])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[9])
                                != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(context, mPermission[10])
                                != PackageManager.PERMISSION_GRANTED) {
                                         ActivityCompat.requestPermissions((Activity) context, mPermission, 100);
                    // If any permission aboe not allowed by user, this condition will execute every tim, else your else part will work
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public static ArrayList<String> readSmsPermissionArrayList(Context context) {
        ArrayList<String> permissions=new ArrayList<>();
        permissions.add(Manifest.permission.SEND_SMS);
        permissions.add(Manifest.permission.RECEIVE_SMS);
        permissions.add(Manifest.permission.READ_SMS);
        return permissions;

    }

    public static ArrayList<String> fileDownloadAndReadPermissionArrayList(Context context) {
        ArrayList<String> permissions=new ArrayList<>();
        permissions.add(Manifest.permission.INTERNET);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        return permissions;

    }

    public static ArrayList<String> contactPermissionArrayList(Context context) {
        ArrayList<String> permissions=new ArrayList<>();
        permissions.add(Manifest.permission.READ_CONTACTS);
        return permissions;

    }

    public static ArrayList<String> imagePermissionArrayList(Context context) {
        ArrayList<String> permissions=new ArrayList<>();
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);
        return permissions;

    }
}

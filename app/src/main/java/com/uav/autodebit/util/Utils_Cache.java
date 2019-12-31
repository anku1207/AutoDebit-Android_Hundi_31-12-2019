package com.uav.autodebit.util;

import android.content.Context;
import android.os.Build;
import android.os.Environment;

import java.io.File;

public class Utils_Cache {

    public static String CACHE_FILEPATH_BANNER="BannerImage";
    public static Integer CACHE_FILE_SIZE = (1024*1024*1024);
    public static String BANNER_PREFIX="banner";

    public static final int IO_BUFFER_SIZE = 8 * 1024;

    private Utils_Cache() {};

    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

}
package com.uav.autodebit.Notification;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;


import com.uav.autodebit.R;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;


/**
 * Created by Ravi on 31/03/15.
 */
public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();

    public static  String CHANNEL_ID ="1234";
    public int count_notification=100;

    private Context mContext;

    public NotificationUtils(Context mContext) {
        Random rand = new Random();
        count_notification=rand.nextInt(100);
        this.mContext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent,String smallimgurl) {
        showNotificationMessage(title, message, timeStamp, intent, null,smallimgurl);
    }

    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl,String smallimgurl) {
        // Check for empty push message
        if (TextUtils.isEmpty(message))
            return;

        // notification icon
        final int icon = R.drawable.autodebitlogo;

        Bitmap smallicon=null;
        if (smallimgurl != null && smallimgurl.length() > 4 && Patterns.WEB_URL.matcher(smallimgurl).matches()) {
            smallicon = getBitmapFromURL(smallimgurl);
        }else {
            smallicon= BitmapFactory.decodeResource(mContext.getResources(),
                    R.drawable.autodebitlogo);
        }

        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final PendingIntent resultPendingIntent =PendingIntent.getActivity(mContext,0,intent,PendingIntent.FLAG_CANCEL_CURRENT
                );
        final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext,CHANNEL_ID);

        Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.hundi);


        if (!TextUtils.isEmpty(imageUrl)) {
            if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,smallicon);
                } else {
                    showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,smallicon);
                }
            }
        } else {
            showSmallNotification(mBuilder, icon, title, message, timeStamp, resultPendingIntent, alarmSound,smallicon);
            //playNotificationSound();
        }
    }

    private void showSmallNotification(NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, Bitmap smallicon) {
         NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = mBuilder
                   /* .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(message))*/
                    .setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(icon)
                    .setLargeIcon(smallicon)
                    .setContentText(message)
                    .build();
            getNotificationChannel(alarmSound,notificationManager);
            notificationManager.notify(count_notification, notification);
        }else {
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

            inboxStyle.addLine(message);

            Notification notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(inboxStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(icon)
                    .setLargeIcon(smallicon)
                    .setContentText(message)
                    .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                    .setDefaults(Notification.DEFAULT_LIGHTS )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setLights(Color.RED, 3000, 3000)
                    .build();


            notificationManager.notify(count_notification, notification);
       }


    }


    private void showBigNotification(Bitmap bitmap, NotificationCompat.Builder mBuilder, int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent, Uri alarmSound, Bitmap smallicon) {
        // Logic to turn on the screen

        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.bigPicture(bitmap);

            Notification notification = mBuilder
                    .setStyle(bigPictureStyle)
                    .setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(icon)
                    .setLargeIcon(smallicon)
                    .setContentText(message).build();
            getNotificationChannel(alarmSound,notificationManager);
            notificationManager.notify(count_notification, notification);
        }else {
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            bigPictureStyle.setBigContentTitle(title);
            bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
            bigPictureStyle.bigPicture(bitmap);
            Notification notification;
            notification = mBuilder.setSmallIcon(icon).setTicker(title).setWhen(0)
                    .setAutoCancel(true)
                    .setContentTitle(title)
                    .setContentIntent(resultPendingIntent)
                    .setSound(alarmSound)
                    .setStyle(bigPictureStyle)
                    .setWhen(getTimeMilliSec(timeStamp))
                    .setSmallIcon(icon)
                    /* .setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(), icon))*/
                    .setLargeIcon(smallicon)
                    .setContentText(message)
                    .setVibrate(new long[]{0, 500, 1000})
                    .setDefaults(Notification.DEFAULT_LIGHTS )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    .build();
            notificationManager.notify(count_notification, notification);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  NotificationChannel getNotificationChannel(Uri alarmSound, NotificationManager notificationManager){


        NotificationChannel mChannel=null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel(CHANNEL_ID, "HUNDI", NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GRAY);
            mChannel.setVibrationPattern(new long[]{0, 500, 1000});
            mChannel.enableVibration(true);
            mChannel.setDescription("DESCRIPTION");

            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            mChannel.setSound(alarmSound, attributes);
        }
        if (notificationManager != null) {
            notificationManager.createNotificationChannel( mChannel );
        }
        return mChannel;
    }

    /**
     * Downloading push notification image before displaying it in
     * the notification tray
     */
    public static Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Playing notification sound
    public void playNotificationSound() {
        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + mContext.getPackageName() + "/" + R.raw.hundi);
            Ringtone r = RingtoneManager.getRingtone(mContext, alarmSound);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }


}
package com.uav.autodebit.Notification;

import android.content.Context;
import android.content.Intent;

import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import com.google.gson.Gson;
import com.uav.autodebit.SQlLite.InsertDateOnSqlLite;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerNotificationVO;

import org.json.JSONObject;


/**
 * Created by Ravi Tamada on 08/08/16.
 * www.androidhive.info
 */
public class FCMService extends FirebaseMessagingService {

    private static final String TAG = FCMService.class.getSimpleName();

    private NotificationUtils notificationUtils;


    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.e("NEW_TOKEN",s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage == null)
            return;
        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.e(TAG, "Notification Body: " + remoteMessage.getNotification().getBody());
            Log.e(TAG, "Notification Title: " + remoteMessage.getNotification().getTitle());
            Log.e(TAG, "Notification Image: " + remoteMessage.getNotification().getImageUrl());

            handleNotification(remoteMessage,remoteMessage.getNotification().getBody());
        }

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                handleDataMessage(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }

    private void handleNotification(RemoteMessage remoteMessage,String message) {

           if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            }else{
                // If the app is in background, firebase itself handles the notification
            }
    }
    private void handleDataMessage(JSONObject data) {
        Log.e(TAG, "push json: " + data.toString());
        try {
            CustomerNotificationVO customerNotificationVO = new Gson().fromJson(data.toString(), CustomerNotificationVO.class);

            //save notification in Cache
            notificationManagement(customerNotificationVO);

            String title = customerNotificationVO.getTitle();
            String message = customerNotificationVO.getMessage();
            String imageUrl = customerNotificationVO.getBigImage();
            String timestamp = customerNotificationVO.getCreatedAt();
            String smallImageurl = customerNotificationVO.getServiceIcon();
            String activityname = customerNotificationVO.getActivityName() != null ? customerNotificationVO.getActivityName() : "Splash_Screen";
            //  JSONObject payload = data.getJSONObject("payload");
            String moveactivityjson = customerNotificationVO.getMoveActivity();

            Log.e(TAG, "title: " + title);
            Log.e(TAG, "message: " + message);
            Log.e(TAG, "imageUrl: " + imageUrl);
            Log.e(TAG, "timestamp: " + timestamp);

            //notification for background service
            /*if (!NotificationUtils.isAppIsInBackground(getApplicationContext())) {
                // app is in foreground, broadcast the push message
                Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
                pushNotification.putExtra("message", message);
                LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

                // play notification sound
                NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
                notificationUtils.playNotificationSound();
            } else {*/
            // app is in background, show the notification in notification tray
            Class<?> clazz = Class.forName(getApplicationContext().getPackageName() + ".Activity." + activityname);
            Intent resultIntent = new Intent(getApplicationContext(), clazz);
            resultIntent.putExtra("message", message);
            if (moveactivityjson != null) {
                resultIntent.putExtra(ApplicationConstant.NOTIFICATION_ACTION, moveactivityjson);
            }
            // check for image attachment
            if (TextUtils.isEmpty(imageUrl)) {
                Log.w("error", "run1" + imageUrl);
                showNotificationMessage(getApplicationContext(), title, message, timestamp, resultIntent, smallImageurl);
            } else {
                Log.w("error", "run2");
                // image is present, show notification with image
                showNotificationMessageWithBigImage(getApplicationContext(), title, message, timestamp, resultIntent, imageUrl, smallImageurl);
            }
            /*   }*/
        }catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
            ExceptionsNotification.ExceptionHandling(getApplicationContext() , Utility.getStackTrace(e));
        }
    }

    private void notificationManagement(CustomerNotificationVO customerNotificationVO) {
        try {
            if(customerNotificationVO.getStoreData()==1){
                GlobalApplication.notificationCount++;
                InsertDateOnSqlLite.insertNotification(getApplicationContext(),customerNotificationVO);
            }
        } catch (Exception e) {
            Log.w("error",e.getMessage());
            ExceptionsNotification.ExceptionHandling(getApplicationContext() , Utility.getStackTrace(e));
        }
    }
    /**
     * Showing notification with text only
     */
    private void showNotificationMessage(Context context, String title, String message, String timeStamp, Intent intent,String smallimgurl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent,smallimgurl);
    }
    /**
     * Showing notification with text and image
     */
    private void showNotificationMessageWithBigImage(Context context, String title, String message, String timeStamp, Intent intent, String imageUrl,String smallimgurl) {
        notificationUtils = new NotificationUtils(context);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(title, message, timeStamp, intent, imageUrl,smallimgurl);
    }
}
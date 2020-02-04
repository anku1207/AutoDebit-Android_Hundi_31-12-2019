package com.uav.autodebit.BroadCastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.SmsMessage;

public class MySMSBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent)
    {
        // Get Bundle object contained in the SMS intent passed in
        Bundle bundle = intent.getExtras();


        if (bundle != null)
        {
            // Get the SMS message
            Object[] pdus = (Object[]) bundle.get("pdus");

            for (int i=0; i<pdus.length; i++){
                SmsMessage smsm = SmsMessage.createFromPdu((byte[])pdus[i]);
                String sender = smsm.getDisplayOriginatingAddress();
                String msg = smsm.getMessageBody().toString();

                //Check here sender is yours
                if(!sender.contains("AUTOPE")) return;

                Intent smsIntent = new Intent("otp");
                smsIntent.putExtra("message",msg);

                LocalBroadcastManager.getInstance(context).sendBroadcast(smsIntent);



            }
        }
    }
}
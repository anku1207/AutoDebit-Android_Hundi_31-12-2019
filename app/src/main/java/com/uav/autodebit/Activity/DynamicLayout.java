package com.uav.autodebit.Activity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.DataAdapterVO;

public class DynamicLayout {

    public static LinearLayout billMinLayout(Context context, DataAdapterVO dataAdapterVO){
        LinearLayout parent_min_layout = new LinearLayout(context);
        parent_min_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent_min_layout.setOrientation(LinearLayout.VERTICAL);


        TextView textView = Utility.getTextView(context,"Important Information");
        textView.setLayoutParams(Utility.getLayoutparams(context,0,0,0,0));
        parent_min_layout.addView(textView);


        LinearLayout parent = new LinearLayout(context);
        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.HORIZONTAL);

        ImageView imageView = new ImageView(context);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(Utility.getPixelsFromDPs(context,15), Utility.getPixelsFromDPs(context,15), (float)0));
        imageView.setImageDrawable(context.getDrawable(R.drawable.iconcheck));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);;

        TextView text1 = new TextView(context);
        text1.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,(float)1));
        text1.setText("Minimum amount allowed is Rs "+dataAdapterVO.getMinTxnAmount().toString());
        text1.setLayoutParams(Utility.getLayoutparams(context,10,0,0,0));
        parent.addView(imageView);
        parent.addView(text1);
        parent_min_layout.addView(parent);
        return parent_min_layout;
    }


    static  void addContectIconEdittext(Context context, PermissionUtils permissionUtils, UAVEditText et){
        try {
            Drawable drawable = Utility.getDrawableResources(context, R.drawable.contacts);
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, context.getResources().getColor(R.color.appbar));

            et.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mobile,0 , R.drawable.contacts, 0);
            et.setDrawableClickListener(new DrawableClickListener() {
                @Override
                public void onClick(DrawablePosition target) {
                    switch (target) {
                        case RIGHT:
                            permissionUtils.check_permission(PermissionHandler.contactPermissionArrayList(context), Content_Message.CONTACT_PERMISSION, ApplicationConstant.REQ_READ_CONTACT_PERMISSION);
                            break;
                    }
                }
            });
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
    static String addMobileNumberInEdittext(Context context ,Intent data){
        String num = "";
        Uri contactData = data.getData();
        Cursor c = context.getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (Integer.valueOf(hasNumber) == 1) {
                Cursor numbers = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (numbers.moveToNext()) {
                    num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+","");
                }
                if (num.length()>10) {
                    num=num.substring(num.length() - 10);
                }
            }
        }
        return num;

    }


    static String addNumberInEdittext(Context context ,Intent data){
        String num = "";
        Uri contactData = data.getData();
        Cursor c = context.getContentResolver().query(contactData, null, null, null, null);
        if (c.moveToFirst()) {
            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
            String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
            if (Integer.valueOf(hasNumber) == 1) {
                Cursor numbers = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                while (numbers.moveToNext()) {
                    num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+","");
                }
            }
        }
        return num;

    }
}

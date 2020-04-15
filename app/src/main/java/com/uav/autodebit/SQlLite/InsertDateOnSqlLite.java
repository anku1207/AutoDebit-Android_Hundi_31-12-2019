package com.uav.autodebit.SQlLite;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import org.json.JSONObject;

public class InsertDateOnSqlLite {

    public static void insertNotification(Context context, JSONObject data){
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("Title",data.getString("title"));
            contentValues.put("Message",data.getString("message"));
            contentValues.put("ImageUrl",data.has("imageUrl")&& !data.getString("imageUrl").equals("")?data.getString("imageUrl"):null);
            contentValues.put("TimeStamp",data.getString("timestamp"));
            contentValues.put("SmallImage",data.has("smallImageUrl") && !data.getString("smallImageUrl").equals("")?data.getString("smallImageUrl"):null);
            contentValues.put("ActivityName",data.has("activityname")? data.getString("activityname"):"SplashScreen");


            DataBaseHelper.myDataBase.beginTransaction();
            DataBaseHelper.myDataBase.insert("notification", null,contentValues);
            DataBaseHelper.myDataBase.setTransactionSuccessful();
            DataBaseHelper.myDataBase.endTransaction();

            dataBaseHelper.close();
            }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
    }
}

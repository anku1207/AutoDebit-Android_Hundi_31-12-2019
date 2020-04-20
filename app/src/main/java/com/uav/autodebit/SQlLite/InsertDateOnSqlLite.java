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


    public static long insertOxigenTxnValue(Context context, JSONObject data){
        long result=-1;
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("CustomerId",data.getInt("customer_id"));
            contentValues.put("ServiceTypeId",data.getInt("service_id"));
            contentValues.put("Amount",data.getString("amount"));
            contentValues.put("OperatorName",data.getString("operator_name"));

            DataBaseHelper.myDataBase.beginTransaction();
            result=DataBaseHelper.myDataBase.insert("oxigentransaction", null,contentValues);
            DataBaseHelper.myDataBase.setTransactionSuccessful();
            DataBaseHelper.myDataBase.endTransaction();

            dataBaseHelper.close();
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    public static long insertOxigenfilter(Context context, JSONObject data){
        long result=-1;
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("OxigenTxnId",data.getInt("oxigenid"));

            DataBaseHelper.myDataBase.beginTransaction();
            result=DataBaseHelper.myDataBase.insert("oxigenbillerfilter", null,contentValues);
            DataBaseHelper.myDataBase.setTransactionSuccessful();
            DataBaseHelper.myDataBase.endTransaction();
            dataBaseHelper.close();
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return result;
    }

}

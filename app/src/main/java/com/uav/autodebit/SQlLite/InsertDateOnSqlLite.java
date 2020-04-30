package com.uav.autodebit.SQlLite;

import android.content.ContentValues;
import android.content.Context;
import android.widget.Toast;

import com.uav.autodebit.vo.CustomerNotificationVO;

import org.json.JSONObject;

public class InsertDateOnSqlLite {

    public static void insertNotification(Context context, CustomerNotificationVO customerNotificationVO){
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();

            ContentValues contentValues = new ContentValues();
            contentValues.put("Title",customerNotificationVO.getTitle());
            contentValues.put("Message",customerNotificationVO.getMessage());
            contentValues.put("ImageUrl",customerNotificationVO.getBigImage());
            contentValues.put("TimeStamp",customerNotificationVO.getCreatedAt());
            contentValues.put("SmallImage",customerNotificationVO.getServiceIcon());
            contentValues.put("ActivityName",customerNotificationVO.getActivityName()!=null?customerNotificationVO.getActivityName():"Splash_Screen");
            contentValues.put("MoveActivity",customerNotificationVO.getMoveActivity());

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

package com.uav.autodebit.SQlLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerNotificationVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetSqlLiteData {

    public  static List<CustomerNotificationVO> getNotification(Context context){
        List<CustomerNotificationVO> customerNotificationVOS= new ArrayList<>();
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();

            String sql= "Select * from  'notification' ";
            Cursor cursor= DataBaseHelper.myDataBase.rawQuery(sql, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    int id = cursor.getInt(cursor.getColumnIndex("ID"));
                    String title=cursor.getString(cursor.getColumnIndex("Title"));
                    String message=cursor.getString(cursor.getColumnIndex("Message"));
                    String imageUrl=cursor.getString(cursor.getColumnIndex("ImageUrl"));
                    String time=cursor.getString(cursor.getColumnIndex("TimeStamp"));
                    String smallimage=cursor.getString(cursor.getColumnIndex("SmallImage"));
                    String activityname=cursor.getString(cursor.getColumnIndex("ActivityName"));
                    String moveactivity=cursor.getString(cursor.getColumnIndex("MoveActivity"));

                    CustomerNotificationVO customerNotificationVO =new CustomerNotificationVO();
                    customerNotificationVO.setTitle(title);
                    customerNotificationVO.setMessage(message);
                    customerNotificationVO.setBigImage(imageUrl);
                    customerNotificationVO.setCreatedAt(time!=null?Utility.convertDate2String(new Date(Long.parseLong(time)),"dd-MM-yyyy HH:mm:ss "):Utility.convertDate2String(new Date(),"dd-MM-yyyy hh:mm:ss"));
                    customerNotificationVO.setServiceIcon(smallimage);
                    customerNotificationVO.setActivityName(activityname);
                    customerNotificationVOS.add(customerNotificationVO);
                    customerNotificationVO.setMoveActivity(moveactivity);
                    cursor.moveToNext();
                }
            }
            dataBaseHelper.close();
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
         return customerNotificationVOS;
    }




    public  static String getOxigenDataByTypeId(Context context , int txnId){
        StringBuilder stringBuilder =new StringBuilder();
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();

            String sql= "Select * ,oxigenbillerfilter.TypeFilterId as tm from  'oxigentransaction' INNER JOIN  oxigenbillerfilter  on oxigenbillerfilter.OxigenTxnId=oxigentransaction.TypeId where TypeId='"+txnId+"'";
            Cursor cursor= DataBaseHelper.myDataBase.rawQuery(sql, null);


            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {


                    int id = cursor.getInt(cursor.getColumnIndex("TypeId"));
                    int cid=cursor.getInt(cursor.getColumnIndex("CustomerId"));
                    int sid=cursor.getInt(cursor.getColumnIndex("ServiceTypeId"));
                    double amount=cursor.getDouble(cursor.getColumnIndex("Amount"));
                    String operator=cursor.getString(cursor.getColumnIndex("ServiceTypeId"));

                    stringBuilder.append(id + " ");
                    stringBuilder.append(cid + " ");
                    stringBuilder.append(sid + " ");
                    stringBuilder.append(amount + " ");
                    stringBuilder.append(operator + " ");

                    stringBuilder.append(cursor.getInt(cursor.getColumnIndex("tm")) +"table 2" + "\n");

                    cursor.moveToNext();
                }
            }
            dataBaseHelper.close();
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return stringBuilder.toString();
    }



    public  static  void deleteNotificationData(Context context , String tableName){
        List<CustomerNotificationVO> customerNotificationVOS= new ArrayList<>();
        try {
            DataBaseHelper dataBaseHelper =new DataBaseHelper(context);
            dataBaseHelper.createDataBase();
            dataBaseHelper.openDataBase();
            //String sql= "delete  from  'notification' ";
            //DataBaseHelper.myDataBase.execSQL(sql);
            DataBaseHelper.myDataBase.delete(tableName, null,null);
            Session.set_Data_Sharedprefence_BoolenvValue(context,Session.CACHE_IS_CLEAR_NOTIFICATION,true);
            dataBaseHelper.close();
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }



}

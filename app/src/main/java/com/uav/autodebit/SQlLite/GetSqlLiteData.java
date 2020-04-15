package com.uav.autodebit.SQlLite;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.uav.autodebit.vo.CustomerNotificationVO;

import java.util.ArrayList;
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


                    CustomerNotificationVO customerNotificationVO =new CustomerNotificationVO();
                    customerNotificationVO.setTitle(title);
                    customerNotificationVO.setMessage(message);
                    customerNotificationVO.setImage(imageUrl);
                    customerNotificationVO.setCreatedAt(time);
                    customerNotificationVO.setServiceIcon(smallimage);
                    customerNotificationVO.setActivityName(activityname);
                    customerNotificationVOS.add(customerNotificationVO);

                    cursor.moveToNext();
                }
            }
            dataBaseHelper.close();
        }catch (Exception e){
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
         return customerNotificationVOS;
    }
}

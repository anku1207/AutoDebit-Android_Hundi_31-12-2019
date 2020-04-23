package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;

import org.json.JSONObject;

public class PayBillByNotification extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_bill_by_notification);

        try {
            JSONObject jsonObject =new JSONObject(getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION));

            Toast.makeText(this, ""+jsonObject.getString("key") + "==== "+ jsonObject.getString("value"), Toast.LENGTH_SHORT).show();


        }catch (Exception e){

        }
    }
}

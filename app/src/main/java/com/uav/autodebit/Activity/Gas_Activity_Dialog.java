package com.uav.autodebit.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.uav.autodebit.R;

public class Gas_Activity_Dialog extends AppCompatActivity {

    String serviceid="" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.showgasbillconfirmation);
        serviceid = getIntent().getStringExtra("serviceid");

        DisplayMetrics displayMetrics =new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width=displayMetrics.widthPixels;
        int height=displayMetrics.heightPixels;

      //  getWindow().setLayout((int)(width*.9),(int)(height*.7));
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams params =getWindow().getAttributes();
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        getWindow().setAttributes(params);

        LinearLayout prepaid_ly = findViewById(R.id.prepaid_ly);
        prepaid_ly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                intent =new Intent(Gas_Activity_Dialog.this, Gas_Bill.class);
                intent.putExtra("serviceid",serviceid+"");
                startActivity(intent);
                finish();
            }
        });
        LinearLayout postpaid_lay = findViewById(R.id.postpaid_lay);
        postpaid_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent;
                intent =new Intent(Gas_Activity_Dialog.this, PNG.class);
                intent.putExtra("serviceid",serviceid+"");
                startActivity(intent);
                finish();
            }
        });

    }
}
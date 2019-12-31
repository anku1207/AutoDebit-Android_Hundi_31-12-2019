package com.uav.autodebit.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.util.ExceptionHandler;

public class Help extends AppCompatActivity {

    TextView emailid,mobileno,timing;
    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        getSupportActionBar().hide();

        mobileno=findViewById(R.id.mobileno);
        emailid=findViewById(R.id.emailid);
        timing=findViewById(R.id.timing);
        back_activity_button = findViewById(R.id.back_activity_button);

    }
}

package com.uav.autodebit.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.uav.autodebit.R;
import com.uav.autodebit.util.ExceptionHandler;

public class Help extends Base_Activity implements View.OnClickListener {

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
        back_activity_button.setOnClickListener(this);
    }


    public void backbuttonfun(){
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backbuttonfun();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                backbuttonfun();
                break;
        }
    }
}

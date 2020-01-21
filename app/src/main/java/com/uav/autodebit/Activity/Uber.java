package com.uav.autodebit.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.uav.autodebit.R;
import com.uav.autodebit.util.Utility;

import java.util.Arrays;

public class Uber extends AppCompatActivity implements View.OnClickListener {
    EditText name,lastname,email;
    Button proceed;
    ImageView back_activity_button;

    LinearLayout main;
    EditText [] edittextArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber);
        getSupportActionBar().hide();
        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        lastname=findViewById(R.id.lname);
        proceed=findViewById(R.id.proceed);
        back_activity_button=findViewById(R.id.back_activity_button);
        main=findViewById(R.id.main);

        proceed.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        edittextArray= new EditText[]{name, lastname, email};

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.proceed:
                if(!Utility.setErrorOnEdittext(edittextArray))return;
                Toast.makeText(this, "success", Toast.LENGTH_SHORT).show();
                break;
            case R.id.back_activity_button :
                finish();
                break;
        }
    }

}

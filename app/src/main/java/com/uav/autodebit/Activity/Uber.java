package com.uav.autodebit.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.uav.autodebit.R;

public class Uber extends AppCompatActivity implements View.OnClickListener {
    EditText name,lastname,email;
    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber);
        getSupportActionBar().hide();
        name=findViewById(R.id.name);
        email=findViewById(R.id.emailId);
        lastname=findViewById(R.id.lname);
        proceed=findViewById(R.id.proceed);

        proceed.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.proceed:
                Toast.makeText(this, "sdfsdf", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}

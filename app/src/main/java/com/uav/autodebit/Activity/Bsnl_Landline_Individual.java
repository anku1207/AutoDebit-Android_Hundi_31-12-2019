package com.uav.autodebit.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.uav.autodebit.R;
import com.uav.autodebit.override.UAVEditText;

public class Bsnl_Landline_Individual extends Base_Activity implements View.OnClickListener {
    EditText amount;
    ImageView back_activity_button;
    UAVEditText accountnumber,number;
    Button proceed;
    TextView fetchbill,title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bsnl__landline__individual);


        getSupportActionBar().hide();

        amount=findViewById(R.id.amount);
        back_activity_button=findViewById(R.id.back_activity_button1);
        title=findViewById(R.id.title);

        proceed=findViewById(R.id.proceed);
        accountnumber=findViewById(R.id.accountnumber);
        fetchbill=findViewById(R.id.fetchbill);
        number=findViewById(R.id.number);
        back_activity_button.setOnClickListener(this);

        proceed.setOnClickListener(this);
        fetchbill.setOnClickListener(this);

        title.setText(getIntent().getStringExtra("key"));




    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button1:
                finish();
                break;
            case R.id.proceed:
                validatefiled("proceed");
                break;
            case R.id.fetchbill:
                if( validatefiled("fetchbill")){
                    accountnumber.setError(null);
                    amount.setError(null);
                    number.setError(null);
                    Toast.makeText(this, "sdfsd", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public boolean validatefiled(String type){

        boolean valid=true;

        accountnumber.setError(null);
        amount.setError(null);
        number.setError(null);
        fetchbill.setVisibility(View.VISIBLE);


        if(accountnumber.getText().toString().equals("")){
            accountnumber.setError("this filed is required");
            valid=false;
        }
        if(number.getText().toString().equals("")){
            number.setError("this filed is required");
            valid=false;
        }

        if(type.equals("proceed")){
            if(amount.getText().toString().equals("")){
                amount.setError("this filed is required");
                valid=false;
            }
        }


        if(!accountnumber.getText().toString().equals("") && accountnumber.getText().toString().length()<10){
            accountnumber.setError("Please enter correct Account Number here");
            valid=false;
        }
        if(!number.getText().toString().equals("") && number.getText().toString().length()<10){
            number.setError("Please enter correct Number here");
            valid=false;
        }

       return valid;

    }

}

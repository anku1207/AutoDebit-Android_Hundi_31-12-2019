package com.uav.autodebit.Activity;


import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.uav.autodebit.R;

public class CreditCardBill extends Base_Activity implements View.OnClickListener {

    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_card_bill);
        getSupportActionBar().hide();

        back_activity_button=findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                finish();
                break;
        }
    }
}

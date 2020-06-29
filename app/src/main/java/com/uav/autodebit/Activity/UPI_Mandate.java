package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.uav.autodebit.R;

public class UPI_Mandate extends AppCompatActivity {
    ImageView imageview,back_activity_button;
    LinearLayout main_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_p_i__mandate);

        getSupportActionBar().hide();

        imageview=findViewById(R.id.imageview);
        main_layout=findViewById(R.id.main_layout);
        back_activity_button=findViewById(R.id.back_activity_button);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
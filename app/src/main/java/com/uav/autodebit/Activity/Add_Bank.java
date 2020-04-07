package com.uav.autodebit.Activity;

import android.os.Bundle;
import com.uav.autodebit.R;

public class Add_Bank extends Base_Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank);
        getSupportActionBar().hide();
    }
}

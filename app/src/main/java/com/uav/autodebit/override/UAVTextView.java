package com.uav.autodebit.override;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

public class UAVTextView extends TextView {

    private String txtAssociatedValue;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public UAVTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);

    }

    public UAVTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public UAVTextView(Context context, AttributeSet attrs) {
        super(context, attrs);


    }


    public UAVTextView(Context context) {
        super(context);

    }



    public String getTxtAssociatedValue() {
        return txtAssociatedValue;
    }

    public void setTxtAssociatedValue(String txtAssociatedValue) {
        this.txtAssociatedValue = txtAssociatedValue;
    }
}



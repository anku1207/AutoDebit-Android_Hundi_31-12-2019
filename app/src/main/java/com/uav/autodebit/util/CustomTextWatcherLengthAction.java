package com.uav.autodebit.util;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public class CustomTextWatcherLengthAction implements TextWatcher {

    private EditText mEditText;
    private Integer length;
    private EditText nextitem;

    private String type;
    public CustomTextWatcherLengthAction(EditText mEditText ,Integer lenght ,EditText nextitem ,String type) {
        this.mEditText = mEditText;
        this.length=lenght;
        this.nextitem=nextitem;
        this.type=type;
    }

    @Override
    public void afterTextChanged(Editable s) {

        switch (type){
            case "touch":
                if(s.length()==length){
                    nextitem.dispatchTouchEvent(Utility.ontouchevent());
                }
                break;
            case "click":
                if(s.length()==length){
                    nextitem.requestFocus();
                }
                break;
        }




    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {


    }

    @Override
    public void onTextChanged(CharSequence s, int start, int count, int after) {


    }

}

package com.uav.autodebit.Interface;

public class DateInterface {

    private DateInterface.OnSuccess onSuccess;

    public DateInterface(DateInterface.OnSuccess onSuccess){
        this.onSuccess  = onSuccess;
    }


    public void onSuccess(String s){
        onSuccess.onSuccess(s);
    }


    public interface OnSuccess {
        void onSuccess(String s);
    }

}

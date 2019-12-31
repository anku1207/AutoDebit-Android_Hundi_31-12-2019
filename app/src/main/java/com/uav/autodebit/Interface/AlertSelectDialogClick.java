package com.uav.autodebit.Interface;

public class AlertSelectDialogClick {
    private OnSuccess onSuccess;
    private OnError onError;

    public AlertSelectDialogClick(OnSuccess onSuccess, OnError onError){
        this.onSuccess  = onSuccess;
        this.onError = onError;
    }


    public AlertSelectDialogClick(OnSuccess onSuccess){
        this.onSuccess  = onSuccess;
    }



    public void onSuccess(String s){
        onSuccess.onSuccess(s);
    }

    public void onError(String s) {
        onError.onError(s);
    }

    public interface OnSuccess {
        void onSuccess(String s);
    }

    public interface OnError {
        void onError(String s);
    }

}

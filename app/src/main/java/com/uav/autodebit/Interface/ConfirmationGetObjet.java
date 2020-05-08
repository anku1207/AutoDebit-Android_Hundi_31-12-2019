package com.uav.autodebit.Interface;

import android.app.Dialog;

public class ConfirmationGetObjet {
    private OnOk onOk;
    private OnCancel onCancel;

    public ConfirmationGetObjet(OnOk onOk, OnCancel onCancel) {
        this.onOk = onOk;
        this.onCancel = onCancel;
    }

    public ConfirmationGetObjet(OnOk onOk) {
        this.onOk = onOk;
    }

    public void onOk(Object s) {
        onOk.onOk(s);
    }

    public void onCancel(Object s) {
        onCancel.onCancel(s);
    }

    public interface OnOk {

        void onOk(Object s);
    }

    public interface OnCancel {
        void onCancel(Object s) ;
    }
}
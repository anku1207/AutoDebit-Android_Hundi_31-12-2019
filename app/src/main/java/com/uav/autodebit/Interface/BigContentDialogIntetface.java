package com.uav.autodebit.Interface;

import android.app.Dialog;

public class BigContentDialogIntetface {
    private Button1 button1;
    private Button2 button2;


    public BigContentDialogIntetface(Button1  button1, Button2 button2) {
        this.button1 = button1;
        this.button2 = button2;
    }

    public BigContentDialogIntetface(Button1  button1) {
        this.button1 = button1;
    }

    public void button1(Dialog s) {
        button1.button1(s);
    }

    public void button2(Dialog s) {
        button2.button2(s);
    }



    public interface Button1 {

        void button1(Dialog s);
    }

    public interface Button2 {
        void button2(Dialog s) ;
    }

}

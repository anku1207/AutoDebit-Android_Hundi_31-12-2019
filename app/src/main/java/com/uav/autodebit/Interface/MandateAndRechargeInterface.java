package com.uav.autodebit.Interface;

public class MandateAndRechargeInterface {

    private OnRecharge onRecharge;
    private OnMandate onMandate;

    public MandateAndRechargeInterface() {
    }

    public MandateAndRechargeInterface(OnRecharge onRecharge,OnMandate onMandate ){
        this.onRecharge  = onRecharge;
        this.onMandate = onMandate;
    }
    public MandateAndRechargeInterface(OnRecharge onRecharge){
        this.onRecharge  = onRecharge;
    }

    public MandateAndRechargeInterface(OnMandate onMandate){
        this.onMandate  = onMandate;
    }
    public void onRecharge(Object s){
        onRecharge.onRecharge(s);
    }
    public void onMandate(Object s) {
        onMandate.onMandate(s);
    }

    public interface OnRecharge {
        void onRecharge(Object s);
    }
    public interface OnMandate {
        void onMandate(Object s);
    }
}


package com.uav.autodebit.Interface;

public class PaymentGatewayResponse {

    private PaymentGatewayResponse.OnPg onPg;
    private PaymentGatewayResponse.OnEnach onEnach;
    private PaymentGatewayResponse.OnEnachScheduler onEnachScheduler;
    private PaymentGatewayResponse.OnSiMandate onSiMandate;
    private OnUPIMandate onUPIMandate;

    public PaymentGatewayResponse(PaymentGatewayResponse.OnPg onPg,PaymentGatewayResponse.OnEnach onEnach , PaymentGatewayResponse.OnEnachScheduler onEnachScheduler,OnSiMandate onSiMandate ,OnUPIMandate onUPIMandate){
        this.onPg  = onPg;
        this.onEnach = onEnach;
        this.onEnachScheduler=onEnachScheduler;
        this.onSiMandate=onSiMandate;
        this.onUPIMandate=onUPIMandate;
    }
    public PaymentGatewayResponse(PaymentGatewayResponse.OnPg onPg){
        this.onPg  = onPg;
    }


    public void onPg(Object s){
        onPg.onPg(s);
    }

    public void onEnach(Object s) {
        onEnach.onEnach(s);
    }
    public void onUPIMandate(Object o){
        onUPIMandate.onUpiMandate(o);
    }


    public void onEnachScheduler(Object s) {
        onEnachScheduler.onEnachScheduler(s);
    }

    public void onSiMandate(Object s) {
        onSiMandate.onSiMandate(s);
    }



    public interface OnPg {
        void onPg(Object s);
    }

    public interface OnEnach {
        void onEnach(Object s);
    }

    public interface OnEnachScheduler {
        void onEnachScheduler(Object s);
    }

    public interface OnSiMandate {
        void onSiMandate(Object s);
    }

    public interface OnUPIMandate {
        void onUpiMandate(Object s);
    }

}

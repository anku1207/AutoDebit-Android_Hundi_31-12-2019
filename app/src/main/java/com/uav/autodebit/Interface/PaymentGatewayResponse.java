package com.uav.autodebit.Interface;

public class PaymentGatewayResponse {

    private PaymentGatewayResponse.OnPg onPg;
    private PaymentGatewayResponse.OnEnach onEnach;
    private PaymentGatewayResponse.OnEnachScheduler onEnachScheduler;

    public PaymentGatewayResponse(PaymentGatewayResponse.OnPg onPg,PaymentGatewayResponse.OnEnach onEnach , PaymentGatewayResponse.OnEnachScheduler onEnachScheduler){
        this.onPg  = onPg;
        this.onEnach = onEnach;
        this.onEnachScheduler=onEnachScheduler;
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


    public void onEnachScheduler(Object s) {
        onEnachScheduler.onEnachScheduler(s);
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

}

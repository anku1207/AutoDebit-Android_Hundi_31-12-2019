package com.uav.autodebit.Interface;

public class PaymentGatewayResponse {

    private PaymentGatewayResponse.OnPg onPg;
    private PaymentGatewayResponse.OnEnach onEnach;

    public PaymentGatewayResponse(PaymentGatewayResponse.OnPg onPg,PaymentGatewayResponse.OnEnach onEnach ){
        this.onPg  = onPg;
        this.onEnach = onEnach;
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

    public interface OnPg {
        void onPg(Object s);
    }

    public interface OnEnach {
        void onEnach(Object s);
    }

}

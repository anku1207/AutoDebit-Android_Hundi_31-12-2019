package com.uav.autodebit.constant;

import android.content.Context;
import android.content.SharedPreferences;

import java.net.StandardProtocolFamily;
import java.util.regex.Pattern;

public class ApplicationConstant {

    public static final String MOBILENO_VALIDATION="{\"pattern\":\"^[6-9][0-9]{9}$\", \"msg\":  \"Mobile No. accepts only  numbers and length should be 10 (first number to start with [6-9])}\"}";
    public static final String SOMETHINGWRONG = "Something went wrong. Please Try Again";
    public static final String EMAIL_VALIDATION="{\"pattern\":\"^[a-zA-Z0-9][a-zA-Z0-9._-]+@[a-zA-Z0-9][a-zA-Z0-9.-]+.[a-zA-Z]{2,6}$\",  \"msg\": \"Enter a valid email address\"}";

    public static final Pattern pancard = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

    public final static String AUTHKEY= "G4s4cCMx2aM7lky1";

    public final static  boolean IS_PRODUCTION_ENVIRONMENT= false;

    public static int MobilePostpaid=14,d2h=17,Dmrc=2,Electricity=10,Landline=7,Water=12,Broadband=8,PNG=6,Gas=11,DTH=13,DISHTV=18,INSURANCE_RENEWAL=19,LOAN_REPAYMENT=20,FASTAG=21,CABLE_TV=22 ,Uber=16,IRCTC=1 ;

    public static int  REQ_ENACH_MANDATE=1003,REQ_ALLSERVICE=1004,REQ_SI_MANDATE=1005,REQ_MANDATE_FOR_FIRSTTIME_RECHARGE=1006,REQ_POPAPACTIVITYRESULT=1007,REQ_MANDATE_FOR_BILL_FETCH_ERROR=1008,REQ_SI_FOR_BILL_FETCH_ERROR=1009,REQ_UPI_FOR_MANDATE=1010;
    public static int  REQ_CAMERA_PERMISSION=2001, REQ_DOWNLOAD_PERMISSION=2002,REQ_READ_SMS_PERMISSION=2003,REQ_READ_CONTACT_PERMISSION=2004;
    public static int  REQ_Code_D2H=3001;

    public static String PG_MANDATE="mandate";
    public static String PG_PAYMENT="payment";
    public static String PG_MANDATE_AND_RECHARGE="mandateandrecharge";
    public static String NOTIFICATION_ACTION="notification_act";


    public final static  String SI_SERVICE="autopepg";

    //private final static String URL_ADDRESS = getServerAddress();
    private final static String HTTPURL = getServerAddress();


    public  static final int SOCKET_TIMEOUT_MILLISEC = 60000;
    public static final String SHAREDPREFENCE = "hundi";

    public static final String STATUS_SIGNUP_MOBILE_OTP_VERIFY="SIGNUP_MOBILE_OTP_VERIFIED";
    public static final String STATUS_SIGNUP_EMAIL_OTP_VERIFY="SIGNUP_EMAIL_OTP_VERIFIED";
    public static final String STATUS_SIGNUP_ACTIVE="Active";
    public static final String STATUS_PAN_VERIFIED="Pan_Verified";

    public static final String INTENT_EXTRA_CONNECTION = "connection";


    public static Integer BankMandatePayment=1,SIMandatePayment=2,UPIMandatePayment=3;





    public static final String CACHE_PORT="port";
    public static final String CACHE_IPADDRESS="ipaddress";
    public static final String CACHE_PROTOCOL="protocol";

    public  static String getHttpURL(Context context){
        SharedPreferences sharedPreferences;
        sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE,  Context.MODE_PRIVATE);
        String protocol= (String)sharedPreferences.getString( ApplicationConstant.CACHE_PROTOCOL,null);
        String ipAddress= (String)sharedPreferences.getString( ApplicationConstant.CACHE_IPADDRESS,null);
        String port= (String)sharedPreferences.getString( ApplicationConstant.CACHE_PORT,null);

        if(protocol!=null && ipAddress != null && port!=null){
            return protocol+"://"+ipAddress + ":" + port + "/rof/rest/stateless/";
        }else{
            return HTTPURL;
        }

    }

    private static String getServerAddress(){
        if(IS_PRODUCTION_ENVIRONMENT){
            return "http://app.autope.in/hundi/rest/stateless/";
          //  return "http://164.52.192.45";
        }else{
             return  "http://205.147.103.18:8080/hundi/rest/stateless/" ;
            // return  "http://192.168.1.103:8080/hundi/rest/stateless/" ;
        }
    }

}


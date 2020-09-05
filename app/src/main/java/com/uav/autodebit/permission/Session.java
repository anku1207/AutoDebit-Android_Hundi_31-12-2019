package com.uav.autodebit.permission;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.vo.CustomerVO;


public class Session {
    static SharedPreferences sharedPreferences;
    static Gson gson = new Gson();

    public  static String CACHE_CUSTOMER="CUSTOMERDATA";
    public final static String LOCAL_CACHE = "LOCALCACHE_UAV";

    public  static String CACHE_BROWSE_DATA="BROWSEDATA";

    public final static String BANNER_LIST = "BANNERLIST";
    public final static String MOBILE_OPERATOR_LIST = "MOBILEOPERATOR";
    public final static String MOBILE_STATE_LIST = "MOBILE_STATE_LIST";

    public final static String DTH_OPERATOR_LIST = "DTHOPERATOR";
    public final static String CACHE_DMRC_MIN_CARD_CHARGE = "dmrccardchargemin";


    public final static String CACHE_LANDLINE_OPERATOR = "LANDLINEOPERATOR";
    public final static String CACHE_BROADBAND_OPERATOR = "BROADBAND_OPERATOR";
    public final static String CACHE_WATER_OPERATOR = "WATER_OPERATOR";
    public final static String CACHE_GAS_OPERATOR = "GAS_OPERATOR";
    public final static String CACHE_ELECTRICITY_OPERATOR = "ELECTRICITY_OPERATOR";
    public final static String CACHE_POSTPAID_OPERATOR = "POSTPAIDOPEATOR";
    public final static String CACHE_PNG_OPERATOR = "PNG_OPERATOR";
    public final static String CACHE_INSURANCE_OPERATOR = "INSURANCE_OPERATOR";
    public final static String CACHE_LOAN_OPERATOR = "LOAN_OPERATOR";
    public final static String CACHE_FASTTAG_OPERATOR = "FASTTAG_OPERATOR";
    public final static String CACHE_CABLETV_OPERATOR = "CABLETV_OPERATOR";



    public final static String CACHE_IS_NEW_USER = "IS_NEW_USER";
    public final static String CACHE_USER_LOGINID = "USERLOGINID";
    public final static String CACHE_TOKENID = "TOKENID";
    public final static String CACHE_NOTIFICATION = "NOTIFICATION";

    public final static String CACHE_IS_CLEAR_NOTIFICATION = "CLEAR_NOTIFICATION";

    public final static String CACHE_ASK_PERMISSION = "ASKPERMISSION";

    public final static String CACHE_IS_DMRC_CARD_ALLOT= "ALLOT_DMRC_CATD";



    public static String getCustomerId(Context context){
       SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
       CustomerVO customerVO=gson.fromJson(sharedPreferences.getString(Session.CACHE_CUSTOMER,null),CustomerVO.class);
       return customerVO.getCustomerId().toString();
    }
    public static Integer getCustomerLevel(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        CustomerVO customerVO=gson.fromJson(sharedPreferences.getString(Session.CACHE_CUSTOMER,null),CustomerVO.class);
        return customerVO.getLevel().getLevelId();
    }

    public static String getCustomerName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        CustomerVO customerVO=gson.fromJson(sharedPreferences.getString(Session.CACHE_CUSTOMER,null),CustomerVO.class);
        return customerVO.getName();
    }

    public static Double getCustomerHighestMandateAmount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        CustomerVO customerVO=gson.fromJson(sharedPreferences.getString(Session.CACHE_CUSTOMER,null),CustomerVO.class);
        return customerVO.getHighestMandateAmount();
    }

    public static String getResponseURL(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        CustomerVO customerVO=gson.fromJson(sharedPreferences.getString(Session.CACHE_CUSTOMER,null),CustomerVO.class);
        return customerVO.getName();
    }


    public static boolean set_Data_Sharedprefence(Context context,String CacheName ,String data){
        sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= sharedPreferences.edit();
        edit.putString( CacheName,data);
        edit.apply();
        return edit.commit();
    }



    public static void set_Data_Sharedprefence_BoolenvValue(Context context,String CacheName ,Boolean data){
        sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE,Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= sharedPreferences.edit();
        edit.putBoolean( CacheName,data);
        edit.apply();
        edit.commit();
    }



    public static boolean check_Exists_key(Context context,String key ){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }


    public static String getSessionByKey(Context context,String cacheKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        String resp=sharedPreferences.getString(cacheKey,null);
        return resp;

    }

    public static Boolean getSessionByKey_BoolenValue(Context context,String cacheKey){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        boolean resp=sharedPreferences.getBoolean(cacheKey,false);
        return resp;

    }

    public static String getCustomerIdOnExceptionTime(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(ApplicationConstant.SHAREDPREFENCE, Context.MODE_PRIVATE);
        CustomerVO customerVO=gson.fromJson(sharedPreferences.getString(Session.CACHE_CUSTOMER,null),CustomerVO.class);
        return customerVO!=null?customerVO.getCustomerId().toString():null;
    }



}

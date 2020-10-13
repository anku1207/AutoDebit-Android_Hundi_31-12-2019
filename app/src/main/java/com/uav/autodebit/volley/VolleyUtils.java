package com.uav.autodebit.volley;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.widget.ProgressBar;


import androidx.annotation.RequiresApi;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.GlobalApplication;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class VolleyUtils {
    ProgressBar progressBar;
    static VolleyResponseListener responseListener;
    static String errorMessage;
    private static final String PROTOCOL_CHARSET="utf-8";

    private static Context mctx;

    public static void makeJsonObjectRequest(final Context context, ConnectionVO connectionVO, final VolleyResponseListener listener) {
        Map<String, Object> params = connectionVO.getParams();
        responseListener  = listener;

        mctx=context;

        JSONObject jsonParams = null;
        if(params!=null){
            jsonParams = new JSONObject(params);
        }


        final ProgressDialog pDialog = new ProgressDialog(context);
        pDialog.setMessage("Connecting ...");
        pDialog.setCancelable(false);
        pDialog.setCanceledOnTouchOutside(false);

        if(connectionVO.getLoaderAvoided()==null || !connectionVO.getLoaderAvoided()) {
            try {
                pDialog.show();
            }
            catch (Exception e) {
                //use a log message
            }

        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (connectionVO.getRequestType(), ApplicationConstant.getHttpURL(context) +"_"+Utility.getVersioncode(context)+"/"+connectionVO.getMethodName() , jsonParams, response -> {
                    Utility.dismissDialog(mctx,pDialog);
                    try {
                        listener.onResponse(response);

                    } catch (Exception e) {
                        ExceptionsNotification.ExceptionHandling(mctx , Utility.getStackTrace(e));
                    }
                }, volleyError -> {
                   // Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    Utility.dismissDialog(mctx,pDialog);
                    boolean showerror=true;

                    if (volleyError instanceof NetworkError) {
                        errorMessage = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        if(volleyError.networkResponse.statusCode==503){
                            showerror=false;
                            showForceUpdateDialog();
                        }else {
                            errorMessage = "The server could not be found. Please try again after some time!!";
                        }
                    } else if (volleyError instanceof AuthFailureError) {
                        errorMessage = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        errorMessage = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof NoConnectionError) {
                        errorMessage = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof TimeoutError) {
                        errorMessage = "Connection TimeOut! Please check your internet connection.";
                    }else {
                        errorMessage= volleyError.toString();
                    }
                    if(showerror) showError("Connection Error", errorMessage, context );
                    //listener.onError(volleyError.toString());
                }) {


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError{
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type","application/json");
                params.put("authkey","G4s4cCMx2aM7lky1");
                params.put("versioncode", String.valueOf(Utility.getVersioncode(context)));
                return params;
            }


            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                Utility.dismissDialog(mctx,pDialog);
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }
        };

        // Access the RequestQueue through singleton class.
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(ApplicationConstant.SOCKET_TIMEOUT_MILLISEC,DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addTorequestque(jsonObjectRequest);
    }





    public static   void showError(String title, String error, final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(error)
                .setTitle(title+"!")
                .setIcon(android.R.drawable.ic_dialog_alert)


                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent =new Intent();
                        responseListener.onError(errorMessage);
                    }
                });
        AlertDialog alert= builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.setCancelable(false);
        if(!((Activity)context).isFinishing()){
            //show dialog
            alert.show();
        }
    }


    public static void showForceUpdateDialog(){
        MyDialog.showImageSingleButtonDialog(mctx,"Alert",GlobalApplication.updateMsg,R.drawable.error_image_yellow,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
            Utility.dismissDialog(mctx,ok);
            ((Activity)mctx).startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse
                    ("market://details?id="+ mctx.getPackageName() )));
            ((Activity) mctx).finish();

        }),"Update");
    }

}


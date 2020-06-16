package com.uav.autodebit.CustomDialog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.uav.autodebit.Interface.BigContentDialogIntetface;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.ConfirmationGetObjet;
import com.uav.autodebit.Interface.DateInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class MyDialog {



    @SuppressLint("SetJavaScriptEnabled")
    public static void showWebviewAlertDialog(Context context , String html, boolean backBtnCloseDialog, ConfirmationDialogInterface confirmationDialogInterface){
        final Dialog cusdialog = new Dialog(context);
        cusdialog.requestWindowFeature(1);
        Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        cusdialog.setContentView(R.layout.webview_alert_dialog);
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(backBtnCloseDialog);
        cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        WebView webview = cusdialog.findViewById(R.id.webview);
        WebSettings ws = webview.getSettings() ;
        ws.setJavaScriptEnabled( true ) ;
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setBuiltInZoomControls(false);

        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);

        webview.loadData(html, "text/html; charset=utf-8", "UTF-8");
        webview.addJavascriptInterface( new Object() {
            @JavascriptInterface // For API 17+
            public void performClick (String message) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(message.equalsIgnoreCase("ok")){
                            confirmationDialogInterface.onOk(cusdialog);
                        }else if(message.equalsIgnoreCase("cancel")){
                            confirmationDialogInterface.onCancel(cusdialog);
                        }
                    }
                });
            }
        } , "ok" ) ;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cusdialog.getWindow().getAttributes());
        lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
        lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);

        if(!cusdialog.isShowing())cusdialog.show();
        cusdialog.getWindow().setAttributes(lp);
    }


    @SuppressLint("SetJavaScriptEnabled")
    public static void showWebviewConditionalAlertDialog(Context context , String value, boolean backBtnCloseDialog, ConfirmationGetObjet confirmationGetObjet){
        final Dialog cusdialog = new Dialog(context);
        cusdialog.requestWindowFeature(1);
        Objects.requireNonNull(cusdialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        cusdialog.setContentView(R.layout.webview_alert_dialog);
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(backBtnCloseDialog);
        // cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        String[] value_split = value.split("\\|");
        WebView webview = cusdialog.findViewById(R.id.webview);
        LinearLayout addview = cusdialog.findViewById(R.id.addview);

        if(value_split.length>1){
            TextView textView =Utility.getTextView(context,value_split[1]);
            addview.addView(textView);
            textView.setLayoutParams(Utility.getLayoutparams(context,0,0,0,0));
            addview.setVisibility(View.VISIBLE);
            addview.setPadding(10,10,10,10);
        }

        WebSettings ws = webview.getSettings() ;
        ws.setJavaScriptEnabled( true ) ;
        webview.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
        });


        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.getSettings().setBuiltInZoomControls(false);

        webview.getSettings().setLoadsImagesAutomatically(true);
        webview.getSettings().setDomStorageEnabled(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);

        // webview.loadData(html, "text/html; charset=utf-8", "UTF-8");
        webview.loadUrl(value_split[0]); //receiptUrl
        webview.addJavascriptInterface( new Object() {
            @JavascriptInterface // For API 17+
            public void performClick (String message) {
                ((Activity)context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(message.equalsIgnoreCase("cancel")){
                            confirmationGetObjet.onCancel(cusdialog);
                        }else {
                            HashMap<String,Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("dialog",cusdialog);
                            objectsHashMap.put("data",message);
                            confirmationGetObjet.onOk(objectsHashMap);
                        }
                    }
                });
            }
        },"ok" );
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(cusdialog.getWindow().getAttributes());
        lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
        lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);
        cusdialog.getWindow().setAttributes(lp);

        webview.setWebViewClient(new WebViewClient(){
            final ProgressDialog progressBar = ProgressDialog.show(context, null, " Please wait...", false, false);
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!progressBar.isShowing()) {
                    progressBar.show();
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                if (progressBar.isShowing()) {
                    progressBar.dismiss();
                }
                if(!cusdialog.isShowing())cusdialog.show();
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            }
        });
    }


    public static void showScheduleMandateDialog(ConfirmationGetObjet confirmationGetObjet, Context context , String Msg , String title, boolean backBtnCloseDialog, String... buttons){
        String leftButton= (buttons.length==0 ?"Yes":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton=(buttons.length<=1 ?"No":buttons[1]);//(rightButton==null?"Next":rightButton);
        try{
            final Dialog cusdialog = new Dialog(context);
            cusdialog.requestWindowFeature(1);
            cusdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            cusdialog.setContentView(R.layout.manual_schedule_mandate);
            cusdialog.setCanceledOnTouchOutside(false);
            cusdialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            cusdialog.setCancelable(backBtnCloseDialog);


            TextView titletext=cusdialog.findViewById(R.id.title);
            TextView msg=cusdialog.findViewById(R.id.message);
            EditText days=cusdialog.findViewById(R.id.days);
            EditText datetext=cusdialog.findViewById(R.id.date);
            EditText amount=cusdialog.findViewById(R.id.amount);

            Button yes=cusdialog.findViewById(R.id.button1);
            Button no=cusdialog.findViewById(R.id.button2);

            yes.setText(leftButton);
            no.setText(rightButton);


            if(title==null || title.equals(""))titletext.setVisibility(View.GONE);
            if(Msg==null || Msg.equals(""))titletext.setVisibility(View.GONE);

            titletext.setText(title);
            msg.setText(Msg);

            days.addTextChangedListener(new TextWatcher() {
                // the user's changes are saved here
                public void onTextChanged(CharSequence c, int start, int before, int count) {
                }
                public void beforeTextChanged(CharSequence c, int start, int count, int after) {
                    // this space intentionally left blank
                }
                public void afterTextChanged(Editable c) {
                    // this one too
                    if(!c.toString().equals("")){
                        String date = Utility.convertDate2String(Utility.addDayInSelectDate(new Date(),Integer.parseInt(c.toString())),"dd/MM/yyyy");
                        datetext.setText(date);
                    }else {
                        datetext.setText(null);
                    }
                }
            });

            datetext.setClickable(false);
            datetext.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
                        try {
                            Calendar c = Calendar.getInstance();
                            if(!datetext.getText().toString().equals("")){
                                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                                c.setTime(sdf.parse(datetext.getText().toString()));
                            }
                            DatePickerDialog datePickerDialog = Utility.DatePickerReturnDate(context,c,new DateInterface((DateInterface.OnSuccess)(stringdate)->{
                                datetext.setError(null);
                                days.setError(null);
                                datetext.setText(stringdate);
                                days.setText( Utility.getTwoDatedeff(Utility.removeTime(new Date()),Utility.convertString2Date(stringdate,"dd/MM/yyyy"))+"");
                                amount.requestFocus();

                            }));

                            Date bookingDate =new Date();
                            datePickerDialog.getDatePicker().setMinDate(Utility.addDayInSelectDate(bookingDate,1).getTime());
                            datePickerDialog.show();
                        }catch (Exception e){
                            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                        }
                        return true;
                    }
                    return false;
                }
            });

            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    try {
                        days.setError(null);
                        datetext.setError(null);
                        amount.setError(null);

                        boolean validation=true;
                        if(days.getText().toString().equals("")){
                            days.setError(ErrorMsg.user_Registration_Filed_Required);
                            validation=false;

                        }
                        if(datetext.getText().toString().equals("")){
                            datetext.setError(ErrorMsg.user_Registration_Filed_Required);
                            validation=false;

                        }
                        if(amount.getText().toString().equals("")){
                            amount.setError(ErrorMsg.user_Registration_Filed_Required);
                            validation=false;

                        }

                        if(validation){

                            HashMap<String,Object> objectsHashMap = new HashMap<>();
                            objectsHashMap.put("dialog",cusdialog);

                            JSONObject jsonObject =new JSONObject();
                            jsonObject.put("day",days.getText().toString());
                            jsonObject.put("amount",amount.getText().toString());
                            objectsHashMap.put("data",jsonObject.toString());

                            confirmationGetObjet.onOk(objectsHashMap);
                        }
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                    }
                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    confirmationGetObjet.onCancel(cusdialog);

                }
            });

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(cusdialog.getWindow().getAttributes());
            lp.width = (WindowManager.LayoutParams.MATCH_PARENT);
            lp.height = (WindowManager.LayoutParams.WRAP_CONTENT);

            if(!cusdialog.isShowing())cusdialog.show();
            cusdialog.getWindow().setAttributes(lp);
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }



    public static void showSingleButtonBigContentDialog(Context context, ConfirmationDialogInterface confirmationDialogInterface, String title, String msg , String... buttons){

        String leftButton= (buttons.length==0 ?"OK":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        final Dialog var3 = new Dialog(context);


        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(R.layout.singlebutton_bigcontent_dialog);
        var3.setCanceledOnTouchOutside(false);
        //   var3.setCancelable(false);

        var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView title_text = (TextView)var3.findViewById(R.id.title);

        if(title==null || title.equals("")){
            title_text.setVisibility(View.GONE);
        }else {
            title_text.setText(title);
            title_text.setVisibility(View.VISIBLE);
         }
        TextView msg_text = (TextView)var3.findViewById(R.id.message);
        msg_text.setText(msg);
        Button button = (Button)var3.findViewById(R.id.btn);
        button.setText(leftButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                confirmationDialogInterface.onOk(var3);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(var3.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if(!var3.isShowing())  var3.show();
        var3.getWindow().setAttributes(lp);
    }




    public static void showDoubleButtonBigContentDialog(Context context, BigContentDialogIntetface bigContentDialogIntetface, String title, String msg , String... buttons){

        String leftButton= (buttons.length==0 ?"Modify":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton=(buttons.length<=1 ?"Next":buttons[1]);//(rightButton==null?"Next":rightButton);
        final Dialog var3 = new Dialog(context);


        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(R.layout.doublebutton_bigcontent_dialog);
        var3.setCanceledOnTouchOutside(false);
        //   var3.setCancelable(false);

        var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView title_text = (TextView)var3.findViewById(R.id.title);

        if(title==null || title.equals("")){
            title_text.setVisibility(View.GONE);
        }else {
            title_text.setText(title);
            title_text.setVisibility(View.VISIBLE);
        }
        TextView msg_text = (TextView)var3.findViewById(R.id.message);
        msg_text.setText(msg);
        Button button1 = (Button)var3.findViewById(R.id.button1);
        button1.setText(leftButton);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                bigContentDialogIntetface.button1(var3);
            }
        });


        Button button2 = (Button)var3.findViewById(R.id.button2);
        button2.setText(rightButton);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                bigContentDialogIntetface.button2(var3);
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(var3.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if(!var3.isShowing())  var3.show();
        var3.getWindow().setAttributes(lp);


    }


}

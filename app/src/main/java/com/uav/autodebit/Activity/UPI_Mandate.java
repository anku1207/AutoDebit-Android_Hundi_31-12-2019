package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;
import com.uav.autodebit.BO.SiBO;
import com.uav.autodebit.BO.UPIBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;

public class UPI_Mandate extends AppCompatActivity  implements MyJavaScriptInterface.javascriptinterface{
    ImageView imageview,back_activity_button;
    LinearLayout main_layout;
    WebView webview;

    boolean foractivity;
    int actionId;
    double amount;
    String redirectUrl, cancelUrl,paymentType,serviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_u_p_i__mandate);

        getSupportActionBar().hide();

        imageview=findViewById(R.id.imageview);
        main_layout=findViewById(R.id.main_layout);
        back_activity_button=findViewById(R.id.back_activity_button);
        webview=findViewById(R.id.webview);

        foractivity=getIntent().getBooleanExtra("forresutl",true);//success finish activity
        actionId=getIntent().getIntExtra("id",0);
        amount=getIntent().getDoubleExtra("amount",1.00);
        serviceId=getIntent().getStringExtra("serviceId");
        paymentType=getIntent().getStringExtra("paymentType");

        Log.w("getIntentResult",foractivity+"="+actionId+"="+amount+"="+serviceId+"="+paymentType);


        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getServiceURL(UPI_Mandate.this, new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            try {
                Log.w("resp",success+"");
                JSONObject respjson = (JSONObject) success;
                String url = respjson.getString("url") + "?customerId=" + Session.getCustomerId(UPI_Mandate.this) + "&entityTypeId=2" + "&versioncode="+ Utility.getVersioncode(UPI_Mandate.this)+ "&Amount="+amount + "&serviceId="+serviceId + "&paymentType="+paymentType;
                openWebView(url);
            }catch (Exception e){

            }

        }));
    }


    public void getServiceURL(Context context, VolleyResponse volleyResponse) {
        VolleyUtils.makeJsonObjectRequest(this, UPIBO.getAutopeUPIRecurringUrl(), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                if (!response.getString("status").equals("200")) {
                    Utility.alertDialog(context, "Alert", response.getString("errorMsg"), "Ok");
                } else {
                    volleyResponse.onSuccess(response);
                }
            }
        });

    }


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    void openWebView(final String receiptUrl) {
        Log.w("CallUrl", receiptUrl);

        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);

        webview.setWebViewClient(new MyBrowser());

        // webview.getSettings().setLoadsImagesAutomatically(true);

        webview.getSettings().setBuiltInZoomControls(true);
        webview.setInitialScale(1);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setSupportMultipleWindows(true);
        webview.getSettings().setLoadWithOverviewMode(true);

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMinimumFontSize(16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        settings.setDomStorageEnabled(true);

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

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {

                WebView newWebView = new WebView(UPI_Mandate.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setSupportZoom(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
                //  newWebView.getSettings().setSupportMultipleWindows(true);
                view.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return true;
                    }
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.w("pagestart", url);

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.w("loadurlresp", url);

                    }

                    @SuppressWarnings("deprecation")
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        showError(description);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        showError((String) error.getDescription());
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        showError(errorResponse.getReasonPhrase().toString());
                    }

                });

                return true;
            }
        });
        webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        webview.loadUrl(receiptUrl); //receiptUrl
    }

    private void showError(String description) {
        Log.e("weverrir", description);
    }

    @Override
    public void htmlresult(String result) {
        Log.w("htmlresp", result);
    }




    private class MyBrowser extends WebViewClient {
        final ProgressDialog progressBar = ProgressDialog.show(UPI_Mandate.this, null, " Please wait...", false, false);

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.w("URL", url);
            view.loadUrl(url);
            if(!UPI_Mandate.this.isFinishing() &&  progressBar!=null && !progressBar.isShowing())  progressBar.show();
            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.w("pagestart", url);
            if(!UPI_Mandate.this.isFinishing() &&  progressBar!=null && !progressBar.isShowing())  progressBar.show();


        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (!UPI_Mandate.this.isFinishing() && progressBar!=null && progressBar.isShowing()) {
                progressBar.dismiss();
            }

        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode,String description, String failingUrl) {

        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {

        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:

                    finish();
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
















}
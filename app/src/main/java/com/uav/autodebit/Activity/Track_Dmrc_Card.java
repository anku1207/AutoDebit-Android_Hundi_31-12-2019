package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BannerVO;

import org.json.JSONObject;

public class Track_Dmrc_Card extends Base_Activity implements View.OnClickListener {
    ImageView back_activity_button;
    WebView webView;
    ProgressDialog progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track__dmrc__card);
        getSupportActionBar().hide();

        progressBar = ProgressDialog.show(Track_Dmrc_Card.this, null, " Please wait...", false, false);


        back_activity_button=findViewById(R.id.back_activity_button);
        back_activity_button.setOnClickListener(this);

        try {
            String notificationData =getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION);
            if(notificationData!=null){
                JSONObject jsonObject = new JSONObject(notificationData);
                if(jsonObject.has("value") &&   jsonObject.isNull("value")){
                    Utility.showSingleButtonDialog(this,"Error !", Content_Message.error_message,true);
                }else{
                    openWebView(ApplicationConstant.getTrackingUrl()+"?cardId="+ jsonObject.getInt("value"));
                }
            }else {
                String cardId=getIntent().getStringExtra("cardId");
                openWebView(ApplicationConstant.getTrackingUrl()+"?cardId="+cardId);
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(Track_Dmrc_Card.this, Utility.getStackTrace(e));
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    void openWebView(final String receiptUrl) {
        webView = findViewById(R.id.webView);
        // webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.getSettings().setBuiltInZoomControls(false);

        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);

        webView.setInitialScale(1);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMinimumFontSize(16);
        webView.setDrawingCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(!Track_Dmrc_Card.this.isFinishing() &&  progressBar!=null && !progressBar.isShowing()){
                    try {
                        progressBar.show();
                    }catch (Exception e){
                    }
                }
                if(newProgress==100){
                    Utility.dismissDialog(Track_Dmrc_Card.this, progressBar);
                }
            }
        });

        webView.addJavascriptInterface( new Object() {
            @JavascriptInterface // For API 17+
            public void performClick (String message) {
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         finish();
                    }
                });
            }
        },"cancel" );
        webView.setWebViewClient(new MyBrowser());
        webView.loadUrl(receiptUrl); //receiptUrl
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showError(String description) {
        Log.w("webviewerror",description);
    }


    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.w("URL",url);
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if( !Track_Dmrc_Card.this.isFinishing() && progressBar!=null && !progressBar.isShowing())  progressBar.show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Utility.dismissDialog(Track_Dmrc_Card.this, progressBar);
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            Utility.dismissDialog(Track_Dmrc_Card.this, progressBar);

            showError(description);
        }
        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            Utility.dismissDialog(Track_Dmrc_Card.this, progressBar);

            showError((String) error.getDescription());
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            Utility.dismissDialog(Track_Dmrc_Card.this, progressBar);

            showError(errorResponse.getReasonPhrase().toString());
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back_activity_button) {
            finish();
        }
    }




}
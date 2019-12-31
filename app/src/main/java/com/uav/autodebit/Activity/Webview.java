package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class Webview extends AppCompatActivity {


    WebView webView;
    ImageView back_activity_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_webview);



        Intent intent = getIntent();


        openWebView(intent.getStringExtra("url")); //http://d.eze.cc/r/o/0zfATRE7





    }

        @SuppressLint("SetJavaScriptEnabled")
        void openWebView(final String receiptUrl) {

            setContentView(R.layout.activity_webview);
             webView = findViewById(R.id.webView);
            //webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.setVerticalScrollBarEnabled(false);
            webView.setHorizontalScrollBarEnabled(false);


            //  wv.setWebViewClient(new MyBrowser());
            //webSettings.setDomStorageEnabled(true);

            //wv.getSettings().setLoadsImagesAutomatically(true);
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

            /*if (Build.VERSION.SDK_INT >= 19) {
                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            }*/

            webView.getSettings().setAppCacheEnabled(false);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


            webView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                    android.util.Log.d("WebView", consoleMessage.message());
                    return true;
                }


            });

            webView.setWebViewClient(new MyBrowser() );

            back_activity_button=findViewById(R.id.back_activity_button);

            back_activity_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    webView.goBack();
                }
            });

            webView.loadUrl(receiptUrl); //receiptUrl




        }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && this.webView.canGoBack()) {
            this.webView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }







    private void showError(String description) {

      /*  new AlertDialog.Builder(Webview.this)
                .setTitle("Error")
                .setMessage(description)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })

                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();*/

        Log.w("webviewerror",description);

    }



    private class MyBrowser extends WebViewClient {
        final ProgressDialog progressBar = ProgressDialog.show(Webview.this, null, " Please wait...", false, false);

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.w("URL",url);
            view.loadUrl(url);
            return true;
        }


        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

            if(url.contains("enachResponse")) {
                progressBar.dismiss();

                Intent intent12 = new Intent();
                intent12.putExtra("url",Utility.getQueryarray(url).toString());
                setResult(RESULT_OK,intent12);
                finish() ;



            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
                 progressBar.dismiss();

        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            showError(description);
        }
        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            showError((String) error.getDescription());
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (progressBar.isShowing()) {
                progressBar.dismiss();
            }
            showError(errorResponse.getReasonPhrase().toString());
        }
    }



}

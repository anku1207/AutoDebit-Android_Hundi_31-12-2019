package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
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

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.uav.autodebit.BO.PaymentGateWayBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.PayUVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PaymentGateWay extends AppCompatActivity implements MyJavaScriptInterface.javascriptinterface {

    WebView webView;
    ImageView back_activity_button;
    String redirectUrl, cancelUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_payment_gate_way);
        getPaymentGateWayURL(getIntent().getStringExtra("oxigenTypeId"));
    }

    public void getPaymentGateWayURL(String id) {
        Log.w("typeid",id);
        VolleyUtils.makeJsonObjectRequest(this, PaymentGateWayBO.getPaymentGateWayUrl(), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                if (!response.getString("status").equals("200")) {
                    Utility.showSingleButtonDialog(PaymentGateWay.this, "Alert", response.getString("errorMsg"), false);
                } else {
                        Log.w("getPaymentGateWayUrl", response.toString());
                        redirectUrl = response.getString("redirectUrl");
                        cancelUrl = response.getString("cancelUrl");
                        String url = response.getString("url") + "&txnid=" +id;
                        openWebView(url);
                }
            }
        });
    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    void openWebView(final String receiptUrl) {
        webView = findViewById(R.id.webView);
        // webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);


        webView.getSettings().setBuiltInZoomControls(true);

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

        webView.setWebViewClient(new MyBrowser());
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");

        back_activity_button=findViewById(R.id.back_activity_button);

        back_activity_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
            }
        });

        webView.loadUrl(receiptUrl); //receiptUrl

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    /*if (webView.canGoBack()) {
                        webView.goBack();
                    } else {
                        finish();
                    }*/
                    String[] buttons = {"No","Yes"};
                    Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            finish();

                        }

                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();

                        }
                    },PaymentGateWay.this,"Do you really want to cancel the transaction","Cancel Transaction",buttons);

                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void htmlresult(String result) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = PaymentGateWayBO.validatePayUResponse();
        params.put("volley", result);
        connectionVO.setParams(params);
        Log.w("request",params.toString());

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                PayUVO payUVO = gson.fromJson(response.toString(), PayUVO.class);
                if (!payUVO.getStatusCode().equals("200")) {
                    Utility.showSingleButtonDialogconfirmation(PaymentGateWay.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        ok.dismiss();
                        finish();
                    }),"Alert", payUVO.getErrorMsgs().get(0));
                } else {
                    Log.w("oxiMobileRecharge", payUVO.toString());
                    if(payUVO.getOperatorTxnID()==null || getIntent().getStringExtra("oxigenTypeId")==null || getIntent().getStringExtra("oxigenTypeId").equals("") ){
                        Utility.showSingleButtonDialogconfirmation(PaymentGateWay.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                            finish();
                        }),"","Something went wrong, Please try again!");
                    }else {

                        Intent intent =new Intent();
                        intent.putExtra("tnxid",payUVO.getOperatorTxnID());
                        intent.putExtra("oxigenTypeId",getIntent().getStringExtra("oxigenTypeId"));
                        intent.putExtra("status",payUVO.getCcTransactionStatus().getStatusId());
                        intent.putExtra("message",payUVO.getAnonymousString());
                        setResult(RESULT_OK,intent);
                        finish();

                    }
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        webView.stopLoading();
    }

    public class MyBrowser extends WebViewClient {
        final ProgressDialog progressBar = ProgressDialog.show(PaymentGateWay.this, null, " Please wait...", false, false);

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.w("URL", url);
            view.loadUrl(url);
            return true;
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.w("pagestart", url);
            if(!PaymentGateWay.this.isFinishing()){
                //show dialog
                progressBar.show();
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            progressBar.dismiss();
            Log.w("loadurlresp", url);
            try{
                if(url.equals(redirectUrl)){
                    webView.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('siresp').innerHTML);");
                }else if(url.equals(cancelUrl)){
                    webView.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('siresp').innerHTML);");
                }
            }catch (Exception e){

            }

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


    private void showError(String description) {

       /* new AlertDialog.Builder(SI_First_Data.this)
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
        Log.e("weverrir", description);
    }
}



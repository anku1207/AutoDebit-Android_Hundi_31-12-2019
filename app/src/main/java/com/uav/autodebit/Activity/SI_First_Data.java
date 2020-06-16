package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.webkit.JavascriptInterface;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;
import com.uav.autodebit.BO.PanCardBO;
import com.uav.autodebit.BO.SiBO;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;

import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;

import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.HashMap;


public class SI_First_Data extends Base_Activity implements MyJavaScriptInterface.javascriptinterface {
    WebView webview;
    JSONObject respjson;

    String redirectUrl, cancelUrl;

    TextView text1, text2, text3;
    Button continuebtn;
    LinearLayout orderlayout;
    boolean foractivity;
    int actionId;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_si__first__data);
        getSupportActionBar().hide();
        webview = findViewById(R.id.webview);

        ImageView rof_backbutton = findViewById(R.id.back_activity_button);

        rof_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    finish();
            }
        });

        foractivity=getIntent().getBooleanExtra("forresutl",true);//success finish activity
        actionId=getIntent().getIntExtra("id",0);

        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        orderlayout = findViewById(R.id.orderlayout);
        continuebtn = findViewById(R.id.continuebtn);

        sifirstdata();
        //openWebView("file:///android_asset/sifirst.html");

        continuebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!foractivity){
                    Intent newIntent = new Intent(SI_First_Data.this, Home.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(newIntent);
                }else{
                    Intent intent =new Intent();
                    setResult(RESULT_OK,intent);
                    intent.putExtra("actionId",actionId);
                    finish();
                }

            }
        });


    }

    public void sifirstdata() {
        VolleyUtils.makeJsonObjectRequest(this, SiBO.getSIMandateProperties(), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;


                if (!response.getString("status").equals("200")) {
                    Utility.alertDialog(SI_First_Data.this, "Alert", response.getString("errorMsg"), "Ok");
                } else {

                    if (ApplicationConstant.SI_SERVICE.equals("hdfc")) {
                        respjson = response.getJSONObject("consumerData");
                        Log.w("resp", respjson.toString());

                        Checkout checkout = new Checkout();
                        checkout.setMerchantIdentifier(respjson.getString("merchantId"));
                        checkout.setTransactionIdentifier("txnId");
                        checkout.setTransactionReference("");
                        checkout.setTransactionType(PaymentActivity.TRANSACTION_TYPE_SALE);
                        checkout.setTransactionSubType(PaymentActivity.TRANSACTION_SUBTYPE_DEBIT);
                        checkout.setTransactionCurrency(respjson.getString("currency"));
                        JSONArray jsonArray = respjson.getJSONArray("items");
                        JSONObject object = jsonArray.getJSONObject(0);
                        checkout.setTransactionAmount(object.getString("amount"));
                        checkout.addCartItem(object.getString("itemId"), object.getString("amount"), "ProductSurchargeOrDiscountAmount", object.getString("comAmt"), "ProductSKU", "ProductReference", "ProductDescriptor", "ProductProviderID");
                        checkout.setTransactionDateTime("");
                        checkout.setConsumerIdentifier(respjson.getString("consumerId"));
                        checkout.setConsumerEmailID(respjson.getString("consumerEmailId"));
                        checkout.setConsumerMobileNumber(respjson.getString("consumerMobileNo"));
                        checkout.setConsumerMobileNumber(respjson.getString("consumerMobileNo"));
                        checkout.setTransactionAmount(respjson.getString("consumerId"));
                        checkout.setPaymentInstructionAction("Y");
                        checkout.setPaymentInstructionType(respjson.getString("amountType"));
                        checkout.setPaymentInstructionLimit(respjson.getString("maxAmount"));
                        checkout.setPaymentInstructionFrequency(respjson.getString("frequency"));
                        checkout.setPaymentInstructionStartDateTime(respjson.getString("debitStartDate"));
                        checkout.setPaymentInstructionEndDateTime(respjson.getString("debitEndDate"));
                        Intent authIntent = new Intent(SI_First_Data.this, PaymentModesActivity.class);
                        // Checkout Object
                        Log.d("Checkout Request Object",
                                checkout.getMerchantRequestPayload().toString());
                        authIntent.putExtra(Constant.ARGUMENT_DATA_CHECKOUT,
                                checkout);
                        // Public Key
                        authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY,
                                "1234-6666-6789-56");
                        // Requested Payment Mode
                        authIntent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,
                                PaymentActivity.PAYMENT_METHOD_DEFAULT);
                        startActivityForResult(authIntent, PaymentActivity.REQUEST_CODE);

                        //openWebView("file:///android_asset/sItechprocessHDFC.html");
                    } else if (ApplicationConstant.SI_SERVICE.equals("icici")) {
                        respjson = response;
                        Log.w("resp", respjson.toString());
                        openWebView("file:///android_asset/sifirst.html");
                    } else if (ApplicationConstant.SI_SERVICE.equals("avenue")   || ApplicationConstant.SI_SERVICE.equals("autopepg")) {
                        respjson = response;
                        Log.w("resp", respjson.toString());

                        redirectUrl = respjson.getString("redirectUrl");
                        cancelUrl = respjson.getString("cancelUrl");
                        String url = respjson.getString("url") + "?customerId=" + Session.getCustomerId(SI_First_Data.this) + "&entityTypeId=2" + "&versioncode="+ Utility.getVersioncode(SI_First_Data.this);
                        openWebView(url);
                    }

                }
            }
        });

    }

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    void openWebView(final String receiptUrl) {


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

                WebView newWebView = new WebView(SI_First_Data.this);
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

        try {
            JSONObject object = new JSONObject(result);
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = SiBO.proceedAutoPePgResponse();

            if(ApplicationConstant.SI_SERVICE.equals("autopepg")){

                CustomerVO customerVO = new CustomerVO();
                String anonymousString = object.getString("anonymousString");
                String anonymousInteger = object.getString("anonymousInteger");

                customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(SI_First_Data.this)));
                customerVO.setAnonymousString(anonymousString);
                customerVO.setAnonymousInteger(Integer.parseInt(anonymousInteger));

                Gson gson = new Gson();
                String json = gson.toJson(customerVO);
                params.put("volley", json);

                connectionVO.setParams(params);

            }else if(ApplicationConstant.SI_SERVICE.equals("avenue")){
                CustomerVO customerVO = new CustomerVO();
                String anonymousString = object.getString("anonymousString");
                String anonymousInteger = object.getString("anonymousInteger");

                customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(SI_First_Data.this)));
                customerVO.setAnonymousString(anonymousString);
                customerVO.setAnonymousInteger(Integer.parseInt(anonymousInteger));

                Gson gson = new Gson();
                String json = gson.toJson(customerVO);
                params.put("volley", json);

                connectionVO.setParams(params);
            }

            Log.w("htmlresultRequest",connectionVO.getParams().toString());



            VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }

                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Gson gson = new Gson();
                    CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);

                    if (!customerVO.getStatusCode().equals("200")) {
                        showSingleButtonDialog(SI_First_Data.this, "Alert", customerVO.getErrorMsgs().get(0));
                    } else {
                        DecimalFormat df = new DecimalFormat();
                        df.setMinimumFractionDigits(2);
                        JSONObject orderreap = new JSONObject(customerVO.getAnonymousString());
                        webview.setVisibility(View.GONE);
                        orderlayout.setVisibility(View.VISIBLE);
                        text2.setText(orderreap.getString("txnId"));
                        text3.setText(df.format(Double.parseDouble(orderreap.getString("orderAmount"))));
                        continuebtn.setVisibility(View.VISIBLE);
                        String json = gson.toJson(customerVO);
                        Session.set_Data_Sharedprefence(SI_First_Data.this, Session.CACHE_CUSTOMER, json);
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(SI_First_Data.this , Utility.getStackTrace(e));
        }
    }


    public void showSingleButtonDialog(final Context var1, String error, String var2) {
        final Dialog var3 = new Dialog(var1);
        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(var1.getResources().getIdentifier("singlebuttondialog", "layout", var1.getPackageName()));
        var3.setCanceledOnTouchOutside(false);
        TextView var4 = (TextView) var3.findViewById(var1.getResources().getIdentifier("dialog_one_tv_title", "id", var1.getPackageName()));
        var4.setText(error);
        TextView var6 = (TextView) var3.findViewById(var1.getResources().getIdentifier("dialog_one_tv_text", "id", var1.getPackageName()));

        var6.setText(var2);
        Button var5 = (Button) var3.findViewById(var1.getResources().getIdentifier("dialog_one_btn", "id", var1.getPackageName()));
        var5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                var3.dismiss();
                if(!foractivity){
                    Intent newIntent = new Intent(SI_First_Data.this, Home.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(newIntent);
                }else{
                    finish();
                }
            }
        });
        var3.show();
    }


    private class MyBrowser extends WebViewClient {
        final ProgressDialog progressBar = ProgressDialog.show(SI_First_Data.this, null, " Please wait...", false, false);

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

            if (ApplicationConstant.SI_SERVICE.equals("avenue")) {
                if (url.equals(redirectUrl + "app") || url.equals(cancelUrl + "app")) {
                    webview.setVisibility(View.GONE);
                }
            }else if(ApplicationConstant.SI_SERVICE.equals("autopepg")){
                if (url.equals(redirectUrl + "app") || url.equals(cancelUrl + "app")) {
                    webview.setVisibility(View.GONE);
                }
            }
            if(progressBar!=null && !progressBar.isShowing())  progressBar.show();

        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (progressBar!=null && progressBar.isShowing()) {
                progressBar.dismiss();
            }
            Log.w("loadurlresp", url);
            if (ApplicationConstant.SI_SERVICE.equals("icici")) {
                if (url.equals("file:///android_asset/sifirst.html")) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        view.evaluateJavascript("loadParam('" + respjson.toString() + "')", null);
                    } else {
                        view.loadUrl("loadParam('" + respjson.toString() + "')");
                    }
                }
            } else if (ApplicationConstant.SI_SERVICE.equals("avenue")) {
                if (url.equals(redirectUrl + "app") || url.equals(cancelUrl + "app")) {
                    webview.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('siresp').innerHTML);");
                    webview.loadUrl("javascript:console.log('MAGIC'+document.getElementById('siresp').innerHTML);");
                }
            }else if(ApplicationConstant.SI_SERVICE.equals("autopepg")){
                if (url.equals(redirectUrl + "app") || url.equals(cancelUrl + "app")) {
                    webview.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('siresp').innerHTML);");
                    webview.loadUrl("javascript:console.log('MAGIC'+document.getElementById('siresp').innerHTML);");
                }
            }
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            if (progressBar!=null && progressBar.isShowing()) {
                progressBar.dismiss();
            }
            showError(description);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            if (progressBar!=null && progressBar.isShowing()) {
                progressBar.dismiss();
            }
            showError((String) error.getDescription());
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            if (progressBar!=null && progressBar.isShowing()) {
                progressBar.dismiss();
            }
            showError(errorResponse.getReasonPhrase().toString());
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

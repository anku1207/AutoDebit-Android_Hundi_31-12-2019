package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
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
import android.view.Gravity;
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
import com.uav.autodebit.R;

import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DialogInterface;
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


public class SI_First_Data extends Base_Activity implements MyJavaScriptInterface.javascriptinterface {
    WebView webview ,newWebView;
    JSONObject respjson;

    String redirectUrl, cancelUrl,paymentType,serviceId;

    TextView text1, text2, text3;
    Button continuebtn;
    LinearLayout orderlayout;
    boolean foractivity;
    int actionId;
    double amount;
    CustomerVO htmlRequestResp;
    ProgressDialog progressBar;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_si__first__data);
        getSupportActionBar().hide();
        webview = findViewById(R.id.webview);

        htmlRequestResp=new CustomerVO();
        progressBar = ProgressDialog.show(SI_First_Data.this, null, " Please wait...", false, false);

        ImageView rof_backbutton = findViewById(R.id.back_activity_button);
        rof_backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelTransaction();
            }
        });

        foractivity=getIntent().getBooleanExtra("forresutl",true);//success finish activity
        actionId=getIntent().getIntExtra("id",0);
        amount=getIntent().getDoubleExtra("amount",1.00);
        serviceId=getIntent().getStringExtra("serviceId");
        paymentType=getIntent().getStringExtra("paymentType");

        Log.w("getIntentResult",foractivity+"="+actionId+"="+amount+"="+serviceId+"="+paymentType);


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
                    //server response get customer auth service id
                    intent.putExtra("mandateId",htmlRequestResp.getAnonymousInteger());
                    finish();
                }

            }
        });


    }

    private void cancelTransaction() {
        String[] buttons = {"No","Yes"};
        Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
            @Override
            public void confirm(Dialog dialog) {
                Utility.dismissDialog(SI_First_Data.this, dialog);
                finish();
            }
            @Override
            public void modify(Dialog dialog) {
                Utility.dismissDialog(SI_First_Data.this, dialog);
            }
        },SI_First_Data.this,"Do you want to cancel the transaction","Cancel Transaction",buttons);
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
                    Utility.alertDialog(SI_First_Data.this, "Error !", response.getString("errorMsg"), "Ok");
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
                    } else if (ApplicationConstant.SI_SERVICE.equals("avenue")   ||
                            ApplicationConstant.SI_SERVICE.equals("autopepg")) {
                        respjson = response;
                        Log.w("resp", respjson.toString());

                        redirectUrl = respjson.getString("redirectUrl");
                        cancelUrl = respjson.getString("cancelUrl");
                        String url;
                        if(actionId!=0){
                            url = respjson.getString("url") + "?customerId=" + Session.getCustomerId(SI_First_Data.this) + "&entityTransactionId="+actionId + "&versioncode="+ Utility.getVersioncode(SI_First_Data.this)+ "&Amount="+amount + "&serviceId="+serviceId + "&paymentType="+paymentType;
                        }else {
                            url = respjson.getString("url") + "?customerId=" + Session.getCustomerId(SI_First_Data.this) + "&versioncode="+ Utility.getVersioncode(SI_First_Data.this)+ "&Amount="+amount + "&serviceId="+serviceId + "&paymentType="+paymentType;
                        }
                        openWebView(url);
                    }

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

        webview.getSettings().setBuiltInZoomControls(false);
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
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if(!SI_First_Data.this.isFinishing() &&  progressBar!=null && !progressBar.isShowing()){
                    try {
                        progressBar.show();
                    }catch (Exception e){
                    }
                }
                if(newProgress==100){
                    Utility.dismissDialog(SI_First_Data.this, progressBar);
                }
            }

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

                newWebView = new WebView(SI_First_Data.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.setInitialScale(1);
                newWebView.getSettings().setUseWideViewPort(true);
                newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                newWebView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onCloseWindow(WebView window) {
                        super.onCloseWindow(window);
                        if (newWebView !=null){
                            try {
                                webview.removeView(newWebView);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
                newWebView.setWebViewClient(new WebViewClient());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,  LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity= Gravity.CENTER;
                newWebView.setLayoutParams(params);

                webview.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {
                     @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                         try {
                             view.loadUrl(url);
                             return true;
                         } catch (Exception e) {
                             return true;
                         }
                     }
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.w("pagestart", url);
                        if(!SI_First_Data.this.isFinishing() &&  progressBar!=null && !progressBar.isShowing()){
                            try {
                                progressBar.show();
                            }catch (Exception e){
                            }
                        }
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.w("newWebcie", url);
                        Utility.dismissDialog(SI_First_Data.this, progressBar);
                        try {
                            if(webview!=null){
                                webview.scrollTo(0,0);
                            }
                        }catch (Exception e){
                            ExceptionsNotification.ExceptionHandling(SI_First_Data.this , Utility.getStackTrace(e),"0");
                        }

                    }
                    @SuppressWarnings("deprecation")
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        showError(description,progressBar);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        showError((String) error.getDescription(),progressBar);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        showError(errorResponse.getReasonPhrase().toString(),progressBar);
                    }

                });
                return true;
            }
            @Override
            public void onCloseWindow(WebView window) {
                super.onCloseWindow(window);
                try {
                    newWebView.destroy();
                } catch (Exception e) {
                    ExceptionsNotification.ExceptionHandling(SI_First_Data.this , Utility.getStackTrace(e),"0");
                }
                Utility.dismissDialog(SI_First_Data.this, progressBar);
            }
        });
        webview.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        webview.loadUrl(receiptUrl); //receiptUrl
    }

    private void showError(String description,Dialog progressBar) {
        Utility.dismissDialog(SI_First_Data.this, progressBar);
        try {
            if(webview!=null){
                webview.scrollTo(0,0);
            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(SI_First_Data.this , Utility.getStackTrace(e),"0");
        }
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

                CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                String anonymousString = object.getString("anonymousString");//json SI response
                String anonymousInteger = object.getString("anonymousInteger");


                CustomerVO customerVO = new CustomerVO();
                customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(SI_First_Data.this)));
                customerAuthServiceVO.setCustomer(customerVO);
                customerAuthServiceVO.setAnonymousString(anonymousString);
                customerAuthServiceVO.setAnonymousInteger(Integer.parseInt(anonymousInteger));

                Gson gson = new Gson();
                String json = gson.toJson(customerAuthServiceVO);
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
                    Log.w("htmlresult_resp",response.toString());
                    Gson gson = new Gson();
                    htmlRequestResp = gson.fromJson(response.toString(), CustomerVO.class);

                    if (!htmlRequestResp.getStatusCode().equals("200")) {
                        showSingleButtonDialog(SI_First_Data.this, "Alert", htmlRequestResp.getErrorMsgs().get(0));
                    } else {
                        if(htmlRequestResp.getAnonymousInteger()==null){
                            showSingleButtonDialog(SI_First_Data.this, "Alert", Content_Message.error_message);
                        }else{

                            if(htmlRequestResp.getTokenId().equalsIgnoreCase(ApplicationConstant.PG_MANDATE)){
                                DecimalFormat df = new DecimalFormat();
                                df.setMinimumFractionDigits(2);
                                JSONObject orderreap = new JSONObject(htmlRequestResp.getAnonymousString());
                                webview.setVisibility(View.GONE);
                                orderlayout.setVisibility(View.VISIBLE);
                                text2.setText(orderreap.getString("txnId"));
                                text3.setText(df.format(Double.parseDouble(orderreap.getString("orderAmount"))));
                                continuebtn.setVisibility(View.VISIBLE);
                            }else{
                                Intent intent =new Intent();
                                setResult(RESULT_OK,intent);
                                intent.putExtra("actionId",actionId);
                                //server response get customer auth service id
                                intent.putExtra("mandateId",htmlRequestResp.getAnonymousInteger());
                                finish();
                            }
                        }
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(SI_First_Data.this , Utility.getStackTrace(e));
        }
    }


    public void showSingleButtonDialog(Context var1, String error, String var2) {
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
                Utility.dismissDialog(SI_First_Data.this, var3);
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
        if(!((Activity)var1).isFinishing() && !var3.isShowing())  var3.show();
    }


    private class MyBrowser extends WebViewClient {


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
                if (url.equals(redirectUrl + "app/") || url.equals(cancelUrl + "app/")) {
                    webview.setVisibility(View.GONE);
                }
            }
            if(!SI_First_Data.this.isFinishing() &&  progressBar!=null && !progressBar.isShowing()){
                try {
                    progressBar.show();
                }catch (Exception e){

                }
            }
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            Utility.dismissDialog(SI_First_Data.this, progressBar);
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
                if (url.equals(redirectUrl + "app/") || url.equals(cancelUrl + "app/")) {
                    webview.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('siresp').innerHTML);");
                    webview.loadUrl("javascript:console.log('MAGIC'+document.getElementById('siresp').innerHTML);");
                }
            }
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            showError(description,progressBar);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            showError((String) error.getDescription(),progressBar);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            showError(errorResponse.getReasonPhrase().toString(),progressBar);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    cancelTransaction();
                     return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

}

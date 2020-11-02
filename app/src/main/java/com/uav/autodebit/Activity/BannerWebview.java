package com.uav.autodebit.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.DownloadListener;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.BannerBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.BannerVO;
import com.uav.autodebit.vo.BaseVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerNotificationVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BannerWebview extends Base_Activity implements View.OnClickListener, MyJavaScriptInterface.javascriptinterface {
    ImageView back_activity_button;
    WebView webView, newWebView;
    ProgressDialog progressBar;
    TextView title;
    JSONObject intentWebviewData;
    int bannerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banner_webview);
        getSupportActionBar().hide();

        progressBar = ProgressDialog.show(BannerWebview.this, null, " Please wait...", false, false);

        back_activity_button = findViewById(R.id.back_activity_button);
        title = findViewById(R.id.title);
        back_activity_button.setOnClickListener(this);
        try {

            String notificationData =getIntent().getStringExtra(ApplicationConstant.NOTIFICATION_ACTION);
            if(notificationData!=null){
                JSONObject jsonObject = new JSONObject(notificationData);
                if(jsonObject.has("value") &&   jsonObject.isNull("value")){
                    Utility.showSingleButtonDialog(this,"Error !", Content_Message.error_message,true);
                }else{
                    BannerWebviewAPI.getBannerClickDetails(BannerWebview.this,jsonObject
                            .getInt("value"),new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                        BannerVO bannerVO1 = (BannerVO) s;
                        try {
                            bannerId=jsonObject.getInt("value");
                            String data = bannerVO1.getWebview();
                            if (data != null) {
                                intentWebviewData = new JSONObject(data);
                                title.setText(intentWebviewData.getString("title"));
                                openWebView(intentWebviewData.getString("url") + "?user=" + Session.getCustomerId(this) + "&source=app");
                            } else {
                                Toast.makeText(this, Content_Message.error_message, Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            ExceptionsNotification.ExceptionHandling(BannerWebview.this, Utility.getStackTrace(e));
                        }
                    }));
                }
            }else {
                bannerId=getIntent().getIntExtra("bannerId",0);
                String data = getIntent().getStringExtra("webviewdata");
                if (data != null) {
                    intentWebviewData = new JSONObject(data);
                    title.setText(intentWebviewData.getString("title"));
                    openWebView(intentWebviewData.getString("url") + "?user=" + Session.getCustomerId(this) + "&source=app");
                } else {
                    Toast.makeText(this, Content_Message.error_message, Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(BannerWebview.this, Utility.getStackTrace(e));
        }
    }



    @SuppressLint("SetJavaScriptEnabled")
    void openWebView(final String receiptUrl) {
        Log.w("callUrl", receiptUrl);
        webView=findViewById(R.id.webView);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(new MyBrowser());

        // webview.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.setInitialScale(1);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMinimumFontSize(16);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }
        settings.setDomStorageEnabled(true);

        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (!BannerWebview.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                    try {
                        progressBar.show();
                    } catch (Exception e) {

                    }
                }
                if (newProgress == 100) {
                    Utility.dismissDialog(BannerWebview.this, progressBar);
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
                newWebView = new WebView(BannerWebview.this);
                newWebView.getSettings().setJavaScriptEnabled(true);
                newWebView.getSettings().setBuiltInZoomControls(true);
                newWebView.setInitialScale(1);
                newWebView.getSettings().setUseWideViewPort(true);
                newWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                //for download PDF files
                newWebView.setDownloadListener(new MyDownLoadListener());


                newWebView.setWebChromeClient(new WebChromeClient() {

                    @Override
                    public void onCloseWindow(WebView window) {
                        super.onCloseWindow(window);
                        if (newWebView != null) {
                            try {
                                webView.removeView(newWebView);
                            } catch (Exception e) {
                            }
                        }
                    }
                });
                newWebView.setWebViewClient(new WebViewClient());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                params.gravity = Gravity.CENTER;
                newWebView.setLayoutParams(params);

                webView.addView(newWebView);
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                newWebView.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        try {
                            if (url.contains(".pdf")) {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(url), "application/pdf");
                                try {
                                    if (newWebView != null) {
                                        try {
                                            webView.removeView(newWebView);
                                        } catch (Exception e) {
                                        }
                                    }
                                    view.getContext().startActivity(intent);
                                } catch (ActivityNotFoundException e) {
                                    //user does not have a pdf viewer installed
                                }
                            } else {
                                view.loadUrl(url);
                            }
                            return true;
                        } catch (Exception e) {
                            return true;
                        }
                    }
                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        Log.w("pagestart", url);
                        if (!BannerWebview.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                            try {
                                progressBar.show();
                            } catch (Exception e) {
                            }
                        }
                    }
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        Log.w("newWebcie", url);
                        Utility.dismissDialog(BannerWebview.this, progressBar);
                        try {
                            if (webView != null) {
                                webView.scrollTo(0, 0);
                            }
                        } catch (Exception e) {
                            ExceptionsNotification.ExceptionHandling(BannerWebview.this, Utility.getStackTrace(e), "0");
                        }

                    }

                    @SuppressWarnings("deprecation")
                    public void onReceivedError(WebView view, int errorCode,
                                                String description, String failingUrl) {
                        showError(description, progressBar);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                        showError((String) error.getDescription(), progressBar);
                    }

                    @TargetApi(android.os.Build.VERSION_CODES.M)
                    public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                        showError(errorResponse.getReasonPhrase().toString(), progressBar);
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
                    ExceptionsNotification.ExceptionHandling(BannerWebview.this, Utility.getStackTrace(e), "0");
                }
                Utility.dismissDialog(BannerWebview.this, progressBar);
            }
        });
        //for download PDF files
        webView.setDownloadListener(new MyDownLoadListener());

        webView.setWebViewClient(new BannerWebview.MyBrowser());
        webView.addJavascriptInterface(new MyJavaScriptInterface(this), "HTMLOUT");
        webView.loadUrl(receiptUrl); //receiptUrl

    }

    @Override
    public void htmlresult(String result) {
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = BannerBO.setAddCampaignResponse();

            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(BannerWebview.this)));
            customerVO.setAnonymousString(result);
            customerVO.setAnonymousInteger(bannerId);
            Gson gson =new Gson();
            String json = gson.toJson(customerVO);
            params.put("volley", json);

            Log.w("htmlresultRequest", json);
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(BannerWebview.this, connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    CustomerVO customerVOresp = gson.fromJson(response.toString(), CustomerVO.class);
                    if(customerVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) customerVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(BannerWebview.this,customerVOresp.getDialogTitle(),customerVOresp.getErrorMsgs().get(0),true);
                    }else {
                        try {
                            //override Local Cache
                            CustomerCacheUpdate.updateLocalCache(BannerWebview.this,customerVOresp.getLocalCache());
                            if (intentWebviewData.has("callBackActivity")) {
                                Class<?> clazz = Class.forName(getApplicationContext().getPackageName() + ".Activity." + intentWebviewData.getString("callBackActivity"));
                                Intent intent = new Intent(getApplicationContext(), clazz);
                                startActivity(intent);
                                finish();
                            } else {
                                finish();
                            }
                        }catch (Exception e){
                            ExceptionsNotification.ExceptionHandling(BannerWebview.this , Utility.getStackTrace(e));
                        }
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(BannerWebview.this , Utility.getStackTrace(e));
        }


    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Log.w("URL", url);

            if (url.contains(".pdf")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/pdf");
                try {
                    if (newWebView != null) {
                        try {
                            webView.removeView(newWebView);
                        } catch (Exception e) {
                        }
                    }
                    view.getContext().startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    //user does not have a pdf viewer installed
                }
            } else {
                view.loadUrl(url);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            if (!BannerWebview.this.isFinishing() && progressBar != null && !progressBar.isShowing()) {
                try {
                    progressBar.show();
                } catch (Exception e) {
                    Toast.makeText(BannerWebview.this, Content_Message.error_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            Utility.dismissDialog(BannerWebview.this, progressBar);
            try {
                if (url.equalsIgnoreCase(intentWebviewData.getString("successUrl"))) {
                    webView.setVisibility(View.GONE);
                    webView.loadUrl("javascript:HTMLOUT.showHTML(document.getElementById('addresp').innerHTML);");
                    webView.loadUrl("javascript:console.log('MAGIC'+document.getElementById('addresp').innerHTML);");
                }
            } catch (Exception e) {
                ExceptionsNotification.ExceptionHandling(BannerWebview.this, Utility.getStackTrace(e));
            }
        }

        @SuppressWarnings("deprecation")
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            showError(description, progressBar);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            showError((String) error.getDescription(), progressBar);
        }

        @TargetApi(android.os.Build.VERSION_CODES.M)
        public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
            showError(errorResponse.getReasonPhrase().toString(), progressBar);
        }
    }

    private void showError(String description, Dialog progressBar) {
        Utility.dismissDialog(BannerWebview.this, progressBar);
        try {
            if (webView != null) {
                webView.scrollTo(0, 0);
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(BannerWebview.this, Utility.getStackTrace(e), "0");
        }
        Log.e("weverrir", description);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back_activity_button:
                if (webView.canGoBack()) {
                    webView.goBack();
                } else {
                    finish();
                }
                break;
        }
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

    public class MyDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {

            if (url != null) {
                if (newWebView != null) {
                    try {
                        webView.removeView(newWebView);
                    } catch (Exception e) {
                    }
                }

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }

        }

    }
}
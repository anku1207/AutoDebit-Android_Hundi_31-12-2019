package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import android.os.Bundle;
import android.util.Log;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.google.gson.Gson;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.BO.PanCardBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.DownloadTask;
import com.uav.autodebit.util.FileDownloadInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Credit_Score_Report extends Base_Activity implements FileDownloadInterface, PermissionUtils.PermissionResultCallback , ActivityCompat.OnRequestPermissionsResultCallback {
    Context context;
    TextView creditreportbtn;
    Button proceed;
    String customername;
    TextView activitytext;
    ImageView back_activity_button1;
    PermissionUtils permissionUtils;
    String creditScoreFileUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit__score__report);

        getSupportActionBar().hide();

        context=Credit_Score_Report.this;
        permissionUtils=new PermissionUtils(Credit_Score_Report.this);


        /*Intent intent =getIntent();
        customername=intent.getStringExtra("customername");
        */
        Gson gson = new Gson();
        CustomerVO customerVO = gson.fromJson( Session.getSessionByKey(this, Session.CACHE_CUSTOMER), CustomerVO.class);
        customername = customerVO.getPanHolderName();

        getcreditscore();

    }

    public void getcreditscore(){

            VolleyUtils.makeJsonObjectRequest(this,PanCardBO.getCreditScore(Integer.parseInt(Session.getCustomerId(context))), new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);


                    if(customerVO.getStatusCode().equals("400")){
                        //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                        ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.alertDialog(context,customerVO.getDialogTitle(),sb.toString(),"Ok");

                    }else if (customerVO.getStatusCode().equals("440")){

                        Intent  intent =new Intent(Credit_Score_Report.this,Credit_Score.class);
                        intent.putStringArrayListExtra("value", (ArrayList<String>) customerVO.getErrorMsgs());
                        intent.putExtra("msg",customerVO.getAnonymousString());
                        intent.setAction("Mobilenomatch");
                        startActivity(intent);
                        finish();
                    }else if(customerVO.getStatusCode().equals("441")){
                        Intent  intent =new Intent(Credit_Score_Report.this,Credit_Score.class);
                        intent.putExtra("msg",customerVO.getAnonymousString());
                        intent.setAction("Customernotfound");
                        startActivity(intent);
                        finish();
                    }else {
                        String json = gson.toJson(customerVO);
                        Session.set_Data_Sharedprefence(context,Session.CACHE_CUSTOMER,json);
                        String[] result = customerVO.getAnonymousString().split("\\|");
                        openWebView(result[1],result[0]); //http://d.eze.cc/r/o/0zfATRE7

                        Log.w("url",customerVO.getAnonymousString());

                    }
                }
            });
    }


    @SuppressLint("SetJavaScriptEnabled")
    void openWebView(final String receiptUrl, final String fileurl) {

        setContentView(R.layout.activity_credit__score__report);
        final WebView webView = (WebView) findViewById(R.id.gaugewebview);
        //webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);

        webView.getSettings().setAppCacheEnabled(false);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);


        webView.setInitialScale(1);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setMinimumFontSize(16);
        settings.setDomStorageEnabled(true);


        final ProgressDialog progressBar = ProgressDialog.show(context, null, "Loading Score. Please wait...", false, false);
        webView.setDrawingCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            WebView.enableSlowWholeDocumentDraw();
        }

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                android.util.Log.d("WebView", consoleMessage.message());
                return true;
            }


        });


        webView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                return (event.getAction() == MotionEvent.ACTION_MOVE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                Utility.dismissDialog(Credit_Score_Report.this,progressBar);

                if(!url.equalsIgnoreCase(receiptUrl)) {
                    showError("Not a valid URL");
                }
            }

            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                Utility.dismissDialog(Credit_Score_Report.this,progressBar);

                showError(description);
            }
            @TargetApi(android.os.Build.VERSION_CODES.M)
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Utility.dismissDialog(Credit_Score_Report.this,progressBar);

                showError((String) error.getDescription());
            }

            @TargetApi(android.os.Build.VERSION_CODES.M)
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                Utility.dismissDialog(Credit_Score_Report.this,progressBar);

                showError(errorResponse.getReasonPhrase().toString());
            }
        });
        webView.loadUrl(receiptUrl); //receiptUrl
        creditreportbtn=findViewById(R.id.creditreportbtn);
        proceed=findViewById(R.id.proceed);
        activitytext=findViewById(R.id.activitytext);
        back_activity_button1=findViewById(R.id.back_activity_button1);

        creditreportbtn.setVisibility(View.VISIBLE);

        if(fileurl.equals("")){
            creditreportbtn.setText("Credit history not available");
        }
        proceed.setVisibility(View.VISIBLE);
        activitytext.setText(customername+", here's your latest Credit Score");


        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  startActivity(new Intent(Credit_Score_Report.this,Home.class));
                finish();*/
                try {
                    setCustomerBucket();
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(Credit_Score_Report.this , Utility.getStackTrace(e));
                }
            }
        });

        creditreportbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(fileurl.equals("")) return;
                creditScoreFileUrl=fileurl;
                permissionUtils.check_permission(PermissionHandler.fileDownloadAndReadPermissionArrayList(Credit_Score_Report.this), Content_Message.DOWNLOAD_PERMISSION, ApplicationConstant.REQ_DOWNLOAD_PERMISSION);
            }
        });
        back_activity_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void setCustomerBucket() throws Exception{
        Gson gson =new Gson();

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = CustomerBO.setCustomerBucket();

        CustomerVO customerVO=new CustomerVO();

        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Credit_Score_Report.this)));

        String json =gson.toJson(customerVO);

        params.put("volley", json);

        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerVO.getStatusCode().equals("400")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Credit_Score_Report.this,"Error !",sb.toString(),false);
                }else {
                    String json = gson.toJson(customerVO);
                    Session.set_Data_Sharedprefence(Credit_Score_Report.this,Session.CACHE_CUSTOMER,json);

                    startActivity(new Intent(Credit_Score_Report.this,Home.class));
                    finish();
                }
            }
        });
    }

    private void showError(String description) {
      /*  new AlertDialog.Builder(Credit_Score_Report.this)
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
                Log.w("error",description);

    }

    @Override
    public void downloadComplete(File path) {
        try {
            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", path);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);

            }else{
                Uri apkUri = Uri.fromFile(path);
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(apkUri,"application/pdf");
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(install);
            }

        }catch (Exception e){
            Log.w("error",e.getMessage());
        }
    }

    @Override
    public void error(final String error) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               Utility.showSingleButtonDialog(context,"Error !",error,false);
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    @Override
    public void PermissionGranted(int request_code) {
        if(request_code==ApplicationConstant.REQ_DOWNLOAD_PERMISSION){
            new DownloadTask(Credit_Score_Report.this,Credit_Score_Report.this, creditScoreFileUrl);
        }
    }
    @Override
    public void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions) {
    }

    @Override
    public void PermissionDenied(int request_code) {
    }

    @Override
    public void NeverAskAgain(int request_code) {
    }
}

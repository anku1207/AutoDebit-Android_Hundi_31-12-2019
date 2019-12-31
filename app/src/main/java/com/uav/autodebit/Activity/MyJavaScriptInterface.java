package com.uav.autodebit.Activity;

import android.app.Activity;
import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class MyJavaScriptInterface {
    private Context ctx;
    private  javascriptinterface javascriptinterface;
    MyJavaScriptInterface(Context ctx) {
        this.ctx = ctx;
        this.javascriptinterface= (javascriptinterface)ctx;

    }

    @JavascriptInterface
    public void showHTML(String html) {
        String htmlString = html;
        javascriptinterface.htmlresult(html);
    }

    interface javascriptinterface{

        void htmlresult(String result);

    }
}

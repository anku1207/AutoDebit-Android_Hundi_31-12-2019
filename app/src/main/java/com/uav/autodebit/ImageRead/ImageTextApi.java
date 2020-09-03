package com.uav.autodebit.ImageRead;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.gson.Gson;
import com.uav.autodebit.Activity.AddOldDmrcCardAutoPe;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.TextReadBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ImageTextApi {


    ImageTextViewInterface ic;
    Context context;
    Bitmap bitmap;


    public ImageTextApi(final ImageTextViewInterface ic, final Context context , final Bitmap bitmap){
       this.ic=ic;
       this.context=context;
       this.bitmap=bitmap;
    }


    public void getImageReadTextAPI(){

        new Thread(new Runnable(){
            public void run() {
                try {
                    // some calculation
                    TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
                    Frame frame = new Frame.Builder().setBitmap(bitmap).build();
                    SparseArray<TextBlock> textBlocks = textRecognizer.detect(frame);
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int index = 0; index < textBlocks.size(); index++) {
                        //extract scanned text blocks here
                        TextBlock item = textBlocks.valueAt(index);
                        stringBuilder.append(item.getValue());
                        stringBuilder.append("\n");
                    }
                    Log.w("imageReadText",stringBuilder.toString());
                  //  String value=stringBuilder.toString().replaceAll(" ", "");
                  /*  BufferedReader bufReader = new BufferedReader(new StringReader(value));
                    String line=null;
                    while( (line=bufReader.readLine()) != null ){
                        Log.w("readline",line);
                            if(Utility.isNumeric(line) && line.length()>=8){
                                ic.onResult(line);
                                break;
                            }
                    }*/
                    ic.onResult(stringBuilder.toString());
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                }
            }
        }).start();

    }

    public static void readDmrcCardNumberByImageText(Context context ,String imageText , String cardNumber, VolleyResponse volleyResponse ){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = TextReadBO.verifyExistingCardNumber();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(context)));

        //image read text
        customerVO.setAnonymousString(imageText);

        //user type card number
        customerVO.setAnonymousString1(cardNumber);
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        Log.w("request",json);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(context,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerVO customerVOResp = gson.fromJson(response.toString(), CustomerVO.class);

                if(customerVOResp.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVOResp.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(context,customerVOResp.getDialogTitle(),sb.toString(),false);
                    volleyResponse.onError(sb.toString());
                }else {
                    volleyResponse.onSuccess(customerVOResp);
                }
            }
        });
    }


}

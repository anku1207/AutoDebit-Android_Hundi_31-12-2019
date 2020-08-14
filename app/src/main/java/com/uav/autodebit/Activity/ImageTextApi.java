package com.uav.autodebit.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.ArrayList;
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
    public void getPincode(){

        new Thread(new Runnable(){
            public void run() {
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
                String value=stringBuilder.toString().replaceAll(" ", "");
                String replaceString=value.toString().replaceAll("\n", "");

                int i=0;
                String nos = "";
                List<String> digitsArray = new ArrayList<String>();
                while(replaceString.length()>i){
                    Character ch = replaceString.charAt(i);
                    if(Character.isDigit(ch)){
                        nos += ch;
                    }else if(nos!=""){
                        digitsArray.add(nos);
                        nos ="";
                    }
                    i++;
                }

                String pincode=null;
                for(String p : digitsArray){
                    if(p.length()==6){
                        pincode=p;
                        break;
                    }
                }
                ic.onResult(pincode);
            }
        }).start();

    }


}

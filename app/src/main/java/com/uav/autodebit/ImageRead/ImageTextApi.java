package com.uav.autodebit.ImageRead;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.Utility;

import java.io.BufferedReader;
import java.io.StringReader;
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
                    String value=stringBuilder.toString().replaceAll(" ", "");


                    BufferedReader bufReader = new BufferedReader(new StringReader(value));
                    String line=null;
                    while( (line=bufReader.readLine()) != null ){
                        Log.w("readline",line);
                            if(Utility.isNumeric(line) && line.length()>=8){
                                ic.onResult(line);
                                break;
                            }
                    }
                    ic.onResult(line);
                  /*  String replaceString= value.replaceAll("\n", "/");

                    int i=0;
                    String nos = "";
                    List<String> digitsArray = new ArrayList<String>();
                    while(replaceString.length()>i){
                        Character ch = replaceString.charAt(i);
                        if(Character.isDigit(ch)){
                            nos += ch;
                            if(replaceString.length()==(i+1)){
                                digitsArray.add(nos);
                                nos ="";
                            }
                        }else if(nos!=""){
                            digitsArray.add(nos);
                            nos ="";
                        }
                        i++;
                    }

                    String pincode=null;
                    for(String p : digitsArray){
                        if(p.length()==8){
                            pincode=p;
                            break;
                        }
                    }
                    ic.onResult(pincode);*/
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
                }

            }
        }).start();

    }


}

package com.uav.autodebit.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.SystemClock;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.text.method.DigitsKeyListener;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import com.uav.autodebit.Activity.Login;
import com.uav.autodebit.Activity.Splash_Screen;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Notification.NotificationUtils;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewAlertSelectListBaseAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.volley.VolleyUtils;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {
    static int errorColor;
    static  Context contextact;
    static  String erroract;


    public static SpannableStringBuilder getErrorSpannableString(Context context, String msg){
        int version = Build.VERSION.SDK_INT;
        //Get the defined errorColor from color resource.
        if (version >= 23) {
            errorColor = ContextCompat.getColor(context, R.color.errorColor);
        } else {
            errorColor = context.getResources().getColor(R.color.errorColor);
        }

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(msg);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, msg.length(), 0);

        return spannableStringBuilder;
    }

    public static SpannableStringBuilder getErrorSpannableStringDynamicEditText(Context context, String msg){
        int version = Build.VERSION.SDK_INT;
        //Get the defined errorColor from color resource.
        int backgroundColor = 0;
        if (version >= 23) {
            errorColor = ContextCompat.getColor(context, R.color.errorColor);
            backgroundColor= ContextCompat.getColor(context, R.color.black);
        } else {
            errorColor = context.getResources().getColor(R.color.errorColor);
            backgroundColor= context.getResources().getColor(R.color.black);
        }

        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(errorColor);
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(backgroundColor);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(msg);
        spannableStringBuilder.setSpan(foregroundColorSpan, 0, msg.length(), 0);
        spannableStringBuilder.setSpan(backgroundColorSpan, 0, msg.length(), 0);

        return spannableStringBuilder;
    }

    public  static String imagetostring (Uri mImageUri, Context context){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        Bitmap bmp= Utility.grabImage(mImageUri,context);
        bmp= Utility.scaleDown(bmp, 1100, true);
        if(bmp.getWidth()>bmp.getHeight()){
            Matrix matrix =new Matrix();
            matrix.postRotate(90);
            bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
        }

        /*Bitmap  bmp1= Bitmap.createScaledBitmap(
                bitmap, 320, 500, false);*/
        bmp.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        byte[] imageBytes =outputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }


    public static String BitMapToString(Bitmap realImage, float maxImageSize,
                                        boolean filter){
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();

        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        realImage = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        if(realImage.getWidth()>realImage.getHeight()){
            Matrix matrix =new Matrix();
            matrix.postRotate(90);
            realImage= Bitmap.createBitmap(realImage,0,0,realImage.getWidth(),realImage.getHeight(),matrix,true);
        }

        realImage.compress(Bitmap.CompressFormat.PNG,100,outputStream);
        byte[] imageBytes =outputStream.toByteArray();

        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte=Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }



    public static String getStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        final PrintWriter pw = new PrintWriter(sw, true);
        throwable.printStackTrace(pw);
        return sw.getBuffer().toString();
    }

    //15-1-2019


    public static Bitmap ByteArrayToBitmap(byte[] byteArray){
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(byteArray);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }




    public static String currentDateFormat(){

        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-mm-dd HH:mm:ss.SSSSSS");
        String currentTimeStamp=dateFormat.format(new Date());
        return  currentTimeStamp;

    }

    public static File storeCameraPhotoInSDCard(Bitmap bitmap , String currentdate){
        //  File outputFile = new File(Environment.getExternalStorageDirectory(),"photo_"+currentdate);
        File direct = new File(Environment.getExternalStorageDirectory() + "/ROF");
        if (!direct.exists()) {
            File wallpaperDirectory = new File(Environment.getExternalStorageDirectory()+"/"+"ROF/");
            wallpaperDirectory.mkdirs();
        }
        File file = new File(new File(Environment.getExternalStorageDirectory()+"/"+"ROF/"), "photo_"+currentdate+".JPEG");
        /*if (file.exists()) {
            file.delete();
        }*/
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }




    public static String validatePattern(String value, String key ){
        try{
            JSONObject valid = new JSONObject(key);
            if(value == null){
                return  valid.getString("msg");
            }
            Pattern ptrn = Pattern.compile(valid.getString("pattern"));
            Matcher matcher = ptrn.matcher(value);
            String errorMsg=null;

            if(! matcher.matches()){
                errorMsg= valid.getString("msg");
            }
            return errorMsg;
        }catch(Exception e){

            Log.w("error",e);
            return   ApplicationConstant.SOMETHINGWRONG;
        }
    }


    public static boolean validepanno(Pattern pattern,String value){

        Matcher matcher = pattern.matcher(value);
        // Check if pattern matches
        if (matcher.matches()) {
           return true;
        }
        return false;
    }


    public static Bitmap getImageFileFromSDCared(String filename){

        Bitmap bitmap=null;
        try {

            FileInputStream fis=new FileInputStream(filename);
            bitmap= BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return  bitmap;
    }



    public  static String getImageByPincode(Context context, Bitmap bitmap){
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
        return pincode;


    }



    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }





    public static void  alertDialog(Context context, String title , String msg , String buttonname){
        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);

       alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonname, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


            }
        });
        alertDialog.show();

    }

    public static void  exceptionAlertDialog(Context context,String title ,String msg , String buttonname,String error){

        contextact=context;
        erroract=error;

        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonname, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {


                sendLogFile(contextact,erroract);

            }
        });
        alertDialog.show();

    }
    private static void sendLogFile(Context context ,String error) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{"manojshakya1207@gmail.com"});
        intent.putExtra(Intent.EXTRA_SUBJECT, "Error reported from MyAPP");
        intent.putExtra(Intent.EXTRA_TEXT, "Log file attached."+error); // do this so some email clients don't complain about empty body.
        context.startActivity(intent);
    }


    public static void  activityAlertDialog(final Context context, String title, String msg, String buttonname){

        AlertDialog alertDialog;
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, buttonname, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent =new Intent(context, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivities(new Intent[]{intent});
            }
        });
        alertDialog.show();

    }
    public static Drawable GetImage(Context c, String ImageName) {
        Drawable drawable;

        try {
            String[] imagename=ImageName.split("\\.");
            drawable= c.getResources().getDrawable(c.getResources().getIdentifier(imagename[0], "drawable", c.getPackageName()));
        }catch (Exception e){
            drawable=null;
        }
       return drawable;
    }


    public static String capitalize(String capString){
        StringBuffer capBuffer = new StringBuffer();
        Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
        while (capMatcher.find()){
            capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
        }
        return capMatcher.appendTail(capBuffer).toString();
    }

    public static JSONArray getQueryarray(String query) {
        JSONArray jsonArray=new JSONArray();
        try {
            String[] params = query.split("&");
            for (String param : params) {
                JSONObject object=new JSONObject();
                String name = param.split("=")[0];
                String  value=param.split("=").length>1?param.split("=")[1]:null;
                object.put(name,value);
                jsonArray.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public static void showSingleButtonDialog(final Context var1, String title, String msg , final boolean activityfinish ,String... buttons){

        String leftButton= (buttons.length==0 ?"OK":buttons[0]);//(leftButton ==null?"Modify": leftButton);



        final Dialog var3 = new Dialog(var1);
        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        var3.setContentView(var1.getResources().getIdentifier("singlebuttondialog", "layout", var1.getPackageName()));
        var3.setCanceledOnTouchOutside(false);
        TextView var4 = (TextView)var3.findViewById(var1.getResources().getIdentifier("dialog_one_tv_title", "id", var1.getPackageName()));
        var4.setText(title);
        TextView var6 = (TextView)var3.findViewById(var1.getResources().getIdentifier("dialog_one_tv_text", "id", var1.getPackageName()));

        var6.setText(msg);
        Button var5 = (Button)var3.findViewById(var1.getResources().getIdentifier("dialog_one_btn", "id", var1.getPackageName()));
        var5.setText(leftButton);


        var5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                var3.dismiss();
                Activity activity = (Activity) var1;
                if(activityfinish) activity.finish();
            }
        });
        var3.show();
    }

    public static Date convertString2Date(String dtValue, String format){
        Date dt = new Date();
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
            dt =  simpleDateFormat.parse(dtValue);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt;
    }

    public static Date StringToDateWithLenient(String dtValue, String format){

        Date dt = new Date();
        try {
            SimpleDateFormat simpleDateFormat=new SimpleDateFormat(format);
            simpleDateFormat.setLenient(false);
            dt =  simpleDateFormat.parse(dtValue);

        } catch (ParseException e) {
            e.printStackTrace();
            dt=null;
        }

        return dt;
    }

    public static String convertDate2String(Date dtValue , String format){
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(dtValue);
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(Calendar.YEAR) - a.get(Calendar.YEAR);
        if (a.get(Calendar.DAY_OF_YEAR) > b.get(Calendar.DAY_OF_YEAR)) {
            diff--;
        }
        return diff;
    }


    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
    }



    public static Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static MotionEvent ontouchevent(){
        // Obtain MotionEvent object
        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis() + 100;
        float x = 0.0f;
        float y = 0.0f;
        // List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
        int metaState = 0;
        MotionEvent motionEvent = MotionEvent.obtain(
                downTime,
                eventTime,
                MotionEvent.ACTION_UP,
                x,
                y,
                metaState
        );
        // Dispatch touch event to view
        return motionEvent;
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    public static String toJson(Object jsonObject) {
        return new Gson().toJson(jsonObject);
    }

    public static Object fromJson(String jsonString, Type type) {
        return new Gson().fromJson(jsonString, type);
    }

    public static Integer getVersioncode(Context context){
        Integer version =null;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            version = pInfo.versionCode;
            Log.w("versioncode", String.valueOf(version));
        } catch (Exception e) {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return version;
    }


    @SuppressLint("NewApi")
    public static CardView getCardViewStyle(Context context){
        CardView cardview ;
        cardview = new CardView(context);

        cardview.setLayoutParams(getLayoutparams(context,10,10,10,10));
        cardview.setRadius(dipToPixels(context,15));
        cardview.setElevation(dipToPixels(context,7));
        return cardview;
    }


    public static TextView getTextView(Context context, String txt){
        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinssemibold);
        TextView tv = new TextView(context);
        tv.setText(txt);
        tv.setTypeface(typeface);
        tv.setLayoutParams(getLayoutparams(context,20,0,0,0));
        return tv;
    }

    public static EditText getEditText(Context context){
        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinsmedium);
        EditText ed = new EditText(context);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources()
                        .getDisplayMetrics());

        layoutparams.setMargins(marginInDp,0,0,0);

        ed.setLayoutParams(layoutparams);
        ed.setSingleLine(true);
        ed.setTypeface(typeface, Typeface.BOLD);
        ed.setInputType(InputType.TYPE_CLASS_NUMBER);
        ed.setKeyListener(DigitsKeyListener.getInstance(context.getString(R.string.NumberDigits)));
        ed.setCompoundDrawablePadding(4);
        ed.setMaxLines(1);
        ed.setTextSize(12);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ed.setBackgroundTintList(context.getResources().getColorStateList(R.color.white));
        }
        ed.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.pencil),null,null,null);

        return ed;
    }

    public static UAVEditText getUavEditText(Context context){
        Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinsmedium);
        UAVEditText ed = new UAVEditText(context,null);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInDp = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources()
                        .getDisplayMetrics());

        layoutparams.setMargins(marginInDp,0,0,0);

        ed.setLayoutParams(layoutparams);
        ed.setSingleLine(true);
        ed.setTypeface(typeface, Typeface.BOLD);
        ed.setInputType(InputType.TYPE_CLASS_NUMBER);
        ed.setKeyListener(DigitsKeyListener.getInstance(context.getString(R.string.NumberDigits)));
        ed.setCompoundDrawablePadding(4);
        ed.setMaxLines(1);
        ed.setTextSize(12);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ed.setBackgroundTintList(context.getResources().getColorStateList(R.color.white));
        }
        ed.setCompoundDrawablesWithIntrinsicBounds(context.getResources().getDrawable(R.drawable.pencil),null,null,null);

        return ed;
    }



    public static RecyclerView getRecyclerView(Context context){
        RecyclerView recyclerView =new RecyclerView(context);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        recyclerView.setLayoutParams(layoutparams);
        return recyclerView;
    }


    public static ViewPager getViewPager(Context context){
        ViewPager viewPager =new ViewPager(context);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                getPixelsFromDPs(context,220)
        );
        viewPager.setLayoutParams(layoutparams);
        return viewPager;
    }

    public static ImageView getImageView(Context context){
        ImageView imageView = new ImageView(context);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,getPixelsFromDPs(context,220));
        imageView.setLayoutParams(layoutparams);
        return imageView;
    }

    public static Button getButton(Context context){
        Button button =new Button(context);
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutparams.setMargins(0,30,0,30);
        layoutparams.gravity = Gravity.CENTER_HORIZONTAL;

        button.setLayoutParams(layoutparams);
        button.setText("Add Card");
        button.setBackground(context.getResources().getDrawable(R.drawable.cornerbutton));
        button.setAllCaps(false);
        button.setTextColor(context.getResources().getColor(R.color.white));
        return button;
    }





    public static LinearLayout.LayoutParams getLayoutparams(Context context, int left, int top, int right, int bottom){
        LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutparams.setMargins(Utility.getPixelsFromDPs(context,left),Utility.getPixelsFromDPs(context,top),Utility.getPixelsFromDPs(context,right),Utility.getPixelsFromDPs(context,bottom));

        return layoutparams;
    }

    public static int getPixelsFromDPs(Context mContext , int dps){
        Resources r = mContext.getResources();
        int  px = (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dps, r.getDisplayMetrics()));
        return px;
    }

    public static float dipToPixels(Context context, float dipValue){
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  dipValue, metrics);
    }


    public static void confirmationDialog(com.uav.autodebit.util.DialogInterface mcxtinter, Context context , JSONArray jsonArray , String Msg , String title,  String... buttons){
         String leftButton= (buttons.length==0 ?"Modify":buttons[0]);//(leftButton ==null?"Modify": leftButton);
         String rightButton=(buttons.length<=1 ?"Next":buttons[1]);//(rightButton==null?"Next":rightButton);
         try{

            final com.uav.autodebit.util.DialogInterface dialogInterface =mcxtinter;

            final Dialog var3 = new Dialog(context);
            var3.requestWindowFeature(1);
            var3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            var3.setContentView(R.layout.confirmation_dialog);
            var3.setCanceledOnTouchOutside(false);
            var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            LinearLayout mainlayout =var3.findViewById(R.id.mainlayout);
            TextView dialog_title=var3.findViewById(R.id.dialog_title);
            Button modify=var3.findViewById(R.id.modify);
            Button next=var3.findViewById(R.id.next);
            ImageView canceldialog=var3.findViewById(R.id.canceldialog);

            modify.setText(leftButton);
            next.setText(rightButton);

            dialog_title.setText(title);

             Typeface typeface = ResourcesCompat.getFont(context, R.font.poppinssemibold);


             if(Msg==null){
                for(int i=0;i<jsonArray.length();i++){

                    JSONObject jsonObject =jsonArray.getJSONObject(i);
                    LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_layout));

                    TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                    text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                    text.setText(jsonObject.getString("key"));
                    text.setMaxLines(1);
                    text.setEllipsize(TextUtils.TruncateAt.END);
                    text.setTypeface(typeface);

                    TextView text1 = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_filed));
                    text1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT,0));
                    text1.setText(" : ");
                    text1.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);




                    TextView value = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_value));
                    value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                    value.setText(jsonObject.getString("value"));
                    value.setTypeface(typeface);

                    et.addView(text);
                    et.addView(text1);
                    et.addView(value);
                    mainlayout.addView(et);
                }
            }else {


                LinearLayout et = new LinearLayout(new ContextThemeWrapper(context,R.style.confirmation_dialog_layout));

                TextView text = new TextView(new ContextThemeWrapper(context, R.style.confirmation_dialog_text_flied));
                text.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                text.setText(Msg);
                text.setTypeface(typeface);


                et.addView(text);
                mainlayout.addView(et);
            }

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(var3.getWindow().getAttributes());
            lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.modify(var3);
                }
            });
            canceldialog.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.modify(var3);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.confirm(var3);

                }
            });

          if(!var3.isShowing())  var3.show();
            var3.getWindow().setAttributes(lp);

        }catch (Exception e){
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    public static void showDoubleButtonDialogConfirmation(com.uav.autodebit.util.DialogInterface mcxtinter, Context context , String Msg , String title,  String... buttons){
        String leftButton= (buttons.length==0 ?"Modify":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        String rightButton=(buttons.length<=1 ?"Next":buttons[1]);//(rightButton==null?"Next":rightButton);
        try{

            final com.uav.autodebit.util.DialogInterface dialogInterface =mcxtinter;

            final Dialog var3 = new Dialog(context);
            var3.requestWindowFeature(1);
            var3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            var3.setContentView(R.layout.showdoublebuttondialogconfirmation);
            var3.setCanceledOnTouchOutside(false);
            var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


            TextView titletext=var3.findViewById(R.id.dialog_one_tv_title);
            TextView msg=var3.findViewById(R.id.dialog_one_tv_text);
            Button modify=var3.findViewById(R.id.button1);
            Button next=var3.findViewById(R.id.button2);

            modify.setText(leftButton);
            next.setText(rightButton);

            titletext.setText(title);
            msg.setText(Msg);


            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.modify(var3);
                }
            });

            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogInterface.confirm(var3);

                }
            });

            if(!var3.isShowing())  var3.show();

        }catch (Exception e){
            Utility.exceptionAlertDialog(context,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }




    public static String maskString(String strText, int start, int end, char maskChar)
            throws Exception{

        if(strText == null || strText.equals(""))
            return "";

        if(start < 0)
            start = 0;

        if( end > strText.length() )
            end = strText.length();

        if(start > end)
            throw new Exception("End index cannot be greater than start index");

        int maskLength = end - start;

        if(maskLength == 0)
            return strText;

        StringBuilder sbMaskString = new StringBuilder(maskLength);

        for(int i = 0; i < maskLength; i++){
            sbMaskString.append(maskChar);
        }

        return strText.substring(0, start)
                + sbMaskString.toString()
                + strText.substring(start + maskLength);
    }



    public static void showSingleButtonDialogconfirmation(Context context, ConfirmationDialogInterface confirmationDialogInterface, String title, String msg , String... buttons){

        String leftButton= (buttons.length==0 ?"OK":buttons[0]);//(leftButton ==null?"Modify": leftButton);
        final Dialog var3 = new Dialog(context);


        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(R.layout.singlebuttondialog);
        var3.setCanceledOnTouchOutside(false);
     //   var3.setCancelable(false);

        var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        TextView title_text = (TextView)var3.findViewById(R.id.dialog_one_tv_title);
        title_text.setText(title);
        TextView msg_text = (TextView)var3.findViewById(R.id.dialog_one_tv_text);

        msg_text.setText(msg);
        Button button = (Button)var3.findViewById(R.id.dialog_one_btn);
        button.setText(leftButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var) {
                confirmationDialogInterface.onOk(var3);
            }
        });
        var3.show();
    }




    public static int byteSizeOf(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return bitmap.getAllocationByteCount();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        } else {
            return bitmap.getRowBytes() * bitmap.getHeight();
        }
    }




    public static Bitmap decodeImageFromFiles(String path, int width, int height) {
        BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
        scaleOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, scaleOptions);
        int scale = 1;
        while (scaleOptions.outWidth / scale / 2 >= width
                && scaleOptions.outHeight / scale / 2 >= height) {
            scale *= 2;
        }
        // decode with the sample size
        BitmapFactory.Options outOptions = new BitmapFactory.Options();
        outOptions.inSampleSize = scale;
        return BitmapFactory.decodeFile(path, outOptions);
    }

    public static DatePickerDialog bindCalendar(Context context, final EditText editText, Calendar cal){
        int mYear, mMonth, mDay;
        mYear = cal.get(Calendar.YEAR);
        mMonth = cal.get(Calendar.MONTH);
        mDay = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        DecimalFormat df = new DecimalFormat("00");
                        int m=monthOfYear+1;
                        String month = df.format(m);

                        String day=df.format(dayOfMonth);
                        editText.setText(day + "/" + month + "/" +year);
                        editText.setError(null);

                        editText.setSelection(editText.getText().toString().length());
                    }
                }, mYear, mMonth, mDay);
        return datePickerDialog;
    }



    public static Bitmap getCircularBitmapWithWhiteBorder(Bitmap bitmap,
                                                          int borderWidth) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }

        final int width = bitmap.getWidth() + borderWidth;
        final int height = bitmap.getHeight() + borderWidth;

        Bitmap canvasBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);

        Canvas canvas = new Canvas(canvasBitmap);
        float radius = width > height ? ((float) height) / 2f : ((float) width) / 2f;
        canvas.drawCircle(width / 2, height / 2, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(borderWidth);
        canvas.drawCircle(width / 2, height / 2, radius - borderWidth / 2, paint);
        return canvasBitmap;
    }

    public static int dpToPx(Context context,int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static int pxToDp(Context context,int px) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }


    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity == null) {
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public static void alertselectdialog(Context context, String title, ArrayList<CustomerAuthServiceVO> dataArray, AlertSelectDialogClick alertSelectDialogClick){
         final Dialog var3 = new Dialog(context);
         var3.requestWindowFeature(1);
         var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
         var3.setContentView(R.layout.alertselectdialog);
         // var3.setCanceledOnTouchOutside(false);
         TextView title_text = (TextView)var3.findViewById(R.id.dialog_one_tv_title);
         title_text.setText(title);
         ListView listView = (ListView) var3.findViewById(R.id.listview);



         ListViewAlertSelectListBaseAdapter myAdapter=new ListViewAlertSelectListBaseAdapter(context, dataArray, R.layout.design_list_text_with_card);


        listView.setAdapter(myAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                var3.dismiss();
                CustomerAuthServiceVO customerAuthServiceVO=dataArray.get(i);
                alertSelectDialogClick.onSuccess(String.valueOf(customerAuthServiceVO.getCustomerAuthId()));
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(var3.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        var3.show();
        var3.getWindow().setAttributes(lp);
    }


    public static void enableDisableView(View view, boolean enabled) {
        view.setEnabled(enabled);
        if ( view instanceof ViewGroup ) {
            ViewGroup group = (ViewGroup)view;

            for ( int idx = 0 ; idx < group.getChildCount() ; idx++ ) {
                enableDisableView(group.getChildAt(idx), enabled);
            }
        }
    }



    public static void changeTextColor(final TextView textView, int startColor, int endColor,
                                       final long animDuration, final long animUnit){
        if (textView == null) return;

        final int startRed = Color.red(startColor);
        final int startBlue = Color.blue(startColor);
        final int startGreen = Color.green(startColor);

        final int endRed = Color.red(endColor);
        final int endBlue = Color.blue(endColor);
        final int endGreen = Color.green(endColor);

        new CountDownTimer(animDuration, animUnit){
            //animDuration is the time in ms over which to run the animation
            //animUnit is the time unit in ms, update color after each animUnit

            @Override
            public void onTick(long l) {
                int red = (int) (endRed + (l * (startRed - endRed) / animDuration));
                int blue = (int) (endBlue + (l * (startBlue - endBlue) / animDuration));
                int green = (int) (endGreen + (l * (startGreen - endGreen) / animDuration));

                Log.d("Changing color", "Changing color to RGB" + red + ", " + green + ", " + blue);
                textView.setTextColor(Color.rgb(red, green, blue));
            }

            @Override
            public void onFinish() {
                textView.setTextColor(Color.rgb(endRed, endGreen, endBlue));
            }
        }.start();
    }

    public static void disable_Tab(TabLayout tabLayout){
        LinearLayout tabStrip = ((LinearLayout)tabLayout.getChildAt(0));
        tabStrip.setEnabled(false);
        for(int i = 0; i < tabStrip.getChildCount(); i++) {
            tabStrip.getChildAt(i).setClickable(false);
        }
    }

    public void disable_AllEditTest(LinearLayout linearLayout){
        for (View view : linearLayout.getTouchables()){
            if (view instanceof EditText){
                EditText editText = (EditText) view;
                editText.setEnabled(false);
                editText.setFocusable(false);
                editText.setFocusableInTouchMode(false);
            }
        }
    }

    public static void removeEle(View ele){
        View myView = ele;
        ViewGroup parent = (ViewGroup) myView.getParent();
        parent.removeView(myView);
    }

    public static int getColorWrapper(Context context, int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return context.getColor(id);
        } else {
            //noinspection deprecation
            return context.getResources().getColor(id);
        }
    }

    public static boolean setErrorOnEdittext(EditText[] eleid){
        boolean valid =true;
        for(EditText ele:eleid){
            if(ele.getText().toString().equals("")){
                ele.setError("this filed is required");
                valid=false;
            }
        }
        return valid;
    }


    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        String ip = Formatter.formatIpAddress(inetAddress.hashCode());
                        Log.i("IP", "***** IP="+ ip);
                        return ip;
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("error", ex.toString());
        }
        return null;
    }
/*
    public void RelativeLayoutPro(EditText editText){
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)editText.getLayoutParams();
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.LEFT_OF, R.id.amount);
        params.addRule(RelativeLayout.ALIGN_BOTTOM, RelativeLayout.TRUE);
    }*/


   public static Uri getVersionWiseUri(Context context, File file){
       Uri uri;
       if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
       }else{
           uri = Uri.fromFile(file);
      }
       return uri;
   }

    public static int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    public static  void furnishErrorMsg(String errorTitle, JSONObject jsonObject, Context context) throws JSONException {
        JSONArray error = jsonObject.getJSONArray("error");
        StringBuilder sb = new StringBuilder();
        for(int i=0; i<error.length(); i++){
            sb.append(error.get(i)).append("\n");
        }
        VolleyUtils.showError(errorTitle, sb.toString(), context );


    }
    /// manoj shakya
    public static File createTemporaryFile(String part, String ext) throws Exception {
        File tempDir= Environment.getExternalStorageDirectory();
        tempDir=new File(tempDir.getAbsolutePath()+"/HUNDI/");
        if(!tempDir.exists()){
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }
    public static Bitmap grabImage(Uri mImageUri, Context context){
        //context.getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = context.getContentResolver();
        Bitmap bitmap = null;
        try{
            bitmap  = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
        }
        catch (Exception e){
            Toast.makeText(context, "Failed to load", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }


    public static Animation getOnShakeAnimation(Context context ) {
        return  AnimationUtils.loadAnimation(context, R.anim.shake);
   }

    public static  LayoutAnimationController getRunLayoutAnimation(Context context) {
        final LayoutAnimationController controller =
                AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_from_bottom);
        return controller;
    }


    private void generateNotification(Context context, RemoteMessage remoteMessage) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-fbase";
        String channelName = "group_Notification";
        int importance = NotificationManager.IMPORTANCE_HIGH;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        Intent notificationIntent = new Intent(context, Splash_Screen.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //  PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, channelId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setSmallIcon(R.drawable.autodebitlogo);
            int color = 0x008000;
            mBuilder.setColor(color);
        } else {
            mBuilder.setSmallIcon(R.drawable.autodebitlogo);
        }
        NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
        bigPictureStyle.setBigContentTitle(remoteMessage.getNotification().getBody());
        if(remoteMessage.getNotification().getImageUrl()!=null){
            bigPictureStyle.bigPicture(NotificationUtils.getBitmapFromURL(remoteMessage.getNotification().getImageUrl()+""));
        }
        mBuilder.setStyle(bigPictureStyle);
        mBuilder.setContentTitle(remoteMessage.getNotification().getTitle());
        mBuilder.setContentText(remoteMessage.getNotification().getBody());
        // mBuilder.setContentIntent(pendingIntent);
        //If you don't want all notifications to overwrite add int m to unique value
        Random random = new Random();
        int m = random.nextInt(9999 - 1000) + 1000;
        notificationManager.notify(m, mBuilder.build());
    }


    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static  void showToast(Context context,final String toast){
        ((Activity)context).runOnUiThread(() -> Toast.makeText(context, toast, Toast.LENGTH_SHORT).show());
    }



    public static Drawable getDrawableResources(Context context,int id){
        Drawable myDrawable;
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP){
            myDrawable = context.getResources().getDrawable(id, context.getTheme());
        } else {
            myDrawable = context.getResources().getDrawable(id);
        }
        return myDrawable;
    }


    public static void showSelectPaymentTypeDialog(Context context ,String title ,ArrayList<DataAdapterVO> dataAdapterVOS ,AlertSelectDialogClick alertSelectDialogClick){

        int   selectItemPossion;

        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(1);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(0));
        dialog.setContentView(R.layout.select_payment_type_stype);
        dialog.setCanceledOnTouchOutside(false);

        TextView title_text = (TextView)dialog.findViewById(R.id.dialog_one_tv_title);
        title_text.setText(title);

        Button proceed= dialog.findViewById(R.id.proceed);

        RadioGroup radiogroup = dialog.findViewById(R.id.radiogroup);
        LinearLayout main_layout = (LinearLayout) dialog.findViewById(R.id.main_layout);


        for(int j=0;j<dataAdapterVOS.size();j++){
            DataAdapterVO dataAdapterVO = dataAdapterVOS.get(j);
            RadioButton rdbtn = new RadioButton(context);
            rdbtn.setId(j);
            rdbtn.setText(dataAdapterVO.getText());
            rdbtn.setChecked(false);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(15, 15, 15, 15);
            rdbtn.setLayoutParams(params);
            rdbtn.setPadding(50,0,0,0);

            rdbtn.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.colorPrimary)));
            radiogroup.addView(rdbtn);
        }
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                proceed.setVisibility(View.VISIBLE);
            }
        });

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                alertSelectDialogClick.onSuccess(radiogroup.getCheckedRadioButtonId()+"");
            }
        });




        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }


}

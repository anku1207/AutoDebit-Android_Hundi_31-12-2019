package com.uav.autodebit.Activity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.PanCardBO;
import com.uav.autodebit.BO.PinCodeBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.util.MaskWatcher;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class PanVerification extends AppCompatActivity {
    EditText pannumber,customername,pin,city,state,permanentaddress;
    TextView attachaddress;
    Button panverify;

    UAVEditText date1;

    Uri mImageUri;
    int  REQ_IMAGE=1001,REQ_GALLERY=1002;
    Bitmap bmp;

    ImageView addressimage,back_activity_button1 ;

    boolean permissionstate;
    String customerId;

    Gson gson = new Gson();

    UAVProgressDialog pd;
    String stringimg;


    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        disableAutofill();

        setContentView(R.layout.activity_pan_verification);
        getSupportActionBar().hide();

        pd=new UAVProgressDialog(this);


        pannumber=findViewById(R.id.pannumber);
        customername=findViewById(R.id.username);
        date1=findViewById(R.id.date);
        pin=findViewById(R.id.pin);
        city=findViewById(R.id.city);
        state=findViewById(R.id.state);
        permanentaddress=findViewById(R.id.permanentaddress);
        attachaddress=findViewById(R.id.attachaddress);
        panverify=findViewById(R.id.panverify);
        addressimage=findViewById(R.id.addressimage);

        back_activity_button1=findViewById(R.id.back_activity_button1);

       // pannumber.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
       // username.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        pin.setInputType(InputType.TYPE_CLASS_NUMBER);




        city.setKeyListener(null);
        state.setKeyListener(null);



        Intent intent = getIntent();
        customerId=Session.getCustomerId(PanVerification.this);

        back_activity_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        CustomerVO customerVO = gson.fromJson(Session.getSessionByKey(PanVerification.this,Session.CACHE_CUSTOMER), CustomerVO.class);

        if(customerVO.getPanHolderName()!=null){
            customername.setText(Utility.capitalize(customerVO.getPanHolderName()));
            customername.setEnabled(false);
        }else {
            customername.setText(Session.getCustomerName(PanVerification.this));
        }



        if(customerVO.getPanNo()!=null){
            pannumber.setText(customerVO.getPanNo());
        }
        if(customerVO.getDateOfBirth()!=null){
            Timestamp timestamp =new Timestamp(customerVO.getDateOfBirth());
            Date date =new Date(timestamp.getTime());

            String simpleDateFormat =new SimpleDateFormat("dd/MM/yyyy").format(date);
            date1.setText(simpleDateFormat);

        }
        if(customerVO.getPincode()!=null){
            pin.setText(customerVO.getPincode());

        }
        if(customerVO.getCity()!=null && customerVO.getCity().getCityName()!=null){
            city.setText(Utility.capitalize(customerVO.getCity().getCityName()));
            city.setEnabled(false);
        }
        if(customerVO.getStateRegion()!=null && customerVO.getStateRegion().getStateRegionName()!=null){
            state.setText(Utility.capitalize(customerVO.getStateRegion().getStateRegionName()));
            state.setEnabled(false);
        }
        if(customerVO.getAddress1()!=null){
            permanentaddress.setText(Utility.capitalize(customerVO.getAddress1()));
        }



        attachaddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {

                        if (PermissionHandler.imagePermission(PanVerification.this)) {
                            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(PanVerification.this);
                            pictureDialog.setTitle("Select Action");
                            String[] pictureDialogItems = {
                                    "Select photo from gallery",
                                    "Capture photo from camera"};
                            pictureDialog.setItems(pictureDialogItems,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case 0:
                                                    galleryimage();
                                                    break;
                                                case 1:
                                                    cameraimage();
                                                    break;
                                            }
                                        }
                                    });
                            pictureDialog.show();
                        }
                    }
                catch(Exception e){
                    Utility.exceptionAlertDialog(PanVerification.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                }
            }
        });


        date1.addTextChangedListener(new MaskWatcher("##/##/####"));

        date1.setDrawableClickListener(new DrawableClickListener() {
            @Override
            public void onClick(DrawablePosition target) {
                try {
                    if(target.equals(DrawablePosition.LEFT)){
                        Calendar c = Calendar.getInstance();
                        if(!date1.getText().toString().equals("")){
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
                            c.setTime(sdf.parse(date1.getText().toString()));
                        }
                        DatePickerDialog datePickerDialog = Utility.bindCalendar(PanVerification.this, date1, c);
                        datePickerDialog.show();
                    }
                }catch (Exception e){
                    Log.w("error",e.getMessage());
                }
            }
        });







        pin.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean gainFocus) {
                //onFocus
                if (gainFocus) {
                    //set the row background to a different color
                }
                //onBlur
                else {
                    if (pin.length() < 6) {

                        pin.setError(Utility.getErrorSpannableString(getApplicationContext(),  "Plz enter at least 6 characters"));
                        city.setText("");
                        state.setText("");
                    }
                }
            }
        });

        pin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (pin.length() == 6) {
                    pincodebycity(pin.getText().toString().trim());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });



        panverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(date1.getError()!=null) return;

                if(!requiredfiled()) return;
                if(!Utility.validepanno(ApplicationConstant.pancard,pannumber.getText().toString().trim())){
                    pannumber.setError("PanNumber is Wrong");
                    return;
                }
                if(pin.getText().toString().trim().length()<6){
                    pin.setError("Pincode is Wrong");
                    return;
                }


                 Date date =  Utility.StringToDateWithLenient(date1.getText().toString().trim(),"dd/MM/yyyy");
                 if(date==null){
                      date1.setError("Enter a valid date: DD/MM/YYYY");
                      return;
                 }
                 Date currentdate=new Date();
                 int numberofyear=Utility.getDiffYears(date,currentdate);


                 if(numberofyear < 18){
                     Utility.showSingleButtonDialog(PanVerification.this,"Error !","Age should be greater than 18 yrs.",false);
                     return;
                 }else if(numberofyear > 100){
                     Utility.showSingleButtonDialog(PanVerification.this,"Error !","Age not valid.",false);
                     return;
                 }



                if(bmp==null){
                    verifyPan();
                }else {
                    BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                        @Override
                        public void doInBackGround() {
                            stringimg= Utility.BitMapToString(bmp);
                        }
                        @Override
                        public void doPostExecute() {
                            pd.dismiss();
                            verifyPan();
                        }
                    });
                    backgroundAsyncService.execute();
                }







            }
        });
    }

    public void verifyPan(){

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = PanCardBO.panCardVerification();

            CustomerVO customerVO=new CustomerVO();

            customerVO.setPanNo(pannumber.getText().toString().trim());
            customerVO.setPanHolderName(customername.getText().toString().trim());
            Date dob = null;
            dob = new SimpleDateFormat("dd/MM/yyyy").parse(date1.getText().toString().trim());
            customerVO.setDateOfBirth(dob.getTime());
            customerVO.setPincode(pin.getText().toString().trim());
            customerVO.setAddress1(permanentaddress.getText().toString().trim());
            customerVO.setCustomerId(Integer.parseInt(customerId));

            if(bmp!=null){
                customerVO.setImage(stringimg);
            }


            String json = gson.toJson(customerVO);

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
                       // Utility.alertDialog(PanVerification.this,"Alert",sb.toString(),"Ok");
                        Utility.showSingleButtonDialog(PanVerification.this,"Error !",sb.toString(),false);
                    }else {
                        String json = gson.toJson(customerVO);
                        Session.set_Data_Sharedprefence(PanVerification.this,Session.CACHE_CUSTOMER,json);

                        startActivity(new Intent(PanVerification.this,Credit_Score_Report.class));
                        finish();

                    }
                }
            });
        } catch (ParseException e) {
            e.printStackTrace();
        }






    }


    public void galleryimage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,REQ_GALLERY );
    }

    public void cameraimage(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        File photo;
        try
        {
            // place where to store camera taken picture
            photo = VolleyUtils.createTemporaryFile("picture", ".jpg");
            photo.delete();
            mImageUri = Uri.fromFile(photo);
            /*Uri mImageUri = CustomProvider.getPhotoUri(photo);
             */
            Uri mImageUri = FileProvider.getUriForFile(getApplicationContext(),getApplicationContext().getPackageName()
                    + ".provider", photo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQ_IMAGE);
        }
        catch(Exception e)
        {
            Utility.exceptionAlertDialog(PanVerification.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    public  boolean requiredfiled(){
        pannumber.setError(null);
        customername.setError(null);
        date1.setError(null);
        pin.setError(null);
        city.setError(null);
        state.setError(null);
        permanentaddress.setError(null);




        boolean filed=true;
        if(pannumber.getText().toString().trim().equals("")){
            pannumber.setError("This filed is required");
            filed=false;
        }
        if(customername.getText().toString().trim().equals("")){
            customername.setError("This filed is required");
            filed=false;

        }
        if(date1.getText().toString().trim().equals("")){
            date1.setError("This filed is required");
            filed=false;
        }
        if(pin.getText().toString().trim().equals("")){
            pin.setError("This filed is required");
            filed=false;
        }
        if(city.getText().toString().trim().equals("")){
            city.setError("This filed is required");
            filed=false;
        }
        if(state.getText().toString().trim().equals("")){
            state.setError("This filed is required");
            filed=false;
        }
        if(permanentaddress.getText().toString().trim().equals("")){
            permanentaddress.setError("This filed is required");
            filed=false;
        }

        return filed;
    }

    public void pincodebycity(String pincode){
        VolleyUtils.makeJsonObjectRequest(this, PinCodeBO.getCityByPincode(pincode), new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;


                Gson gson = new Gson();
                CityVO cityVO = gson.fromJson(response.toString(), CityVO.class);


                Log.w("responsesignup",response.toString());
                if(cityVO.getStatusCode().equals("400")){
                    //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                    ArrayList error = (ArrayList) cityVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    //Utility.alertDialog(PanVerification.this,"Alert",sb.toString(),"Ok");
                    Utility.showSingleButtonDialog(PanVerification.this,"Error !",sb.toString(),false);
                    city.setText("");
                    state.setText("");


                }else {

                    state.setText(Utility.capitalize( cityVO.getCityName()));
                    city.setText(Utility.capitalize(cityVO.getStateRegion().getStateRegionName()));
                    city.setError(null);
                    state.setError(null);

                }
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == REQ_IMAGE) {
                    bmp =Utility.decodeImageFromFiles(mImageUri.getPath(),150,150);
                    if(bmp.getWidth()>bmp.getHeight()){
                        Matrix matrix =new Matrix();
                        matrix.postRotate(90);
                        bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
                    }

                    addressimage.setImageBitmap(bmp);

                    View current = getCurrentFocus();
                    if (current != null) current.clearFocus();
                }
               if(requestCode==REQ_GALLERY){
                      Uri contentURI = data.getData();
                      bmp =VolleyUtils.grabImage(contentURI,PanVerification.this);
                      addressimage.setImageBitmap(bmp);
               }
            } catch (Exception e) {
                Utility.exceptionAlertDialog(PanVerification.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
            }
        }
    }

}


package com.uav.autodebit.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.RecyclerViewAdapterMenu;
import com.uav.autodebit.adpater.RecyclerViewProfileBankAdapterMenu;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.CircularImageView;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.CircleTransform;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.DownloadTask;
import com.uav.autodebit.util.FileDownloadInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Profile_Activity extends Base_Activity implements FileDownloadInterface ,
        View.OnClickListener , PermissionUtils.PermissionResultCallback ,ActivityCompat.OnRequestPermissionsResultCallback{

    BottomNavigationView navigation;
    TextView usename,pannumber,mobileno,email,address,citystate,pincode,creditscore,changepass;
    ImageView mobileverify,emailverify,addressverify,downloadreport,back_activity_button,more_service,more_bankadd;
    String cir_report;

    Context context =Profile_Activity.this;
    RecyclerView servicesrecy,bankrecycler;
    CircularImageView imageView1;
    boolean isemailverify=false;

    UAVProgressDialog pd;
    List<ServiceTypeVO> bankServiceList=new ArrayList<>();
    List<ServiceTypeVO> addservice =new ArrayList<>();

    RecyclerViewAdapterMenu recyclerViewAdapter;
    int  REQ_IMAGE=1001,REQ_GALLERY=1002,REQ_ENACH_MANDATE=1003,PIC_CROP=1004,REQ_CHANGE_PASS=300,REQ_ADD_MORE_SERVICE=200,REQ_EMAIL_VERIFY=100,REQ_ADDBANK_ANDSERVICE=400;

    Bitmap bmp;

    ConnectionVO customerProfileImage;
    ProgressBar progressBar;

    ScrollView scrollView;

    File photofileurl;

    PermissionUtils permissionUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_);
        getSupportActionBar().hide();

        pd=new UAVProgressDialog(Profile_Activity.this);

        usename=findViewById(R.id.usename);
        pannumber=findViewById(R.id.pannumber);

        mobileno=findViewById(R.id.mobileno);
        email=findViewById(R.id.email);
        address=findViewById(R.id.address);
        citystate=findViewById(R.id.citystate);
        pincode=findViewById(R.id.pincode);
        creditscore=findViewById(R.id.creditscore);
        back_activity_button=findViewById(R.id.back_activity_button);
        servicesrecy=findViewById(R.id.servicerecycler);
        more_service=findViewById(R.id.more_service);
        bankrecycler=findViewById(R.id.bankrecycler);
        imageView1=findViewById(R.id.imageView1);
        progressBar=findViewById(R.id.progressBar);
        changepass=findViewById(R.id.changepass);
        more_bankadd=findViewById(R.id.more_bankadd);
        scrollView=findViewById(R.id.scrollView);
        mobileverify=findViewById(R.id.mobileverify);
        emailverify=findViewById(R.id.emailverify);
        addressverify=findViewById(R.id.addressverify);
        downloadreport=findViewById(R.id.downloadreport);

        permissionUtils=new PermissionUtils(Profile_Activity.this);


        back_activity_button.setOnClickListener(this);
        more_service.setOnClickListener(this);
        changepass.setOnClickListener(this);
        more_bankadd.setOnClickListener(this);

        downloadreport.setVisibility(View.GONE);


        downloadreport.setOnClickListener(this);
        imageView1.setOnClickListener(this);

        imageView1.setBorderColor(getResources().getColor(R.color.appbar));
        imageView1.setBorderWidth(4);
        customerProfileImage = CustomerBO.setCustomerProfileImage();




        navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.bottom_profile);

        BottomNavigationView  navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getProfileDate(Session.getCustomerId(Profile_Activity.this));

        servicesrecy.setHasFixedSize(true);
        servicesrecy.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
      //  servicesrecy.addItemDecoration(new DividerItemDecorator(serviceautope.size(),2,false));
        servicesrecy.setNestedScrollingEnabled(false);


        bankrecycler.setHasFixedSize(true);
        bankrecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
       // bankrecycler.addItemDecoration(new DividerItemDecorator(serviceautope.size(),2,false));
        bankrecycler.setNestedScrollingEnabled(false);

    }


    BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener =new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.bottom_home:
                    backbuttonfun();
                    break;
                case R.id.bottom_profile:
                    break;
                case R.id.bottom_history:
                    startActivity(new Intent(Profile_Activity.this,History.class));
                    break;
                case R.id.bottom_help:
                    startActivity(new Intent(Profile_Activity.this,Help.class));
                    break;
            }
            return true;
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            backbuttonfun();
             return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                backbuttonfun();
                break;
            case R.id.downloadreport:
                permissionUtils.check_permission(PermissionHandler.fileDownloadAndReadPermissionArrayList(Profile_Activity.this), Content_Message.DOWNLOAD_PERMISSION, ApplicationConstant.REQ_DOWNLOAD_PERMISSION);
                break;
            case R.id.emailverify:
                String[] buttons = {"No","Yes"};
                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        Utility.dismissDialog(Profile_Activity.this,dialog);
                        sendOtpToEmailVerify("email",email.getText().toString());
                    }
                    @Override
                    public void modify(Dialog dialog) {
                        Utility.dismissDialog(Profile_Activity.this,dialog);
                    }
                },this,null,"Would you like to verify the email ?\n"+email.getText().toString(),"Alert",buttons);
                break;
            case R.id.more_service:
                startActivityForResult(new Intent(Profile_Activity.this,AdditionalService.class),REQ_ADD_MORE_SERVICE);
                break;
            case R.id.imageView1:
                permissionUtils.check_permission(PermissionHandler.imagePermissionArrayList(Profile_Activity.this), Content_Message.CAMERA_PERMISSION,ApplicationConstant.REQ_CAMERA_PERMISSION);
                break;
            case R.id.changepass:
                String[] changePass = {"No","Yes"};
                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        Utility.dismissDialog(Profile_Activity.this,dialog);

                        Intent intent =new Intent(Profile_Activity.this,ChangePassword.class);
                        intent.putExtra("customerid",Session.getCustomerId(Profile_Activity.this));
                        intent.putExtra("methodname","setCustomerChangePassword");
                        startActivityForResult(intent,REQ_CHANGE_PASS);
                    }
                    @Override
                    public void modify(Dialog dialog) {
                        Utility.dismissDialog(Profile_Activity.this,dialog);
                    }
                },this,null,"Would you like change pin ?","Alert",changePass);
                break;
            case R.id.more_bankadd:
                startActivityForResult(new Intent(Profile_Activity.this,AddBankAndServicelist.class),REQ_ADDBANK_ANDSERVICE);
                break;
        }
    }

    public void startCamera(){
            AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
            pictureDialog.setTitle("Select Action");
            String[] pictureDialogItems = {
                    "Select photo from gallery",
                    "Capture photo from camera",
                    "Remove profile photo"
            };
            pictureDialog.setItems(pictureDialogItems,
                    new android.content.DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(android.content.DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    galleryimage();
                                    break;
                                case 1:
                                    cameraimage();
                                    break;
                                case 2:
                                    removeProfileImage();
                                    break;
                            }
                    }
                    });
            pictureDialog.show();
    }



    private void removeProfileImage(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO =CustomerBO.removeProfileImage();

        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(this)));
        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson=new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Profile_Activity.this,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                    imageView1.setImageBitmap(null);
                    //imageView1.setImageBitmap(Utility.drawableToBitmap(getDrawable(R.drawable.noprofileimage)));
                    imageView1.setBackgroundResource(R.drawable.ava_user);
                }
            }
        });
    }


    public void galleryimage(){
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent,REQ_GALLERY );

    }

    public void cameraimage(){
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        photofileurl=null;
        try
        {
            // place where to store camera taken picture
            photofileurl = Utility.createTemporaryFile("picture", ".jpg",Profile_Activity.this);
            photofileurl.delete();
            Uri mImageUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", photofileurl);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQ_IMAGE);
        }
        catch(Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }
    public void backbuttonfun(){
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();


    }



    public void sendOtpToEmailVerify(String type,String emailId){
      OTPApi.sendOtpToEmailVerification(Profile_Activity.this,emailId,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
          CustomerVO customerVO = (CustomerVO) s;
          customerVO.setUserid(emailId);
          customerVO.setLoginType(type);
          Intent intent=new Intent(Profile_Activity.this,Verify_Email_Otp_By_Id.class);

          customerVO.setActionname("emailOTPVerification");
          String json = new Gson().toJson(customerVO); // myObject - instance of MyObject
          intent.putExtra("resp",json);
          startActivityForResult(intent,REQ_EMAIL_VERIFY);
      }));
    }
    

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode==RESULT_OK){
                if(requestCode==REQ_EMAIL_VERIFY ){
                    if(data!=null){
                        getProfileDate(Session.getCustomerId(Profile_Activity.this));
                    }
                }else if(requestCode==REQ_ADD_MORE_SERVICE){
                        getProfileDate(Session.getCustomerId(Profile_Activity.this));
                }else if (requestCode == REQ_IMAGE) {
                    try {
                        bmp =Utility.decodeImageFromFiles(Uri.fromFile(photofileurl).getPath(),500,500);
                        if(bmp.getWidth()>bmp.getHeight()){
                            bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),Utility.getImageMatrix(Profile_Activity.this,photofileurl),true);
                        }
                        imageView1.setImageBitmap(bmp);
                        performCrop(Utility.getVersionWiseUri(Profile_Activity.this,photofileurl));
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
                    }

                }else if(requestCode==REQ_GALLERY){
                    try {
                        Uri contentURI = data.getData();
                        bmp= Utility.decodeImageFromFiles(Utility.getPathByUri(Profile_Activity.this,contentURI) ,500,500);
                        if(bmp.getWidth()>bmp.getHeight()){
                            bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),Utility.getImageMatrix(Profile_Activity.this,new File(Objects.requireNonNull(contentURI.getPath()))),true);
                        }
                        imageView1.setImageBitmap(bmp);
                        performCrop(contentURI);
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
                    }
                }else  if(requestCode==PIC_CROP){
                    //get the returned data
                    try {
                        Bundle extras = data.getExtras();
                        //get the cropped bitmap
                        bmp = (Bitmap) extras.get("data");
                        //display the returned cropped image
                        imageView1.setImageBitmap(bmp);
                        setCustomerProfileImage();
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
                    }
                }else if(requestCode==REQ_CHANGE_PASS){
                    Utility.showSingleButtonDialog(Profile_Activity.this,"Alert","Pin Successfully Updated ",false);
                }else if(requestCode==REQ_ADDBANK_ANDSERVICE){
                    getProfileDate(Session.getCustomerId(Profile_Activity.this));
                }
            }else{
                if(requestCode==PIC_CROP){
                    imageView1.setImageBitmap(bmp);
                    setCustomerProfileImage();
                }
            }
        }catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }



    private void performCrop(Uri picUri){
        try {
            //call the standard crop action intent (the user device may not support it)
            Intent cropIntent = new Intent( "com.android.camera.action.CROP");
            //indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");

            //set crop properties
            cropIntent.putExtra("crop", "true");
            //indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            //indicate output X and Y
            cropIntent.putExtra("outputX", 256);
            cropIntent.putExtra("outputY", 256);
            //retrieve data on return
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, PIC_CROP);
        }
        catch(ActivityNotFoundException anfe){
            //display an error message
           /* String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();*/
            imageView1.setImageBitmap(bmp);
            setCustomerProfileImage();
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
           // Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    private void setCustomerProfileImage(){
        BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
            @Override
            public void doInBackGround() {

                HashMap<String, Object> params = new HashMap<String, Object>();
                CustomerVO customerVO=new CustomerVO();
                customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Profile_Activity.this)));
                customerVO.setImage(Utility.BitMapToString(bmp,500,true));

                Gson gson = new Gson();
                String json = gson.toJson(customerVO);
                params.put("volley", json);
                customerProfileImage.setParams(params);

            }

            @Override
            public void doPostExecute() {
                VolleyUtils.makeJsonObjectRequest(Profile_Activity.this, customerProfileImage, new VolleyResponseListener() {
                    @Override
                    public void onError(String message) {
                    }
                    @Override
                    public void onResponse(Object resp) throws JSONException {

                        JSONObject response = (JSONObject) resp;
                        Gson gson=new Gson();
                        CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);
                        if(customerVO.getStatusCode().equals("400")){
                            ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                            StringBuilder sb = new StringBuilder();
                            for(int i=0; i<error.size(); i++){
                                sb.append(error.get(i)).append("\n");
                            }
                            Utility.alertDialog(Profile_Activity.this,customerVO.getDialogTitle(),sb.toString(),"Ok");
                        }else {
                            Drawable drawable=Utility.getDrawableResources(Profile_Activity.this,R.drawable.ava_user);
                            Picasso.with(Profile_Activity.this).load(customerVO.getImage()).transform(new CircleTransform())
                                    .memoryPolicy(MemoryPolicy.NO_CACHE )
                                    .networkPolicy(NetworkPolicy.NO_CACHE)
                                    .error(R.drawable.autodebitlogo)
                                    .into(imageView1, new Callback() {
                                        @Override
                                        public void onSuccess() {
                                            progressBar.setVisibility(View.GONE);

                                        }
                                        @Override
                                        public void onError() {
                                            progressBar.setVisibility(View.GONE);
                                        }
                                    });
                        }
                    }
                });
            }
        });
        backgroundAsyncService.execute();
    }


    private void getProfileDate(String id){
        progressBar.setVisibility(View.VISIBLE);

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = CustomerBO.getProfileData();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(id));
        Gson gson = new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;

                Log.w("responsesignup",response.toString());

                Gson gson=new Gson();

                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);
                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Profile_Activity.this,customerVO.getDialogTitle(),sb.toString(),false);
                }else {
                    scrollView.setVisibility(View.VISIBLE);
                    setServiceAndBankList(customerVO);
                    usename.setText(customerVO.getName());

                    if(customerVO.getLevel().getLevelId()<=1){
                        pannumber.setText("---");
                        citystate.setText("---");
                        pincode.setText("---");
                        address.setText("---");

                    }else {
                        pannumber.setText(customerVO.getPanNo());
                        citystate.setText(customerVO.getCity().getCityName() + ",  "+customerVO.getStateRegion().getStateRegionName());
                        pincode.setText(customerVO.getPincode());
                        address.setText(customerVO.getAddress1());
                    }
                    mobileno.setText( "+91 "+customerVO.getMobileNumber());
                    email.setText(customerVO.getEmailId());


                    if(customerVO.getImage()!=null){
                        Picasso.with(Profile_Activity.this).load(customerVO.getImage()).transform(new CircleTransform())
                                .memoryPolicy(MemoryPolicy.NO_CACHE )
                                .networkPolicy(NetworkPolicy.NO_CACHE)
                                .error(R.drawable.autodebitlogo)
                                .into(imageView1, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                    @Override
                                    public void onError() {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                });
                    }else {
                        progressBar.setVisibility(View.GONE);
                        //imageView1.setImageBitmap(Utility.drawableToBitmap(getDrawable(R.drawable.noprofileimage)));
                        imageView1.setBackgroundResource(R.drawable.ava_user);
                    }

                    if(customerVO.getMobileVerified()==null){
                        mobileverify.setImageResource(R.drawable.warning);
                    }else {
                        mobileverify.setImageResource(R.drawable.iconcheck);
                    }

                    if(customerVO.getEmailVerified()==null){

                        emailverify.setImageResource(R.drawable.warning);
                        emailverify.setOnClickListener(Profile_Activity.this);

                    }else {

                        emailverify.setImageResource(R.drawable.iconcheck);
                        emailverify.setOnClickListener(null);
                    }

                    if(customerVO.getLevel().getLevelId()<=2){
                        creditscore.setText("---" );
                    }else {
                        creditscore.setText("Credit Score : "+customerVO.getCreditScore() );
                        cir_report=customerVO.getCirPdfPath();
                        if(cir_report!=null) downloadreport.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    public void setServiceAndBankList(final CustomerVO customerVO){
        BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,false, new BackgroundServiceInterface() {
            @Override
            public void doInBackGround() {
                try {
                    bankServiceList.clear();
                    addservice.clear();

                    JSONArray bankArry=new JSONArray(customerVO.getEnachDetails());
                    for(int i=0;i<bankArry.length();i++){
                        ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                        JSONObject object =bankArry.getJSONObject(i);
                        serviceTypeVO.setTitle(object.getString("bankName")+" \n"+object.getString("accountNumber"));
                        serviceTypeVO.setAppIcon("bankicon.png");
                        serviceTypeVO.setAnonymousInteger(object.getInt("customerAuthId"));
                        bankServiceList.add(serviceTypeVO);
                    }


                    // change this active service To server
                    List<ServiceTypeVO> serviceautope  =new Gson().fromJson(customerVO.getAnonymousString(), new TypeToken<ArrayList<ServiceTypeVO>>() { }.getType());
                    addservice.addAll(serviceautope);



                }catch (Exception e){
                    Log.e("profile_activity",e.getMessage());
                    e.printStackTrace();
                }
            }
            @Override
            public void doPostExecute() {
                RecyclerViewProfileBankAdapterMenu recyclerViewProfileBankAdapterMenu=new RecyclerViewProfileBankAdapterMenu(Profile_Activity.this, bankServiceList,R.layout.profile_bankservice_design);
                bankrecycler.setAdapter(recyclerViewProfileBankAdapterMenu);

                recyclerViewAdapter=new RecyclerViewAdapterMenu(Profile_Activity.this, addservice,R.layout.profile_service_design);
                servicesrecy.setAdapter(recyclerViewAdapter);
            }
        });
        backgroundAsyncService.execute();
    }

    @Override
    public void downloadComplete(File file) {
        try {

            Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, "application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(intent);

            }else{
                Uri apkUri = Uri.fromFile(file);
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.setDataAndType(apkUri,"application/pdf");
                install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(install);
            }

        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(Profile_Activity.this , Utility.getStackTrace(e));
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

    public void bankDetails(int customerAuthId){
        startActivity(new Intent(Profile_Activity.this,Confirm_Bank_Details.class).putExtra("bankid",customerAuthId));
    }

    public void showBankDetailsDialog(CustomerAuthServiceVO customerAuthServiceVO) throws Exception{


            String[] changePass = {"Cancel","Modify"};
            JSONArray jsonArray=new JSONArray();
            JSONObject object =new JSONObject();
            object.put("key","Bank Name");
            object.put("value",customerAuthServiceVO.getBankName());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Account No");
            object.put("value",customerAuthServiceVO.getAccountNumber());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Status");
            object.put("value",customerAuthServiceVO.getAuthStatus().getStatusName());
            jsonArray.put(object);

            object =new JSONObject();
            object.put("key","Mandate Amount");
            object.put("value",customerAuthServiceVO.getMandateAmount());
            jsonArray.put(object);

            Utility.confirmationDialog(new DialogInterface() {
                @Override
                public void confirm(Dialog dialog) {
                    Utility.dismissDialog(Profile_Activity.this,dialog);
                }
                @Override
                public void modify(Dialog dialog) {
                    Utility.dismissDialog(Profile_Activity.this,dialog);
                }
            },Profile_Activity.this,jsonArray,null,"Bank Detail",changePass);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionUtils.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }

    @Override
    public void PermissionGranted(int request_code) {
        if(request_code==ApplicationConstant.REQ_CAMERA_PERMISSION){
            startCamera();
        }else if(request_code==ApplicationConstant.REQ_DOWNLOAD_PERMISSION){
            new DownloadTask(Profile_Activity.this,Profile_Activity.this, cir_report);
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

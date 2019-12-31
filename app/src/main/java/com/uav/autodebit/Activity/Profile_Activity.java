package com.uav.autodebit.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.CustomerBO;
import com.uav.autodebit.BO.SignUpBO;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewItemCheckboxBaseAdapter;
import com.uav.autodebit.adpater.RecyclerViewAdapterMenu;
import com.uav.autodebit.adpater.RecyclerViewProfileBankAdapterMenu;
import com.uav.autodebit.adpater.UitilityAdapter;
import com.uav.autodebit.androidFragment.Home_Menu;
import com.uav.autodebit.androidFragment.Profile;
import com.uav.autodebit.override.CircularImageView;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.DownloadTask;
import com.uav.autodebit.util.FileDownloadInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerStatusVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.OTPVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Profile_Activity extends AppCompatActivity implements FileDownloadInterface , View.OnClickListener {

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

    Uri mImageUri;
    int  REQ_IMAGE=1001,REQ_GALLERY=1002,REQ_ENACH_MANDATE=1003;
    Bitmap bmp;

    ConnectionVO customerProfileImage;
    ProgressBar progressBar;


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


        back_activity_button.setOnClickListener(this);
        more_service.setOnClickListener(this);
        changepass.setOnClickListener(this);
        more_bankadd.setOnClickListener(this);


        mobileverify=findViewById(R.id.mobileverify);
        emailverify=findViewById(R.id.emailverify);
        addressverify=findViewById(R.id.addressverify);

        downloadreport=findViewById(R.id.downloadreport);
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
                    Toast.makeText(Profile_Activity.this, "bottom_history", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.bottom_help:
                    Toast.makeText(Profile_Activity.this, "bottom_help", Toast.LENGTH_SHORT).show();
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
                if(PermissionHandler.filedownloadandread(Profile_Activity.this)){
                    new DownloadTask(Profile_Activity.this,Profile_Activity.this, cir_report);
                }
                break;
            case R.id.emailverify:
                String[] buttons = {"No","Yes"};
                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();
                        resendotpfun("email",email.getText().toString());
                    }

                    @Override
                    public void modify(Dialog dialog) {
                        dialog.dismiss();


                    }
                },this,null,"Would you like email verify ?"+email.getText().toString(),"Please Confirm Detail",buttons);


                break;
            case R.id.more_service:
                startActivityForResult(new Intent(Profile_Activity.this,AdditionalService.class),200);
                break;
            case R.id.imageView1:
                    if (PermissionHandler.imagePermission(this)) {
                        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
                        pictureDialog.setTitle("Select Action");
                        String[] pictureDialogItems = {
                                "Select photo from gallery",
                                "Capture photo from camera"};
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
                                        }
                                    }
                                });
                        pictureDialog.show();
                    }

                break;
            case R.id.changepass:
                String[] changePass = {"No","Yes"};
                Utility.confirmationDialog(new DialogInterface() {
                    @Override
                    public void confirm(Dialog dialog) {
                        dialog.dismiss();


                        Intent intent =new Intent(Profile_Activity.this,ChangePassword.class);
                        intent.putExtra("customerid",Session.getCustomerId(Profile_Activity.this));
                        intent.putExtra("methodname","setCustomerChangePassword");
                        startActivityForResult(intent,300);
                    }

                    @Override
                    public void modify(Dialog dialog) {
                        dialog.dismiss();


                    }
                },this,null,"Would you like change password ?","Please Confirm Detail",changePass);

                break;
            case R.id.more_bankadd:
                startActivityForResult(new Intent(Profile_Activity.this,Enach_Mandate.class).putExtra("activity",getPackageName()+".Activity.Profile_Activity").putExtra("forresutl",true),REQ_ENACH_MANDATE);
                break;
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
            Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }



    public void backbuttonfun(){
        Intent intent = new Intent(getApplicationContext(), Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }



    public void resendotpfun(final String type, final String value){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SignUpBO.resendOTP();

        OTPVO otpvo=new OTPVO();
        if(type.equals("mobile")){
            otpvo.setMobileNo(value);
        }else if(type.equals("email")){
            otpvo.setEmailId(value);
        }
        Gson gson = new Gson();
        String json = gson.toJson(otpvo);
        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerVO customerVO = gson.fromJson(response.toString(), CustomerVO.class);


                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(Profile_Activity.this,"Alert",sb.toString(),"Ok");

                }else {
                    customerVO.setUserid(value);
                    customerVO.setLoginType(type);

                    Intent intent=new Intent(Profile_Activity.this,Verify_OTP.class);
                    customerVO.setActionname("verifySignUp");
                    // customerVO.setAnonymousString(customerVO.getOtpExpiredMobile().toString());
                    String json = gson.toJson(customerVO); // myObject - instance of MyObject
                    intent.putExtra("resp",json);
                    startActivityForResult(intent,100);


                }
            }
        });
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if(resultCode==RESULT_OK){
                if(requestCode==100 ){
                    if(data!=null){

                        getProfileDate(Session.getCustomerId(Profile_Activity.this));
                    }
                }else if(requestCode==200){
                    getProfileDate(Session.getCustomerId(Profile_Activity.this));
                }else if (requestCode == REQ_IMAGE) {
                    bmp =Utility.decodeImageFromFiles(mImageUri.getPath(),150,150);
                    if(bmp.getWidth()>bmp.getHeight()){
                        Matrix matrix =new Matrix();
                        matrix.postRotate(90);
                        bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
                    }
                    imageView1.setImageBitmap(bmp);
                    View current = getCurrentFocus();
                    if (current != null) current.clearFocus();
                    setCustomerProfileImage();

                }else if(requestCode==REQ_GALLERY){
                    Uri contentURI = data.getData();
                    bmp =VolleyUtils.grabImage(contentURI,this);
                    if(bmp.getWidth()>bmp.getHeight()){
                        Matrix matrix =new Matrix();
                        matrix.postRotate(90);
                        bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
                    }
                    imageView1.setImageBitmap(bmp);

                    setCustomerProfileImage();
                }else if(requestCode==300){
                    Utility.showSingleButtonDialog(Profile_Activity.this,"","Success fully update ",false);
                }else if(requestCode==REQ_ENACH_MANDATE){
                    Toast.makeText(context, "sfdsdfsfd", Toast.LENGTH_SHORT).show();
                }
            }
        }catch (Exception e) {
            Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
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
                            Utility.alertDialog(Profile_Activity.this,"Alert",sb.toString(),"Ok");
                        }else {

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

                setServiceAndBankList(customerVO);


                if(customerVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(Profile_Activity.this,"Alert",sb.toString(),"Ok");
                }else {

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
                        Picasso.with(Profile_Activity.this).load(customerVO.getImage()).fit()
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
                        cir_report=response.getString("cirPdfPath");
                        if(!cir_report.equals("null")) downloadreport.setVisibility(View.VISIBLE);
                        Log.w("cirreportpath",cir_report);
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
                    Gson gson =new Gson();
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

                    LocalCacheVO localCacheVO = gson.fromJson(customerVO.getLocalCache(), LocalCacheVO.class);
                    List<ServiceTypeVO> serviceautope = localCacheVO.getUtilityBills();

                    List<ServiceTypeVO> utilityServices = localCacheVO.getSerives();

                    for(ServiceTypeVO serviceTypeVO : utilityServices){
                        if(serviceTypeVO.getAdopted()==1){
                            addservice.add(serviceTypeVO);
                        }
                    }


                    for(ServiceTypeVO utility:serviceautope){
                        if(utility.getAdopted()==1){
                            addservice.add(utility);
                        }
                    }
                   /* int[] my_array = {15};
                    Arrays.sort(my_array);
                    for(ServiceTypeVO utility:serviceautope){
                        int index = Arrays.binarySearch(my_array,utility.getServiceTypeId() );
                        if(utility.getAdopted()==1 && index==-1){
                            addservice.add(utility);
                        }
                    }*/



                }catch (Exception e){
                    Log.e("profile_activity",e.getMessage());
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

    public void bankDetails(int customerAuthId){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = CustomerBO.cutomerBankDetails();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Profile_Activity.this)));

        CustomerAuthServiceVO customerAuthServiceVO=new CustomerAuthServiceVO();
        customerAuthServiceVO.setCustomerAuthId(customerAuthId);
        customerAuthServiceVO.setCustomer(customerVO);


        Gson gson = new Gson();
        String json = gson.toJson(customerAuthServiceVO);
        params.put("volley", json);
        connectionVO.setParams(params);

        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                CustomerAuthServiceVO customerAuthServiceVO = gson.fromJson(response.toString(), CustomerAuthServiceVO.class);


                if(customerAuthServiceVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) customerAuthServiceVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(Profile_Activity.this,"Alert",sb.toString(),"Ok");

                }else {
                    try {
                        showBankDetailsDialog(customerAuthServiceVO);
                    }catch (Exception e){
                        Utility.exceptionAlertDialog(Profile_Activity.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                    }


                }
            }
        });

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
                    dialog.dismiss();
                }
                @Override
                public void modify(Dialog dialog) {
                    dialog.dismiss();
                }
            },Profile_Activity.this,jsonArray,null,"Bank Detail",changePass);
    }
}

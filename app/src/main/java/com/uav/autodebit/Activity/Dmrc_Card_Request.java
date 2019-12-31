package com.uav.autodebit.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.PinCodeBO;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewItemCheckboxBaseAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.CommonUtils;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.LocalCacheVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Dmrc_Card_Request extends AppCompatActivity implements View.OnClickListener {

    EditText customername,pin,city,state,permanentaddress,mobilenumber;

    TextView attachaddress,changeaddress,cardcharges;
    Button verify;
    Uri mImageUri;
    int  REQ_IMAGE=1001,REQ_GALLERY=1002;
    Bitmap bmp;

    ImageView addressimage,back_activity_button1 ;

    boolean permissionstate=true;
    String customerId;

    Gson gson = new Gson();
    boolean isdisable;

    UAVProgressDialog pd;
    String stringimg=null;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dmrc__card__request);
        getSupportActionBar().hide();

        pd=new UAVProgressDialog(this);

        mobilenumber=findViewById(R.id.mobilenumber);
        customername=findViewById(R.id.customername);
        pin=findViewById(R.id.pin);
        city=findViewById(R.id.city);
        state=findViewById(R.id.state);
        permanentaddress=findViewById(R.id.permanentaddress);
        attachaddress=findViewById(R.id.attachaddress);
        verify=findViewById(R.id.verify);
        addressimage=findViewById(R.id.addressimage);
        changeaddress=findViewById(R.id.changeaddress);
        cardcharges=findViewById(R.id.cardcharges);

        back_activity_button1=findViewById(R.id.back_activity_button);

        pin.setInputType(InputType.TYPE_CLASS_NUMBER);

        city.setKeyListener(null);
        state.setKeyListener(null);

        isdisable=getIntent().getBooleanExtra("isdisable",true);
        String onetimeamt=Session.getSessionByKey(Dmrc_Card_Request.this,Session.CACHE_DMRC_MIN_CARD_CHARGE);

        cardcharges.setText("* INR "+onetimeamt +" One Time Charges");

        customerId= Session.getCustomerId(Dmrc_Card_Request.this);



        setCustomerDetail();


        back_activity_button1.setOnClickListener(this);
        attachaddress.setOnClickListener(this);
        changeaddress.setOnClickListener(this);
        verify.setOnClickListener(this);






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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                finish();
                break;

            case R.id. attachaddress:

                PermissionHandler.checkpermission(Dmrc_Card_Request.this);
                if(!permissionstate) return;
                try
                {
                    AlertDialog.Builder pictureDialog = new AlertDialog.Builder(Dmrc_Card_Request.this);

                    pictureDialog.setTitle("Select Action");
                    String[] pictureDialogItems = {
                            "Select photo from gallery",
                            "Capture photo from camera" };
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
                catch(Exception e)
                {
                    Utility.exceptionAlertDialog(Dmrc_Card_Request.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                }
               break;
            case R.id. changeaddress :
                enabledAllEle(true);
                break;
            case R.id.verify :


                if(!requiredfiled()) return;
                if(Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION)!=null){
                    mobilenumber.setError(Utility.validatePattern(mobilenumber.getText().toString().trim(),ApplicationConstant.MOBILENO_VALIDATION));
                    return;
                }
                if(pin.getText().toString().trim().length()<6){
                    pin.setError("Pincode is Wrong");
                    return;
                }

                Utility.hideKeyboard(Dmrc_Card_Request.this);








                final Dialog var3 = new Dialog(Dmrc_Card_Request.this);
                var3.requestWindowFeature(1);
                var3.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                var3.setContentView(R.layout.dmrc_catd_request_verify_design);
                var3.setCanceledOnTouchOutside(false);
                var3.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


                TextView name=var3.findViewById(R.id.name);
                TextView mobile =var3.findViewById(R.id.mobile);
                TextView address =var3.findViewById(R.id.address);
                ImageView canceldialog=var3.findViewById(R.id.canceldialog);
                Button modify=var3.findViewById(R.id.modify);
                Button next=var3.findViewById(R.id.next);

                name.setText("  "+customername.getText().toString());
                mobile.setText("  "+mobilenumber.getText().toString());
                address.setText("  "+permanentaddress.getText().toString());


                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(var3.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                canceldialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        var3.dismiss();
                    }
                });

                modify.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        var3.dismiss();
                    }
                });

                next.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        var3.dismiss();

                        if(bmp==null){
                            saveDmrcCardInServer(var3);
                        }else {
                            BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                                @Override
                                public void doInBackGround() {
                                    stringimg= Utility.BitMapToString(bmp);
                                }
                                @Override
                                public void doPostExecute() {
                                    pd.dismiss();
                                    saveDmrcCardInServer(var3);
                                }
                            });
                            backgroundAsyncService.execute();
                        }
                    }
                });

                var3.show();
                var3.getWindow().setAttributes(lp);


                break;
        }
    }




    public void saveDmrcCardInServer(final Dialog dialog){



        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.saveDmarcCards();

            DMRC_Customer_CardVO dmrc_customer_cardVO=new DMRC_Customer_CardVO();
            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Dmrc_Card_Request.this)));

            dmrc_customer_cardVO.setCustomer(customerVO);
            dmrc_customer_cardVO.setCustomerName(customername.getText().toString());
            dmrc_customer_cardVO.setMobileNumber(mobilenumber.getText().toString());
            dmrc_customer_cardVO.setAddress(permanentaddress.getText().toString());
            dmrc_customer_cardVO.setPincode(pin.getText().toString());

            if(bmp!=null){
                dmrc_customer_cardVO.setImage(stringimg);
            }

            Gson gson =new Gson();

            String json = gson.toJson(dmrc_customer_cardVO);

            params.put("volley", json);

            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(Dmrc_Card_Request.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    DMRC_Customer_CardVO dmrc_customer_cardVO = gson.fromJson(response.toString(), DMRC_Customer_CardVO.class);
                    dialog.hide();

                    if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                        //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                        ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        // Utility.alertDialog(PanVerification.this,"Alert",sb.toString(),"Ok");
                        Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !",sb.toString(),false);
                    }else {

                        Intent intent =new Intent(Dmrc_Card_Request.this,DMRC_Cards_List.class);
                        intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                        startActivity(intent);
                        finish();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  boolean requiredfiled(){
        mobilenumber.setError(null);
        customername.setError(null);
        pin.setError(null);
        city.setError(null);
        state.setError(null);
        permanentaddress.setError(null);




        boolean filed=true;
        if(mobilenumber.getText().toString().trim().equals("")){
            mobilenumber.setError("This filed is required");
            filed=false;
        }
        if(customername.getText().toString().trim().equals("")){
            customername.setError("This filed is required");
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






    public void  enabledAllEle(Boolean ele){
            customername.setEnabled(ele);
            mobilenumber.setEnabled(ele);
            pin.setEnabled(ele);
            city.setEnabled(ele);
            state.setEnabled(ele);
            permanentaddress.setEnabled(ele);

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
                    Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !",sb.toString(),false);
                    city.setText("");
                    state.setText("");
                 }else {

                    state.setText(cityVO.getCityName());
                    city.setText(cityVO.getStateRegion().getStateRegionName());
                    city.setError(null);
                    state.setError(null);
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
            Utility.exceptionAlertDialog(Dmrc_Card_Request.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }
    public void setCustomerDetail(){
        CustomerVO customerVO = gson.fromJson(Session.getSessionByKey(Dmrc_Card_Request.this,Session.CACHE_CUSTOMER), CustomerVO.class);


        if(customerVO.getPanHolderName()!=null){
            customername.setText(customerVO.getPanHolderName());
        }
        if(customerVO.getMobileNumber()!=null){
            mobilenumber.setText(customerVO.getMobileNumber());
        }
        if(customerVO.getPincode()!=null){
            pin.setText(customerVO.getPincode());
        }
        if(customerVO.getCity()!=null && customerVO.getCity().getCityName()!=null){
            city.setText(customerVO.getCity().getCityName());
        }
        if(customerVO.getStateRegion()!=null && customerVO.getStateRegion().getStateRegionName()!=null){
            state.setText(customerVO.getStateRegion().getStateRegionName());
        }
        if(customerVO.getAddress1()!=null){
            permanentaddress.setText(customerVO.getAddress1());
        }

        if(isdisable) enabledAllEle(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == REQ_IMAGE) {

/*
                    String image_path=CommonUtils.compressImage(mImageUri.getPath());
                    Log.w("image_path",image_path);

                    File file =new File(image_path);
                    Log.w("file_length",file.length()/1024+"");
*/

                    bmp=Utility.decodeImageFromFiles(mImageUri.getPath(),150,150);

                    int imagesizeinbyte=Utility.byteSizeOf(bmp);

                    Log.w("image",imagesizeinbyte +"  ====   "+(imagesizeinbyte/1024) +"");

                    if(bmp.getWidth()>bmp.getHeight()){
                        Matrix matrix =new Matrix();
                        matrix.postRotate(90);
                        bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
                    }

                    imagesizeinbyte=Utility.byteSizeOf(bmp);
                    Log.w("image",imagesizeinbyte +"  ====   "+(imagesizeinbyte/1024) +"");

                    addressimage.setImageBitmap(bmp);

                    View current = getCurrentFocus();
                    if (current != null) current.clearFocus();
                }
                if(requestCode==REQ_GALLERY){
                    Uri contentURI = data.getData();
                    bmp =VolleyUtils.grabImage(contentURI,Dmrc_Card_Request.this);
                    addressimage.setImageBitmap(bmp);
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utility.exceptionAlertDialog(Dmrc_Card_Request.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
            }
        }
    }


}

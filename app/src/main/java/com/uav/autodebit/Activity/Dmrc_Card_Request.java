package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.MetroBO;
import com.uav.autodebit.BO.PinCodeBO;
import com.uav.autodebit.BO.SiBO;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.BigContentDialogIntetface;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.CustomPagerAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.constant.ErrorMsg;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.PermissionUtils;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundAsyncServiceGetList;
import com.uav.autodebit.util.BackgroundAsyncServiceGetListInterface;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CityVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Dmrc_Card_Request extends Base_Activity implements View.OnClickListener ,PermissionUtils.PermissionResultCallback , ActivityCompat.OnRequestPermissionsResultCallback{

    EditText customername,pin,city,state,permanentaddress,mobilenumber;

    TextView attachaddress,changeaddress,cardcharges;
    Button verify;
    Uri mImageUri;
    int  REQ_IMAGE=1001,REQ_GALLERY=1002,PIC_CROP=1004;
    Bitmap bmp;
    ImageView addressimage,back_activity_button1 ;
    boolean permissionstate=true;
    String customerId;
    Gson gson = new Gson();
    boolean isdisable;
    UAVProgressDialog pd;
    String stringimg=null;

    LinearLayout addcardlistlayout;
    DMRC_Customer_CardVO dmrc_customer_cardVO;


    RecyclerView recyclerView;
    ViewPager viewPager;
    ScrollView scrollView;

    int serviceId=ApplicationConstant.Dmrc;
    TabLayout tabLayout;
    PermissionUtils permissionUtils;
    File photofileurl;

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
        addcardlistlayout=findViewById(R.id.addcardlistlayout);
        scrollView=findViewById(R.id.scrollView);

        tabLayout =findViewById(R.id.indicator);

        back_activity_button1=findViewById(R.id.back_activity_button);

        pin.setInputType(InputType.TYPE_CLASS_NUMBER);
        city.setKeyListener(null);
        state.setKeyListener(null);
        permissionUtils=new PermissionUtils(Dmrc_Card_Request.this);

        addcardlistlayout.removeAllViews();

        isdisable = getIntent().getBooleanExtra("isdisable",true);
        String onetimeamt = Session.getSessionByKey(Dmrc_Card_Request.this,Session.CACHE_DMRC_MIN_CARD_CHARGE);
        cardcharges.setText("* INR "+onetimeamt +" One Time Charges");
        customerId = Session.getCustomerId(Dmrc_Card_Request.this);

        dmrc_customer_cardVO = gson.fromJson(getIntent().getStringExtra("dmrccard"), DMRC_Customer_CardVO.class);
        addRequestDmrcCardBanner(dmrc_customer_cardVO);

        setCustomerDetail(dmrc_customer_cardVO);

        back_activity_button1.setOnClickListener(this);


        //25-05-2020
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

    public void addRequestDmrcCardBanner(DMRC_Customer_CardVO dmrc_customer_cardVO){
        addcardlistlayout.removeAllViewsInLayout();

        if(dmrc_customer_cardVO.getDmrcCustomerList()!=null && dmrc_customer_cardVO.getDmrcCustomerList().size()>0){

            //Show Addcard btn
            if(dmrc_customer_cardVO.getDmrcid()==null){
                showAddCardBtn();
            }
          /*ArrayList<DMRC_Customer_CardVO> listforcard= (ArrayList<DMRC_Customer_CardVO>) dmrc_customer_cardVO.getDmrcCustomerList();
            recyclerView =Utility.getRecyclerView(Dmrc_Card_Request.this);
            recyclerView.setNestedScrollingEnabled(true);
            recyclerView.setHasFixedSize(false);
            recyclerView.setLayoutManager(new LinearLayoutManager(Dmrc_Card_Request.this, LinearLayoutManager.HORIZONTAL, false));
            getdata(listforcard);*/

            ArrayList<DMRC_Customer_CardVO> listforcard= (ArrayList<DMRC_Customer_CardVO>) dmrc_customer_cardVO.getDmrcCustomerList();
           /* viewPager=Utility.getViewPager(Dmrc_Card_Request.this);
            viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);*/
            getdata(listforcard);
        }else{
            CardView cardView =Utility.getCardViewStyle(Dmrc_Card_Request.this);
            ImageView imageView =Utility.getImageView(Dmrc_Card_Request.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            Picasso.with(this)
                    .load("http://autope.in/images/apk/1577709175082.jpeg")
                    .into(imageView, new com.squareup.picasso.Callback() {
                        @Override
                        public void onSuccess() {
                            //do smth when picture is loaded successfully
                        }
                        @Override
                        public void onError() {
                        }
                    });
            cardView.addView(imageView);
            addcardlistlayout.addView(cardView);
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        scrollView.fullScroll(ScrollView.FOCUS_UP);
    }

    public void getdata(ArrayList<DMRC_Customer_CardVO> listforcard){
        BackgroundAsyncServiceGetList backgroundAsyncServiceGetList =new BackgroundAsyncServiceGetList(pd, false, new BackgroundAsyncServiceGetListInterface.BackgroundServiceInterface() {
            @Override
            public List doInBackGround(BackgroundAsyncServiceGetListInterface backgroundAsyncServiceGetListInterface) {

                ArrayList<DMRC_Customer_CardVO> dmrc_customer_cardVOS=new ArrayList<>();
                for(DMRC_Customer_CardVO dmrc_customer_cardVOS1 :listforcard ){
                    DMRC_Customer_CardVO dmrc_customer_cardVO =new DMRC_Customer_CardVO();
                    dmrc_customer_cardVO.setCustomerName(dmrc_customer_cardVOS1.getCustomerName());
                    dmrc_customer_cardVO.setCardNo(dmrc_customer_cardVOS1.getCardNo());
                    DmrcCardStatusVO dmrcCardStatusVO =new DmrcCardStatusVO();
                    dmrcCardStatusVO.setStatusName(dmrc_customer_cardVOS1.getDmrccardStaus().getStatusName());
                    dmrcCardStatusVO.setStatusId(dmrc_customer_cardVOS1.getDmrccardStaus().getStatusId());
                    dmrc_customer_cardVO.setIssueDate(dmrc_customer_cardVOS1.getIssueDate());
                    dmrc_customer_cardVO.setDmrccardStaus(dmrcCardStatusVO);
                    dmrc_customer_cardVO.setImage(dmrc_customer_cardVOS1.getImage());
                    dmrc_customer_cardVOS.add(dmrc_customer_cardVO);
                }
                return backgroundAsyncServiceGetListInterface.doInBackGround.doInBackGround(dmrc_customer_cardVOS);
            }
            @Override
            public void doPostExecute(List list) {
              /*  DMRC_List_Adpater dmrc_list_adpater=new DMRC_List_Adpater(Dmrc_Card_Request.this,list ,R.layout.design_dmrc_card_list);
                recyclerView.setAdapter(dmrc_list_adpater);
                addcardlistlayout.addView(recyclerView);

*/

                CustomPagerAdapter models =new CustomPagerAdapter(list,Dmrc_Card_Request.this);

                viewPager=Utility.getViewPager(Dmrc_Card_Request.this);
                viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
                viewPager.setAdapter(models);
                viewPager.setPadding(0,0,0,0);
                tabLayout.setupWithViewPager(viewPager, false);
                Utility.disable_Tab(tabLayout);
                addcardlistlayout.addView(viewPager);

                // add animation on viewpager
               /* DepthTransformation depthTransformation = new DepthTransformation();
                viewPager.setPageTransformer(true, depthTransformation);*/

                View current = getCurrentFocus();
                if (current != null) current.clearFocus();
            }
        });
        backgroundAsyncServiceGetList.execute();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button:
                finish();
                break;

            case R.id. attachaddress:
                permissionUtils.check_permission(PermissionHandler.imagePermissionArrayList(Dmrc_Card_Request.this), Content_Message.CAMERA_PERMISSION,ApplicationConstant.REQ_CAMERA_PERMISSION);


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
                            saveDmrcCardInServer();
                        }else {
                            BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                                @Override
                                public void doInBackGround() {
                                    stringimg= Utility.BitMapToString(bmp);
                                }
                                @Override
                                public void doPostExecute() {
                                    pd.dismiss();
                                    saveDmrcCardInServer();
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



    public void startCamera(){
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
    @SuppressLint("ResourceType")
    public void showAddCardBtn(){

        LinearLayout linearLayout =findViewById(R.id.layoutmainBanner);
        TextView textView = Utility.getTextView(Dmrc_Card_Request.this,"Add on Card");
        textView.setPaintFlags(textView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_blue));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
        textView.setTypeface(typeface ,Typeface.BOLD);
        linearLayout.addView(textView);

        scrollView.setAnimation(null);
        scrollView.setVisibility(View.GONE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dmrc_customer_cardVO=new DMRC_Customer_CardVO();

                mobilenumber.setText(null);
                customername.setText(null);
                pin.setText(null);
                city.setText(null);
                state.setText(null);
                permanentaddress.setText(null);
                addressimage.setImageBitmap(null);
                bmp=null;
                scrollviewAnimationAndVisibility();
                Utility.removeEle(textView);
            }
        });
    }

    private void scrollviewAnimationAndVisibility(){
        scrollView.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,
                0,
                1000,
                0);
        animate.setDuration(1000);
        animate.setFillAfter(true);
        scrollView.startAnimation(animate);

    }

    public void saveDmrcCardInServer(){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.saveDmarcCards();
            DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Dmrc_Card_Request.this)));
            request_dmrc_customer_cardVO.setDmrcid(dmrc_customer_cardVO.getDmrcid());
            request_dmrc_customer_cardVO.setCustomer(customerVO);
            request_dmrc_customer_cardVO.setCustomerName(customername.getText().toString());
            request_dmrc_customer_cardVO.setMobileNumber(mobilenumber.getText().toString());
            request_dmrc_customer_cardVO.setAddress(permanentaddress.getText().toString());
            request_dmrc_customer_cardVO.setPincode(pin.getText().toString());
            if(bmp!=null){
                request_dmrc_customer_cardVO.setImage(stringimg);
            }
            Gson gson =new Gson();
            String json = gson.toJson(request_dmrc_customer_cardVO);
            params.put("volley", json);
            Log.w("saveDmrcCardInServer",json);
            connectionVO.setParams(params);
            VolleyUtils.makeJsonObjectRequest(Dmrc_Card_Request.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    dmrc_customer_cardVO = gson.fromJson(response.toString(), DMRC_Customer_CardVO.class);

                    if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                        //VolleyUtils.furnishErrorMsg(  "Fail" ,response, MainActivity.this);
                        ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !",sb.toString(),false);
                    }else {
                       /*Intent intent =new Intent(Dmrc_Card_Request.this,DMRC_Cards_List.class);
                        intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                        startActivity(intent);
                        finish();*/
                        if(!dmrc_customer_cardVO.getStatusCode().equals("200") && !dmrc_customer_cardVO.getStatusCode().equals("ap104")){
                            if(dmrc_customer_cardVO.getStatusCode().equals("ap105") || dmrc_customer_cardVO.getStatusCode().equals("ap107") ||dmrc_customer_cardVO.getStatusCode().equals("ap102")){

                               // 12/04/2020
                                MyDialog.showWebviewAlertDialog(Dmrc_Card_Request.this, dmrc_customer_cardVO.getHtmlString(),false,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(dialog)->{
                                    dialog.dismiss();
                                    startActivityForResult(new Intent(Dmrc_Card_Request.this,Enach_Mandate.class).putExtra("forresutl",true).putExtra("selectservice",new ArrayList<Integer>( Arrays.asList(serviceId))),ApplicationConstant.REQ_ENACH_MANDATE);
                                },(ConfirmationDialogInterface.OnCancel)(cancel)->{
                                    cancel.dismiss();
                                }));

                            }else if(dmrc_customer_cardVO.getStatusCode().equals("ap106") || dmrc_customer_cardVO.getStatusCode().equals("ap103") || dmrc_customer_cardVO.getStatusCode().equals("ap108")) {

                                // 12/04/2020
                                MyDialog.showWebviewAlertDialog(Dmrc_Card_Request.this, dmrc_customer_cardVO.getHtmlString(), false, new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk) (dialog) -> {
                                    dialog.dismiss();
                                    String[] buttons = {"New Bank","Existing Bank"};
                                    Utility.showDoubleButtonDialogConfirmation(new com.uav.autodebit.util.DialogInterface() {
                                        @Override
                                        public void confirm(Dialog dialog) {
                                            dialog.dismiss();
                                            try {
                                                JSONArray arryjson = new JSONArray(dmrc_customer_cardVO.getAnonymousString());
                                                ArrayList<CustomerAuthServiceVO> customerAuthServiceArry = new ArrayList<>();
                                                for (int i = 0; i < arryjson.length(); i++) {
                                                    JSONObject jsonObject = arryjson.getJSONObject(i);
                                                    CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                                                    customerAuthServiceVO.setBankName(jsonObject.getString("bankName"));
                                                    customerAuthServiceVO.setProviderTokenId(jsonObject.getString("mandateId"));
                                                    customerAuthServiceVO.setCustomerAuthId(jsonObject.getInt("id"));
                                                    customerAuthServiceVO.setAnonymousString(jsonObject.getString("status"));
                                                    customerAuthServiceArry.add(customerAuthServiceVO);
                                                }
                                                CustomerAuthServiceVO customerAuthServiceVO = new CustomerAuthServiceVO();
                                                customerAuthServiceVO.setBankName(null);
                                                customerAuthServiceVO.setProviderTokenId("Add New Mandate");
                                                customerAuthServiceVO.setCustomerAuthId(0);
                                                customerAuthServiceVO.setAnonymousString(null);
                                                customerAuthServiceArry.add(customerAuthServiceVO);

                                                Utility.alertselectdialog(Dmrc_Card_Request.this, "Choose from existing Bank", customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (s) -> {
                                                    if (!s.equals("0")) {
                                                        Log.w("Home_value", s);
                                                        sIMandateDmrc(Integer.parseInt(s));
                                                    } else {
                                                        startActivityForResult(new Intent(Dmrc_Card_Request.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(serviceId))), ApplicationConstant.REQ_ENACH_MANDATE);
                                                    }
                                                }));
                                            } catch (Exception e) {
                                                ExceptionsNotification.ExceptionHandling(Dmrc_Card_Request.this , Utility.getStackTrace(e));
                                               // Utility.exceptionAlertDialog(Dmrc_Card_Request.this, "Alert!", "Something went wrong, Please try again!", "Report", Utility.getStackTrace(e));
                                            }
                                        }
                                        @Override
                                        public void modify(Dialog dialog) {
                                            dialog.dismiss();
                                            startActivityForResult(new Intent(Dmrc_Card_Request.this, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(serviceId))), ApplicationConstant.REQ_ENACH_MANDATE);
                                        }
                                    }, Dmrc_Card_Request.this, dmrc_customer_cardVO.getErrorMsgs().get(0), "", buttons);

                                  }, (ConfirmationDialogInterface.OnCancel) (cancel) -> {
                                    cancel.dismiss();
                                }));
                            }
                        }else {
                            sIMandateDmrc(null);
                        }
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(Dmrc_Card_Request.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(Dmrc_Card_Request.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }




    public void sIMandateDmrc(Integer bankId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SiBO.sIMandateDmrc();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Dmrc_Card_Request.this)));
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(dmrc_customer_cardVO.getDmrcid());
        request_dmrc_customer_cardVO.setAnonymousInteger(bankId);

        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("sIMandateDmrc",json);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(Dmrc_Card_Request.this,connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {

                try {

                    JSONObject response = (JSONObject) resp;
                    Gson gson = new Gson();
                    DMRC_Customer_CardVO dmrc_customer_SI_cardVO = gson.fromJson(response.toString(), DMRC_Customer_CardVO.class);

                    if(dmrc_customer_SI_cardVO.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) dmrc_customer_SI_cardVO.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !",sb.toString(),false);
                    }else {
                        if(dmrc_customer_SI_cardVO.getCustomer()!=null){
                            String json = new Gson().toJson(dmrc_customer_SI_cardVO.getCustomer());
                            Session.set_Data_Sharedprefence(Dmrc_Card_Request.this,Session.CACHE_CUSTOMER,json);
                            Session.set_Data_Sharedprefence(Dmrc_Card_Request.this, Session.LOCAL_CACHE,dmrc_customer_SI_cardVO.getCustomer().getLocalCache());
                        }

                        if(dmrc_customer_SI_cardVO.getShowDialog()){
                            JSONObject object = new JSONObject(dmrc_customer_SI_cardVO.getAnonymousString());
                            String [] btnText= {object.getString("Button1"),object.getString("Button2")};

                            MyDialog.showDoubleButtonBigContentDialog(Dmrc_Card_Request.this,new BigContentDialogIntetface((BigContentDialogIntetface.Button1)(button1)->{
                                button1.dismiss();
                                startSIActivity(Dmrc_Card_Request.this,dmrc_customer_SI_cardVO.getDmrcid(),1.00,ApplicationConstant.Dmrc,ApplicationConstant.PG_MANDATE);

                                },(BigContentDialogIntetface.Button2)(button2)->{
                                button2.dismiss();
                                dmrcCustomerCardSecurityDeposti(dmrc_customer_SI_cardVO.getDmrcid());

                            }),dmrc_customer_SI_cardVO.getDialogTitle(),dmrc_customer_SI_cardVO.getHtmlString(),btnText);

                        }else{
                            allotDmrcCard(dmrc_customer_SI_cardVO.getDmrcid());
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ExceptionsNotification.ExceptionHandling(Dmrc_Card_Request.this , Utility.getStackTrace(e));
                }
            }
        });
    }

    public static void startSIActivity(Context context , int id,double amount,int serviceId, String paymentType){
        Intent intent = new Intent(context,SI_First_Data.class);
        intent.putExtra("id",id);
        intent.putExtra("amount",amount);
        intent.putExtra("serviceId",serviceId+"");
        intent.putExtra("paymentType",paymentType);
        ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_MANDATE);
    }


    private void dmrcCustomerCardSecurityDeposti(Integer dmrcid) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MetroBO.dmrcCustomerCardSecurityDeposti();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Dmrc_Card_Request.this)));
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(dmrcid);

        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("CardSecurityDeposti",json);
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

                if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !",sb.toString(),false);
                }else {
                    allotDmrcCard(dmrcid);
                }
            }
        });
    }

    public void allotDmrcCard(Integer cardId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MetroBO.allotDmrcCard();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Dmrc_Card_Request.this)));
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(cardId);


        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("request",json);
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

                if(dmrc_customer_cardVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) dmrc_customer_cardVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !",sb.toString(),false);
                }else {
                    //update customer cache
                    if(dmrc_customer_cardVO.getCustomer()!=null){
                        String json = new Gson().toJson(dmrc_customer_cardVO.getCustomer());
                        Session.set_Data_Sharedprefence(Dmrc_Card_Request.this,Session.CACHE_CUSTOMER,json);
                        Session.set_Data_Sharedprefence(Dmrc_Card_Request.this, Session.LOCAL_CACHE,dmrc_customer_cardVO.getCustomer().getLocalCache());
                    }

                    addRequestDmrcCardBanner(dmrc_customer_cardVO);
                }
            }
        });
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
        photofileurl=null;
        try
        {
            // place where to store camera taken picture
            photofileurl = Utility.createTemporaryFile("picture", ".jpg");
            photofileurl.delete();
            Uri mImageUri = FileProvider.getUriForFile(this, this.getApplicationContext().getPackageName() + ".provider", photofileurl);

            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
            startActivityForResult(intent, REQ_IMAGE);
        }
        catch(Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }
    public void setCustomerDetail(DMRC_Customer_CardVO dmrc_customer_cardVO){
        if(dmrc_customer_cardVO.getDmrcid()==null){
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
        }else {
            scrollviewAnimationAndVisibility();
            if(dmrc_customer_cardVO.getCustomerName()!=null){
                customername.setText(dmrc_customer_cardVO.getCustomerName());
            }
            if(dmrc_customer_cardVO.getMobileNumber()!=null){
                mobilenumber.setText(dmrc_customer_cardVO.getMobileNumber());
            }
            if(dmrc_customer_cardVO.getPincode()!=null){
                pin.setText(dmrc_customer_cardVO.getPincode());
            }
            if(dmrc_customer_cardVO.getCity()!=null && dmrc_customer_cardVO.getCity().getCityName()!=null){
                city.setText(dmrc_customer_cardVO.getCity().getCityName());
            }
            if(dmrc_customer_cardVO.getStateRegion()!=null && dmrc_customer_cardVO.getStateRegion().getStateRegionName()!=null){
                state.setText(dmrc_customer_cardVO.getStateRegion().getStateRegionName());
            }
            if(dmrc_customer_cardVO.getAddress()!=null){
                permanentaddress.setText(dmrc_customer_cardVO.getAddress());
            }
        }
        if(isdisable) enabledAllEle(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            try {
                if (requestCode == REQ_IMAGE) {
                    bmp =Utility.decodeImageFromFiles(Uri.fromFile(photofileurl).getPath(),150,150);
                    if(bmp.getWidth()>bmp.getHeight()){
                        Matrix matrix =new Matrix();
                        matrix.postRotate(90);
                        bmp= Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),matrix,true);
                    }
                    int imagesizeinbyte=Utility.byteSizeOf(bmp);
                    Log.w("image",imagesizeinbyte +"  ====   "+(imagesizeinbyte/1024) +"");
                    imagesizeinbyte=Utility.byteSizeOf(bmp);
                    Log.w("image",imagesizeinbyte +"  ====   "+(imagesizeinbyte/1024) +"");

                    addressimage.setImageBitmap(bmp);
                    performCrop(Utility.getVersionWiseUri(this,photofileurl));
                    View current = getCurrentFocus();
                    if (current != null) current.clearFocus();
                }else  if(requestCode==REQ_GALLERY){
                    Uri contentURI = data.getData();
                    bmp =Utility.grabImage(contentURI,Dmrc_Card_Request.this);
                    addressimage.setImageBitmap(bmp);
                    performCrop(contentURI);
                }else  if(requestCode==PIC_CROP){
                    //get the returned data
                    Bundle extras = data.getExtras();
                    //get the cropped bitmap
                    bmp = (Bitmap) extras.get("data");
                    //display the returned cropped image
                    addressimage.setImageBitmap(bmp);
                }else if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                    boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
                    if(enachMandateStatus){
                        sIMandateDmrc(null);
                    }else{
                        Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Alert",data.getStringExtra("msg"),false);
                    }
                }else if(requestCode==ApplicationConstant.REQ_SI_MANDATE){
                    int actionId=data.getIntExtra("actionId",0);
                    if(actionId!=0){
                        allotDmrcCard(actionId);
                    }else {
                        Utility.showSingleButtonDialog(Dmrc_Card_Request.this,"Error !", "Something went wrong",false);
                    }
                }
            } catch (Exception e) {
                ExceptionsNotification.ExceptionHandling(Dmrc_Card_Request.this , Utility.getStackTrace(e));
           }
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
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
            // Utility.exceptionAlertDialog(this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
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

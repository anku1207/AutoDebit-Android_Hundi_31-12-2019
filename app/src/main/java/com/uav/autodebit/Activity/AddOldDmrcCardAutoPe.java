package com.uav.autodebit.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.uav.autodebit.BO.SiBO;
import com.uav.autodebit.CustomDialog.BeforeRecharge;
import com.uav.autodebit.CustomDialog.MyDialog;
import com.uav.autodebit.ImageRead.ImageTextApi;
import com.uav.autodebit.ImageRead.ImageTextViewInterface;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.BigContentDialogIntetface;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.MandateAndRechargeInterface;
import com.uav.autodebit.MessageFormater.CustomMessageFormat;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.CustomPagerAdapter;
import com.uav.autodebit.adpater.DmrcCardPagerAdapter;
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
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AddOldDmrcCardAutoPe extends AppCompatActivity implements View.OnClickListener ,PermissionUtils.PermissionResultCallback , ActivityCompat.OnRequestPermissionsResultCallback{

    Button addCard;

    Uri mImageUri;
    PermissionUtils permissionUtils;
    Bitmap bmp;


    EditText card_Number,confirm_Card_Number,image_Read_Number;
    int  REQ_IMAGE=1001,REQ_GALLERY=1002,PIC_CROP=1004;
    ImageView addDmrcImage,back_activity_button1 ;
    String customerId;
    Gson gson = new Gson();
    boolean checkCardImageUpload;
    UAVProgressDialog pd;
    String stringimg=null;

    LinearLayout addcardlistlayout,bottom_layout;
    DMRC_Customer_CardVO dmrc_customer_cardVO;
    RecyclerView recyclerView;
    ViewPager viewPager;
    ScrollView scrollView;
    int serviceId=ApplicationConstant.Dmrc;
    TabLayout tabLayout;
    File photofileurl;

    TextView add_Card_Link;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_old_dmrc_card_auto_pe);
        getSupportActionBar().hide();


        pd=new UAVProgressDialog(this);
        permissionUtils=new PermissionUtils(this);

        addDmrcImage=findViewById(R.id.addDmrcImage);
        addCard=findViewById(R.id.addCard);
        addcardlistlayout=findViewById(R.id.addcardlistlayout);
        scrollView=findViewById(R.id.scrollView);
        tabLayout =findViewById(R.id.indicator);
        back_activity_button1=findViewById(R.id.back_activity_button);
        bottom_layout=findViewById(R.id.bottom_layout);
        add_Card_Link=findViewById(R.id.add_Card_Link);

        card_Number=findViewById(R.id.card_Number);
        confirm_Card_Number=findViewById(R.id.confirm_Card_Number);
        image_Read_Number=findViewById(R.id.image_Read_Number);

        addcardlistlayout.removeAllViews();

        customerId = Session.getCustomerId(this);
        dmrc_customer_cardVO = gson.fromJson(getIntent().getStringExtra("dmrccard"), DMRC_Customer_CardVO.class);

        setDetail(dmrc_customer_cardVO);

        addRequestDmrcCardBanner(dmrc_customer_cardVO);

        add_Card_Link.setPaintFlags(add_Card_Link.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);


        back_activity_button1.setOnClickListener(this);
        add_Card_Link.setOnClickListener(this);
        addCard.setOnClickListener(this);

        confirm_Card_Number.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus){
                    if(confirm_Card_Number.getText().toString().equals("")){
                        confirm_Card_Number.setError(ErrorMsg.Filed_Required);
                    }else{
                        confirm_Card_Number.setError(null);
                        if(!card_Number.getText().toString().trim().equals(confirm_Card_Number.getText().toString().trim())){
                            confirm_Card_Number.setError("confirm card number is mismatch");
                        }
                    }
                }
            }
        });
    }

    private void setDetail(DMRC_Customer_CardVO dmrc_customer_cardVO) {
        if(dmrc_customer_cardVO.getDmrcid()!=null){
            card_Number.setText(dmrc_customer_cardVO.getCardNo());
            confirm_Card_Number.setText(dmrc_customer_cardVO.getCardNo());
            Picasso.with(this).load(dmrc_customer_cardVO.getImage()).fit()
                    .into(addDmrcImage);
            bottom_layout.setVisibility(View.VISIBLE);
            image_Read_Number.setText(dmrc_customer_cardVO.getCardNo());
            checkCardImageUpload=true;
        }

    }


    public void addRequestDmrcCardBanner(DMRC_Customer_CardVO dmrc_customer_cardVO){
        addcardlistlayout.removeAllViewsInLayout();

        if(dmrc_customer_cardVO.getDmrcCustomerList()!=null && dmrc_customer_cardVO.getDmrcCustomerList().size()>0){
            if(dmrc_customer_cardVO.getDmrcid()==null){
                // restart layout
                bottom_layout.setVisibility(View.GONE);
                image_Read_Number.setText("");
                card_Number.setText("");
                confirm_Card_Number.setText("");
                addDmrcImage.setImageDrawable(Utility.getDrawableResources(AddOldDmrcCardAutoPe.this,R.drawable.old_dmrc_image));
                card_Number.setEnabled(true);
                confirm_Card_Number.setEnabled(true);
            }

            ArrayList<DMRC_Customer_CardVO> listforcard= (ArrayList<DMRC_Customer_CardVO>) dmrc_customer_cardVO.getDmrcCustomerList();
            getdata(listforcard);
        }else{
            CardView cardView =Utility.getCardViewStyle(this);
            ImageView imageView =Utility.getImageView(this);
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

                DmrcCardPagerAdapter models =new DmrcCardPagerAdapter(AddOldDmrcCardAutoPe.this,list);

                viewPager=Utility.getViewPager(AddOldDmrcCardAutoPe.this);
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
    public void onClick(View v) {
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.imageviewclickeffect);
        switch (v.getId()){
            case R.id.add_Card_Link:
                add_Card_Link.startAnimation(animation);
                confirm_Card_Number.clearFocus();
                EditText [] editTexts = {card_Number,confirm_Card_Number};
                if(Utility.setErrorOnEdittext(editTexts)){
                    permissionUtils.check_permission(PermissionHandler.imagePermissionArrayList(this), Content_Message.CAMERA_PERMISSION, ApplicationConstant.REQ_CAMERA_PERMISSION);
                }
                break;
            case R.id.addCard:
                addCard.startAnimation(animation);
                saveExistDmrcCard();
                break;
        }
    }

    private void saveExistDmrcCard() {
        if(checkEdittextValidation()){
            BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                @Override
                public void doInBackGround() {
                    try {
                        stringimg=  Utility.BitMapToString(bmp);
                    }catch (Exception e){
                        ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
                    }

                }
                @Override
                public void doPostExecute() {
                    pd.dismiss();
                    if(stringimg!=null){
                        saveDmrcCardInServer();
                    }

                }
            });
            backgroundAsyncService.execute();
        }
    }


    public void saveDmrcCardInServer(){
        try {
            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = MetroBO.saveDmarcExitingCards();
            DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
            CustomerVO customerVO=new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(AddOldDmrcCardAutoPe.this)));
            request_dmrc_customer_cardVO.setCustomer(customerVO);
            request_dmrc_customer_cardVO.setCardNo(image_Read_Number.getText().toString().trim());
            request_dmrc_customer_cardVO.setImage(stringimg);

            // exist dmrc card id
            request_dmrc_customer_cardVO.setDmrcid(dmrc_customer_cardVO.getDmrcid());

            Gson gson =new Gson();
            String json = gson.toJson(request_dmrc_customer_cardVO);
            params.put("volley", json);
            Log.w("saveDmrcCardInServer",json);
            connectionVO.setParams(params);
            VolleyUtils.makeJsonObjectRequest(AddOldDmrcCardAutoPe.this,connectionVO, new VolleyResponseListener() {
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
                        Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Error !",sb.toString(),false);
                    }else {
                       /*Intent intent =new Intent(Dmrc_Card_Request.this,DMRC_Cards_List.class);
                        intent.putExtra("dmrccard",gson.toJson(dmrc_customer_cardVO));
                        startActivity(intent);
                        finish();*/
                        if(dmrc_customer_cardVO.isEventIs()){
                            Utility.showSelectPaymentTypeDialog(AddOldDmrcCardAutoPe.this, "Payment Type", dmrc_customer_cardVO.getPaymentTypeObject(), new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) (position) -> {
                                int selectPosition = Integer.parseInt(position);
                                if (selectPosition == ApplicationConstant.BankMandatePayment){
                                    // 07/05/2020
                                    BillPayRequest.showBankMandateOrSiMandateInfo(AddOldDmrcCardAutoPe.this,dmrc_customer_cardVO.getBankMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                        dmrc_customer_cardVO.setAnonymousInteger(AuthServiceProviderVO.ENACHIDFC);
                                        setBankMandateOrRecharge(AddOldDmrcCardAutoPe.this,dmrc_customer_cardVO);
                                    }));


                                } else if(selectPosition == ApplicationConstant.SIMandatePayment) {
                                    // recharge on SI mandate

                                    BillPayRequest.showBankMandateOrSiMandateInfo(AddOldDmrcCardAutoPe.this,dmrc_customer_cardVO.getSiMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                        dmrc_customer_cardVO.setAnonymousInteger(AuthServiceProviderVO.AUTOPE_PG);
                                        setBankMandateOrRecharge(AddOldDmrcCardAutoPe.this,dmrc_customer_cardVO);
                                    }));
                                    // proceedToRecharge(oxigenValidateResponce.getTypeId().toString(),"AUTOPETXNID60", AuthServiceProviderVO.PAYU);
                                }else if(selectPosition == ApplicationConstant.UPIMandatePayment) {
                                    // recharge on SI mandate
                                    BillPayRequest.showBankMandateOrSiMandateInfo(AddOldDmrcCardAutoPe.this,dmrc_customer_cardVO.getUpiMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                        dmrc_customer_cardVO.setAnonymousInteger(AuthServiceProviderVO.AUTOPE_PG_UPI);
                                        setBankMandateOrRecharge(AddOldDmrcCardAutoPe.this,dmrc_customer_cardVO);
                                    }));
                                    // proceedToRecharge(oxigenValidateResponce.getTypeId().toString(),"AUTOPETXNID60", AuthServiceProviderVO.PAYU);
                                }
                            }));

                        }else {
                            sIMandateDmrc(null,null);
                        }
                    }
                }
            });
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
            //Utility.exceptionAlertDialog(Dmrc_Card_Request.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    public  void setBankMandateOrRecharge(Context context , DMRC_Customer_CardVO dmrc_customer_cardVO){
        OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();
        oxigenTransactionVO.setServiceId(dmrc_customer_cardVO.getServiceId());

        AuthServiceProviderVO authServiceProviderVO = new AuthServiceProviderVO();
        authServiceProviderVO.setProviderId(dmrc_customer_cardVO.getAnonymousInteger());
        oxigenTransactionVO.setProvider(authServiceProviderVO);

        BeforeRecharge.beforeRechargeAddMandate(context,oxigenTransactionVO,new MandateAndRechargeInterface((MandateAndRechargeInterface.OnRecharge)(recharge)->{
            sIMandateDmrc(Integer.parseInt((String) recharge),dmrc_customer_cardVO.getAnonymousInteger());
        }, (MandateAndRechargeInterface.OnMandate)(mandate)->{
            if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                startSIActivity(context,dmrc_customer_cardVO,ApplicationConstant.PG_MANDATE);
            }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                startBankMandateActivity(context,dmrc_customer_cardVO);
            }else if(oxigenTransactionVO.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG_UPI){
                startUPIActivity(context,dmrc_customer_cardVO,ApplicationConstant.PG_MANDATE);
            }
        }));
    }

    public  void startBankMandateActivity(Context context , DMRC_Customer_CardVO  dmrc_customer_cardVO){
        try {
            Intent intent = new Intent(context,Enach_Mandate.class);
            intent.putExtra("forresutl",true);
            intent.putExtra("selectservice",new ArrayList<Integer>(Arrays.asList(dmrc_customer_cardVO.getServiceId())));
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_ENACH_MANDATE);
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
        }

    }


    public  void startSIActivity(Context context ,  DMRC_Customer_CardVO  dmrc_customer_cardVO , String paymentType){
        try {
            Intent intent = new Intent(context,SI_First_Data.class);
            intent.putExtra("id",dmrc_customer_cardVO.getDmrcid());
            intent.putExtra("amount",dmrc_customer_cardVO.getAnonymousAmount());
            intent.putExtra("serviceId",dmrc_customer_cardVO.getServiceId()+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_SI_MANDATE);
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
        }

    }


    public  void startUPIActivity(Context context ,  DMRC_Customer_CardVO  dmrc_customer_cardVO , String paymentType){
        try {
            Intent intent = new Intent(context,UPI_Mandate.class);
            intent.putExtra("id",dmrc_customer_cardVO.getDmrcid());
            intent.putExtra("amount",dmrc_customer_cardVO.getAnonymousAmount());
            intent.putExtra("serviceId",dmrc_customer_cardVO.getServiceId()+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_UPI_FOR_MANDATE);
        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
        }
    }




    public void sIMandateDmrc(Integer bankId , Integer providerId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SiBO.sIMandateDmrc();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(AddOldDmrcCardAutoPe.this)));
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(dmrc_customer_cardVO.getDmrcid());

        //set provider id  anonymousInteger
        request_dmrc_customer_cardVO.setAnonymousInteger1(providerId);

        //set customer auth bank id select by BANK list mandate
        request_dmrc_customer_cardVO.setAnonymousInteger(bankId);

        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("sIMandateDmrc",json);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(AddOldDmrcCardAutoPe.this,connectionVO, new VolleyResponseListener() {
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
                        Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Error !",sb.toString(),false);
                    }else {
                        if(dmrc_customer_SI_cardVO.getCustomer()!=null){
                            String json = new Gson().toJson(dmrc_customer_SI_cardVO.getCustomer());
                            Session.set_Data_Sharedprefence(AddOldDmrcCardAutoPe.this,Session.CACHE_CUSTOMER,json);
                            Session.set_Data_Sharedprefence(AddOldDmrcCardAutoPe.this, Session.LOCAL_CACHE,dmrc_customer_SI_cardVO.getCustomer().getLocalCache());
                        }

                        if(dmrc_customer_SI_cardVO.getShowDialog()){
                            JSONObject object = new JSONObject(dmrc_customer_SI_cardVO.getAnonymousString());
                            String [] btnText= {object.getString("Button1"),object.getString("Button2")};

                            MyDialog.showDoubleButtonBigContentDialog(AddOldDmrcCardAutoPe.this,new BigContentDialogIntetface((BigContentDialogIntetface.Button1)(button1)->{
                                button1.dismiss();

                                BillPayRequest.showBankMandateOrSiMandateInfo(AddOldDmrcCardAutoPe.this,dmrc_customer_SI_cardVO.getSiMandateHtml(),new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                    OxigenTransactionVO oxigenTransactionVO = new OxigenTransactionVO();
                                    oxigenTransactionVO.setServiceId(dmrc_customer_SI_cardVO.getServiceId());

                                    AuthServiceProviderVO authServiceProviderVO =new AuthServiceProviderVO();
                                    authServiceProviderVO.setProviderId(AuthServiceProviderVO.AUTOPE_PG);
                                    oxigenTransactionVO.setProvider(authServiceProviderVO);

                                    BeforeRecharge.beforeRechargeAddMandate(AddOldDmrcCardAutoPe.this,oxigenTransactionVO,new MandateAndRechargeInterface((MandateAndRechargeInterface.OnRecharge)(recharge)->{
                                        try {
                                            allotDmrcCard(dmrc_customer_SI_cardVO.getDmrcid(),Integer.parseInt((String) recharge));
                                        }catch (Exception e){
                                            ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
                                        }
                                    }, (MandateAndRechargeInterface.OnMandate)(mandate)->{
                                        startSIActivity(AddOldDmrcCardAutoPe.this,dmrc_customer_SI_cardVO.getDmrcid(),dmrc_customer_SI_cardVO.getAnonymousAmount(),dmrc_customer_SI_cardVO.getServiceId(),ApplicationConstant.PG_MANDATE);
                                    }));
                                }));
                            },(BigContentDialogIntetface.Button2)(button2)->{
                                button2.dismiss();
                                String [] proceedBtn= {"Proceed"};
                                MyDialog.showSingleButtonBigContentDialog(AddOldDmrcCardAutoPe.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                                    ok.dismiss();
                                    dmrcCustomerCardSecurityDeposti(dmrc_customer_SI_cardVO.getDmrcid());
                                }),"Add Security Deposit",dmrc_customer_SI_cardVO.getDialogMessage(),proceedBtn);
                            }),dmrc_customer_SI_cardVO.getDialogTitle(),dmrc_customer_SI_cardVO.getHtmlString(),btnText);
                        }else{
                            allotDmrcCard(dmrc_customer_SI_cardVO.getDmrcid(),null);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
                }
            }
        });
    }

    public  void startSIActivity(Context context , int id,double amount,int serviceId, String paymentType){
        try {
            Intent intent = new Intent(context,SI_First_Data.class);
            intent.putExtra("id",id);
            intent.putExtra("amount",amount);
            intent.putExtra("serviceId",serviceId+"");
            intent.putExtra("paymentType",paymentType);
            ((Activity) context).startActivityForResult(intent,ApplicationConstant.REQ_DMRC_MANDATE_SI_BUCKET);

        }catch (Exception e){
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(AddOldDmrcCardAutoPe.this , Utility.getStackTrace(e));
        }

    }

    private void dmrcCustomerCardSecurityDeposti(Integer dmrcid) {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MetroBO.dmrcCustomerCardSecurityDeposti();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(AddOldDmrcCardAutoPe.this)));
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(dmrcid);

        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("CardSecurityDeposti",json);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(AddOldDmrcCardAutoPe.this,connectionVO, new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Error !",sb.toString(),false);
                }else {
                    allotDmrcCard(dmrcid,null);
                }
            }
        });
    }

    public void allotDmrcCard(Integer cardId , Integer sIMandateId){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MetroBO.allotDmrcCard();
        DMRC_Customer_CardVO request_dmrc_customer_cardVO=new DMRC_Customer_CardVO();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(AddOldDmrcCardAutoPe.this)));
        request_dmrc_customer_cardVO.setCustomer(customerVO);
        request_dmrc_customer_cardVO.setDmrcid(cardId);

        //set customer auth si id select by SI Mandate list mandate
        request_dmrc_customer_cardVO.setAnonymousInteger(sIMandateId);


        Gson gson =new Gson();
        String json = gson.toJson(request_dmrc_customer_cardVO);
        Log.w("request",json);
        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(AddOldDmrcCardAutoPe.this,connectionVO, new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Error !",sb.toString(),false);
                }else {
                    //update customer cache
                    if(dmrc_customer_cardVO.getCustomer()!=null){
                        String json = new Gson().toJson(dmrc_customer_cardVO.getCustomer());
                        Session.set_Data_Sharedprefence(AddOldDmrcCardAutoPe.this,Session.CACHE_CUSTOMER,json);
                        Session.set_Data_Sharedprefence(AddOldDmrcCardAutoPe.this, Session.LOCAL_CACHE,dmrc_customer_cardVO.getCustomer().getLocalCache());
                    }

                    addRequestDmrcCardBanner(dmrc_customer_cardVO);
                }
            }
        });
    }



    private boolean checkEdittextValidation(){
        boolean valid=true;
        if(card_Number.getText().toString().trim().equals("")){
            card_Number.setError(ErrorMsg.Filed_Required);
            valid=false;
        }
        if(confirm_Card_Number.getText().toString().trim().equals("")){
            confirm_Card_Number.setError(ErrorMsg.Filed_Required);
            valid=false;
        }

        if(image_Read_Number.getText().toString().trim().equals("")){
            image_Read_Number.setError(ErrorMsg.Filed_Required);
            valid=false;
        }

        if(!checkCardImageUpload){
            Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"","Card Image Id null",false);
            valid=false;
        }
        return valid;
    }



    private void openCamera(Context context) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        photofileurl=null;
        try
        {
            // place where to store camera taken picture
            photofileurl = Utility.createTemporaryFile("picture", ".jpg",AddOldDmrcCardAutoPe.this);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (resultCode == RESULT_OK) {


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
                        addDmrcImage.setImageBitmap(bmp);
                        performCrop(Utility.getVersionWiseUri(this,photofileurl));
                        View current = getCurrentFocus();
                        if (current != null) current.clearFocus();

                    }else  if(requestCode==PIC_CROP){
                        //get the returned data
                        Bundle extras = data.getExtras();
                        //get the cropped bitmap
                        bmp = (Bitmap) extras.get("data");
                        //display the returned cropped image
                        getTextByImage();
                    }else if(requestCode==ApplicationConstant.REQ_DMRC_MANDATE_SI_BUCKET){
                        int actionId=data.getIntExtra("actionId",0);
                        if(actionId!=0){
                            allotDmrcCard(actionId,null);
                        }else {
                            Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Error !", "Something went wrong",false);
                        }
                    }else if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                        boolean enachMandateStatus=data.getBooleanExtra("mandate_status",false);
                        if(enachMandateStatus){
                            sIMandateDmrc(null,null);
                        }else{
                            Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Alert",data.getStringExtra("msg"),false);
                        }
                    }else if(requestCode == ApplicationConstant.REQ_UPI_FOR_MANDATE || requestCode == ApplicationConstant.REQ_SI_MANDATE){
                        int SIMandateId=data.getIntExtra("mandateId",0);
                        if(SIMandateId!=0){
                            sIMandateDmrc(null,null);
                        }else{
                            Utility.showSingleButtonDialog(AddOldDmrcCardAutoPe.this,"Alert", Content_Message.error_message,false);
                        }

                    }


            }else {
                if(requestCode==PIC_CROP){
                    getTextByImage();
                }
            }
        } catch (Exception e) {
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    private void getTextByImage(){
        ImageTextApi imageTextApi = new ImageTextApi(new ImageTextViewInterface() {
            @Override
            public void onResult(String o) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(o!=null ){
                            if(o.equals(card_Number.getText().toString().trim()) && o.equals(confirm_Card_Number.getText().toString().trim())){
                                card_Number.setEnabled(false);
                                confirm_Card_Number.setEnabled(false);
                                bottom_layout.setVisibility(View.VISIBLE);
                                image_Read_Number.setText(o);

                                checkCardImageUpload=true;
                            }else {
                                Toast.makeText(AddOldDmrcCardAutoPe.this, "Card number " + o +" is mismatch", Toast.LENGTH_LONG).show();
                                bottom_layout.setVisibility(View.GONE);
                                image_Read_Number.setText("");

                                checkCardImageUpload=false;
                            }
                        }else {
                            Toast.makeText(AddOldDmrcCardAutoPe.this, "Card Number Con't be read ", Toast.LENGTH_SHORT).show();
                            bottom_layout.setVisibility(View.GONE);
                            image_Read_Number.setText("");
                            card_Number.setEnabled(true);
                            confirm_Card_Number.setEnabled(true);

                            checkCardImageUpload=false;
                        }
                    }
                });
            }
        },AddOldDmrcCardAutoPe.this,bmp);

        imageTextApi.getPincode();
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
            openCamera(AddOldDmrcCardAutoPe.this);
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
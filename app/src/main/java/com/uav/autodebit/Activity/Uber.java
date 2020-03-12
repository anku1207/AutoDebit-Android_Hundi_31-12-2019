package com.uav.autodebit.Activity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.uav.autodebit.BO.ServiceBO;
import com.uav.autodebit.BO.UberBO;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.CustomPagerAdapter;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncServiceGetList;
import com.uav.autodebit.util.BackgroundAsyncServiceGetListInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DMRC_Customer_CardVO;
import com.uav.autodebit.vo.DmrcCardStatusVO;
import com.uav.autodebit.vo.UberVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.uav.autodebit.util.Utility.fromJson;

public class Uber extends Base_Activity implements View.OnClickListener {
    EditText name,lastname,email;
    Button proceed;
    ImageView back_activity_button;

    LinearLayout main,addcardlistlayout;
    EditText [] edittextArray;
    ScrollView scrollView;
    ViewPager viewPager;
    TabLayout tabLayout;
    UAVProgressDialog pd;

    UberVO uberVO;

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uber);
        getSupportActionBar().hide();

        //disableAutoFill
        disableAutofill();

        name=findViewById(R.id.name);
        email=findViewById(R.id.email);
        lastname=findViewById(R.id.lname);
        proceed=findViewById(R.id.proceed);
        back_activity_button=findViewById(R.id.back_activity_button);
        main=findViewById(R.id.main);
        addcardlistlayout=findViewById(R.id.addcardlistlayout);
        scrollView=findViewById(R.id.scrollView);

        scrollView.setVisibility(View.GONE);

        addcardlistlayout.removeAllViews();

        pd=new UAVProgressDialog(this);
        tabLayout =findViewById(R.id.indicator);

        proceed.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        edittextArray= new EditText[]{name, lastname, email};

        getUberDetails(new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            uberVO =(UberVO)s;
            setCustomerDetail(uberVO);
            addRequestDmrcCardBanner(uberVO);
        }));
    }

    private void getUberDetails(VolleyResponse volleyResponse){
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = UberBO.getUberDetails();

        UberVO uberVO =new UberVO();

        CustomerVO  customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Uber.this)));
        uberVO.setCustomer(customerVO);

        String json = new Gson().toJson(uberVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("setBankForService",params.toString());
        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                UberVO uberVO = gson.fromJson(response.toString(), UberVO.class);

                if(uberVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) uberVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Uber.this,"Alert",sb.toString(),false);
                }else {
                    volleyResponse.onSuccess(uberVO);

                }
            }
        });
    }


    public void addRequestDmrcCardBanner(UberVO uberVO){
        addcardlistlayout.removeAllViewsInLayout();

        if(uberVO.getUberCustomerList()!=null && uberVO.getUberCustomerList().size()>0){

            //Show Addcard btn
            if(uberVO.getUberId()==null){
                showAddCardBtn();
            }
            ArrayList<UberVO> listforcard= (ArrayList<UberVO>) uberVO.getUberCustomerList();
            viewPager=Utility.getViewPager(Uber.this);
            viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
            getdata(listforcard);
        }else{

            CardView cardView =Utility.getCardViewStyle(Uber.this);
            ImageView imageView =Utility.getImageView(Uber.this);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);

            Picasso.with(this)
                    .load(uberVO.getImage())
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


    @SuppressLint("ResourceType")
    public void showAddCardBtn(){

        LinearLayout linearLayout =findViewById(R.id.layoutmainBanner);


        TextView textView = Utility.getTextView(Uber.this,"Add On");
        textView.setPaintFlags(textView.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
        textView.setTextColor(getApplication().getResources().getColorStateList(R.drawable.text_change_color_blue));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 17);
        textView.setId(1);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.poppinssemibold);
        textView.setTypeface(typeface ,Typeface.BOLD);
        textView.setTag("sfdsf");

        if(linearLayout.getChildCount()==1){
            linearLayout.addView(textView);
        }
        scrollView.setAnimation(null);
        scrollView.setVisibility(View.GONE);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                uberVO=new UberVO();

                email.setText(null);
                name.setText(null);
                lastname.setText(null);

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
                500,
                0);
        animate.setDuration(1000);
        animate.setFillAfter(true);
        scrollView.startAnimation(animate);

    }


    public void getdata(ArrayList<UberVO> listforcard){
        BackgroundAsyncServiceGetList backgroundAsyncServiceGetList =new BackgroundAsyncServiceGetList(pd, false, new BackgroundAsyncServiceGetListInterface.BackgroundServiceInterface() {
            @Override
            public List doInBackGround(BackgroundAsyncServiceGetListInterface backgroundAsyncServiceGetListInterface) {

                ArrayList<DMRC_Customer_CardVO> dmrc_customer_cardVOS=new ArrayList<>();
                for(UberVO uberVO :listforcard ){
                    DMRC_Customer_CardVO dmrc_customer_cardVO =new DMRC_Customer_CardVO();
                    dmrc_customer_cardVO.setCustomerName(uberVO.getFirstName());
                    dmrc_customer_cardVO.setCardNo(uberVO.getLastName());
                    DmrcCardStatusVO dmrcCardStatusVO =new DmrcCardStatusVO();
                    dmrcCardStatusVO.setStatusName(null);
                    dmrc_customer_cardVO.setIssueDate(null);
                    dmrc_customer_cardVO.setDmrccardStaus(dmrcCardStatusVO);

                    dmrc_customer_cardVOS.add(dmrc_customer_cardVO);
                }
                return backgroundAsyncServiceGetListInterface.doInBackGround.doInBackGround(dmrc_customer_cardVOS);
            }
            @Override
            public void doPostExecute(List list) {
                CustomPagerAdapter models =new CustomPagerAdapter(list,Uber.this);
                viewPager.setAdapter(models);
                viewPager.setPadding(0,0,0,0);
                tabLayout.setupWithViewPager(viewPager, false);
                Utility.disable_Tab(tabLayout);
                addcardlistlayout.addView(viewPager);

                View current = getCurrentFocus();
                if (current != null) current.clearFocus();
            }
        });
        backgroundAsyncServiceGetList.execute();
    }


    public void setCustomerDetail(UberVO uberVO){
        scrollviewAnimationAndVisibility();
        if(uberVO.getFirstName()!=null){
            name.setText(uberVO.getFirstName());
        }
        if(uberVO.getLastName()!=null){
            lastname.setText(uberVO.getLastName());
        }
        if(uberVO.getEmail()!=null){
            email.setText(uberVO.getEmail());
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.proceed:
                if(!Utility.setErrorOnEdittext(edittextArray))return;
                uberSaveDetail();
                break;
            case R.id.back_activity_button :
                finish();
                break;
        }
    }

    private void uberSaveDetail() {

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = UberBO.saveUberCustomerDetails();

        UberVO uberVOrequest =new UberVO();

        CustomerVO customerVO =new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Uber.this)));
        uberVOrequest.setCustomer(customerVO);
        uberVOrequest.setFirstName(name.getText().toString());
        uberVOrequest.setLastName(lastname.getText().toString());
        uberVOrequest.setEmail(email.getText().toString());
        uberVOrequest.setUberId(uberVO.getUberId());


        String json = new Gson().toJson(uberVOrequest);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("uberSaveDetail",params.toString());
        VolleyUtils.makeJsonObjectRequest(this,connectionVO , new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }
            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                Gson gson = new Gson();
                UberVO uberVO = gson.fromJson(response.toString(), UberVO.class);

                if(uberVO.getStatusCode().equals("400")){
                    ArrayList error = (ArrayList) uberVO.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.showSingleButtonDialog(Uber.this,"Alert",sb.toString(),false);
                }else if(uberVO.getStatusCode().equals("E_1")){

                    Intent intent=new Intent(Uber.this,Verify_Otp_By_Id.class);
                    intent.putExtra("id",uberVO.getUberId().toString());
                    intent.putExtra("type","email");
                    intent.putExtra("action",uberVO.getActionname());
                    intent.putExtra("time",uberVO.getAnonymousString());
                    intent.putExtra("otp_display",uberVO.getEmail());
                    startActivityForResult(intent,100);

                } else{
                    //set session customer or local cache
                    addRequestDmrcCardBanner(uberVO);
                    Utility.showSingleButtonDialog(Uber.this,"Alert",uberVO.getAnonymousString(),false);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        getUberDetails(new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
            uberVO =(UberVO)s;
            setCustomerDetail(uberVO);
            addRequestDmrcCardBanner(uberVO);
        }));

        if(resultCode==RESULT_OK){
            if(requestCode==100 ){
                Utility.showSingleButtonDialog(Uber.this,"Alert",data.getStringExtra("msg"),false);
            }
        }

    }

}

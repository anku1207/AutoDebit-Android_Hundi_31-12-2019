package com.uav.autodebit.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Gas_Bill extends Base_Activity implements View.OnClickListener {

    EditText amount,operator;
    ImageView back_activity_button;
    String operatorcode,operatorname=null;
    Button proceed;
    TextView fetchbill;
    CardView amountlayout;

    LinearLayout dynamicCardViewContainer , fetchbilllayout,min_amt_layout;

    List<OxigenQuestionsVO> questionsVOS= new ArrayList<OxigenQuestionsVO>();
    CardView fetchbillcard;

    boolean isFetchBill=true;
    String operatorListDate;
    UAVProgressDialog pd;

    OxigenTransactionVO oxigenTransactionVOresp;
    int minAmt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gas__bill);

        getSupportActionBar().hide();


        operatorListDate=null;
        pd=new UAVProgressDialog(this);

        amount=findViewById(R.id.amount);
        back_activity_button=findViewById(R.id.back_activity_button1);

        amount.setEnabled(false);

        proceed=findViewById(R.id.proceed);
        fetchbill=findViewById(R.id.fetchbill);
        amountlayout=findViewById(R.id.amountlayout);
        operator=findViewById(R.id.operator);
        dynamicCardViewContainer =findViewById(R.id.dynamiccards);
        fetchbilllayout=findViewById(R.id.fetchbilllayout);
        fetchbillcard =findViewById(R.id.fetchbillcard);
        min_amt_layout=findViewById(R.id.min_amt_layout);

        amountlayout.setVisibility(View.GONE);
        oxigenTransactionVOresp=new OxigenTransactionVO();
        minAmt=0;

        back_activity_button.setOnClickListener(this);
        proceed.setOnClickListener(this);
        fetchbill.setOnClickListener(this);

        operator.setClickable(false);

        operator.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    operator.setEnabled(false);
                    //startActivity(new Intent(Mobile_Prepaid_Recharge_Service.this,Listview_With_Image.class));
                    BackgroundAsyncService backgroundAsyncService = new BackgroundAsyncService(pd,true, new BackgroundServiceInterface() {
                        @Override
                        public void doInBackGround() {

                            Gson gson = new Gson();
                            operatorListDate = gson.toJson(getDataList());
                            //manoj
                        }
                        @Override
                        public void doPostExecute() {
                            Intent intent =new Intent(Gas_Bill.this, Listview_With_Image.class);
                            intent.putExtra("datalist", operatorListDate);
                            intent.putExtra("title","Operator");
                            startActivityForResult(intent,100);


                        }
                    });
                    backgroundAsyncService.execute();
                }
                return false;
            }
        });
    }

    public ArrayList<DataAdapterVO> getDataList(){
        ArrayList<DataAdapterVO> datalist = new ArrayList<>();
        String operator= Session.getSessionByKey(Gas_Bill.this,Session.CACHE_GAS_OPERATOR);
        try {
            JSONArray jsonArray =new JSONArray(operator);

            Log.w("dataoperator",jsonArray.toString());
            for(int i=0;i<jsonArray.length();i++){
                DataAdapterVO dataAdapterVO = new DataAdapterVO();
                JSONObject object =jsonArray.getJSONObject(i);
                dataAdapterVO.setText(object.getString("name"));
                dataAdapterVO.setQuestionsData(object.getString("questionsData"));
                dataAdapterVO.setImageUrl(object.has("imageUrl") ?object.getString("imageUrl"):null);
                dataAdapterVO.setAssociatedValue(object.getString("service"));
                dataAdapterVO.setIsbillFetch(object.getString("isbillFetch"));
                dataAdapterVO.setMinTxnAmount(object.getInt("minTxnAmount"));
                datalist.add(dataAdapterVO);
            }
        } catch (Exception e) {
            Utility.showToast(this, Content_Message.error_message);
        }
        return  datalist;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            operator.setEnabled(true);

            if(resultCode==RESULT_OK){

                if(requestCode==100){
                    operatorname =data.getStringExtra("operatorname");
                    operatorcode=data.getStringExtra("operator");

                    amountlayout.setVisibility(View.VISIBLE);

                    DataAdapterVO dataAdapterVO = (DataAdapterVO) data.getSerializableExtra("datavo");
                    operator.setText(operatorname);
                    operator.setTag(operatorcode);

                    operator.setError(null);
                    amount.setError(null);

                    //add fetch bill btn
                    if (dataAdapterVO.getIsbillFetch().equals("1")) {
                        fetchbill.setVisibility(View.VISIBLE);
                        amount.setEnabled(false);
                        isFetchBill=true;
                    } else {
                        fetchbill.setVisibility(View.GONE);
                        amount.setEnabled(true);
                        isFetchBill=false;
                    }

                    //add min Amt Layout
                    if(dataAdapterVO.getMinTxnAmount()!=null){
                        if(min_amt_layout.getChildCount()>0)min_amt_layout.removeAllViews();
                        minAmt=dataAdapterVO.getMinTxnAmount();
                        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
                        min_amt_layout.startAnimation(animFadeIn);
                        min_amt_layout.setVisibility(View.VISIBLE);
                        min_amt_layout.setBackgroundColor(Utility.getColorWithAlpha(Color.rgb(224,224,224), 0.5f));
                        min_amt_layout.setPadding(Utility.getPixelsFromDPs(Gas_Bill.this,15),Utility.getPixelsFromDPs(Gas_Bill.this,15),0,Utility.getPixelsFromDPs(Gas_Bill.this,15));

                        min_amt_layout.addView(DynamicLayout.billMinLayout(Gas_Bill.this,dataAdapterVO));

                    }else {
                        min_amt_layout.setVisibility(View.GONE);
                    }

                    //Remove dynamic cards from the layout and arraylist
                    if(dynamicCardViewContainer.getChildCount()>0) dynamicCardViewContainer.removeAllViews();
                    removefetchbilllayout();

                    questionsVOS.clear();

                    //Create dynamic cards of edit text
                    if(dataAdapterVO.getQuestionsData() !=null){
                        JSONArray jsonArray = new JSONArray(dataAdapterVO.getQuestionsData());
                        for(int i=0; i<jsonArray.length(); i++){
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            Gson gson = new Gson();
                            OxigenQuestionsVO oxigenQuestionsVO = gson.fromJson(jsonObject.toString(), OxigenQuestionsVO.class);

                            CardView cardView = Utility.getCardViewStyle(this);
                            //EditText et = new EditText(new ContextThemeWrapper(this,R.style.edittext));

                            EditText et = Utility.getEditText(Gas_Bill.this);
                            et.setId(View.generateViewId());
                            et.setHint(oxigenQuestionsVO.getQuestionLabel());
                            changeEdittextValue(et);
                            cardView.addView(et);
                            dynamicCardViewContainer.addView(cardView);
                            if(oxigenQuestionsVO.getInstructions()!=null){
                                TextView tv = Utility.getTextView(this, oxigenQuestionsVO.getInstructions());
                                dynamicCardViewContainer.addView(tv);
                            }
                            oxigenQuestionsVO.setElementId(et.getId());
                            questionsVOS.add(oxigenQuestionsVO);
                        }
                        EditText editText =(EditText) findViewById(questionsVOS.get(0).getElementId());
                        editText.requestFocus();
                    }

                }else if(requestCode==200 || requestCode== ApplicationConstant.REQ_ENACH_MANDATE || requestCode==ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE || requestCode== ApplicationConstant.REQ_SI_MANDATE || requestCode== ApplicationConstant.REQ_MANDATE_FOR_BILL_FETCH_ERROR || requestCode== ApplicationConstant.REQ_SI_FOR_BILL_FETCH_ERROR){
                    if(data !=null){
                        BillPayRequest.onActivityResult(this,data,requestCode);
                    }else {
                        Utility.showSingleButtonDialog(this,"Error !","Something went wrong, Please try again!",false);
                    }
                }

            }
        }catch (Exception e){
            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View view) {
        Utility.hideKeyboard(Gas_Bill.this);
        switch (view.getId()){
            case R.id.back_activity_button1:
                finish();
                break;
            case R.id.proceed:

                try {
                    JSONObject dataarray=getQuestionLabelDate(true);
                    if(dataarray==null)return;
                    if(isFetchBill){
                        BillPayRequest.proceedRecharge(this,isFetchBill,oxigenTransactionVOresp);
                    }else {
                        BillPayRequest.confirmationDialogBillPay(this, operator, amount ,dataarray , new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                            oxigenTransactionVO.setOperateName(operatorcode);
                            oxigenTransactionVO.setAmount(Double.valueOf(amount.getText().toString()));
                            oxigenTransactionVO.setAnonymousString(dataarray.toString());

                            ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                            serviceTypeVO.setServiceTypeId(ApplicationConstant.Gas);
                            oxigenTransactionVO.setServiceType(serviceTypeVO);

                            CustomerVO customerVO =new CustomerVO();
                            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
                            oxigenTransactionVO.setCustomer(customerVO);

                            BillPayRequest.proceedRecharge(this,isFetchBill,oxigenTransactionVO);
                        }));
                    }
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));

                }
                break;
            case R.id.fetchbill:
                try {
                    JSONObject dataarray=getQuestionLabelDate(false);
                    if(dataarray==null)return;
                    CustomerVO customerVO =new CustomerVO();
                    customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Gas_Bill.this)));

                    ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                    serviceTypeVO.setServiceTypeId(ApplicationConstant.Gas);

                    OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                    oxigenTransactionVO.setOperateName(operatorcode);
                    oxigenTransactionVO.setCustomer(customerVO);
                    oxigenTransactionVO.setServiceType(serviceTypeVO);
                    oxigenTransactionVO.setAnonymousString(dataarray.toString());

                    BillPayRequest.proceedFetchBill(oxigenTransactionVO,Gas_Bill.this,new VolleyResponse((VolleyResponse.OnSuccess)(s)->{
                        try {
                            oxigenTransactionVOresp=(OxigenTransactionVO)s;
                            fetchbill.setVisibility(View.GONE);
                            amount.setText(oxigenTransactionVOresp.getNetAmount()+"");

                            JSONArray dataArry =new JSONArray(oxigenTransactionVOresp.getAnonymousString());

                            Typeface typeface = ResourcesCompat.getFont(Gas_Bill.this, R.font.poppinssemibold);
                            for(int i=0 ;i<dataArry.length();i++){
                                JSONObject jsonObject =dataArry.getJSONObject(i);

                                LinearLayout et = new LinearLayout(new ContextThemeWrapper(Gas_Bill.this,R.style.confirmation_dialog_layout));

                                et.setPadding(Utility.getPixelsFromDPs(Gas_Bill.this,10),Utility.getPixelsFromDPs(Gas_Bill.this,10),Utility.getPixelsFromDPs(Gas_Bill.this,10),Utility.getPixelsFromDPs(Gas_Bill.this,10));

                                TextView text = new TextView(new ContextThemeWrapper(Gas_Bill.this, R.style.confirmation_dialog_filed));
                                text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                                text.setText(jsonObject.getString("key"));
                                text.setMaxLines(1);
                                text.setEllipsize(TextUtils.TruncateAt.END);
                                text.setTypeface(typeface);
                                text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                TextView value = new TextView(new ContextThemeWrapper(Gas_Bill.this, R.style.confirmation_dialog_value));
                                value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                                value.setText(jsonObject.getString("value"));
                                value.setTypeface(typeface);
                                value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                                et.addView(text);
                                et.addView(value);
                                fetchbilllayout.addView(et);
                            }
                            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fadein);
                            fetchbillcard.startAnimation(animFadeIn);
                            fetchbillcard.setVisibility(View.VISIBLE);
                        }catch (Exception e){
                            ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                        }
                    },(VolleyResponse.OnError)(e)->{
                        fetchbill.setVisibility(View.VISIBLE);
                    }));
                }catch (Exception e){
                    ExceptionsNotification.ExceptionHandling(this , Utility.getStackTrace(e));
                }
                break;
        }
    }


    private JSONObject getQuestionLabelDate(boolean fetchBill) throws Exception{
        return BillPayRequest.getQuestionLabelData(Gas_Bill.this,operator,amount,fetchBill,isFetchBill, questionsVOS,minAmt);
    }

    public void removefetchbilllayout(){
        oxigenTransactionVOresp=new OxigenTransactionVO();

        if(fetchbilllayout.getChildCount()>0) {
            fetchbilllayout.removeAllViews();
            amount.setText("");
            fetchbill.setVisibility(View.VISIBLE);
            fetchbillcard.setVisibility(View.GONE);
        }
    }

    public void changeEdittextValue(EditText editText){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                removefetchbilllayout();
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.w("onTextChanged",charSequence.toString());
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }

}

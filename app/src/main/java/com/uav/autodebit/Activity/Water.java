package com.uav.autodebit.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenQuestionsVO;
import com.uav.autodebit.vo.OxigenTransactionVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Water extends AppCompatActivity implements View.OnClickListener {


    EditText amount,operator;
    ImageView back_activity_button;
    String operatorcode,operatorname=null;
    Button proceed;
    TextView fetchbill;
    CardView amountlayout;

    LinearLayout dynamicCardViewContainer , fetchbilllayout;

    List<OxigenQuestionsVO> questionsVOS= new ArrayList<OxigenQuestionsVO>();
    CardView fetchbillcard;

    boolean valid=true;
    String operatorListDate;
    UAVProgressDialog pd;

    OxigenTransactionVO oxigenTransactionVOresp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_water);

        getSupportActionBar().hide();

        operatorListDate=null;
        pd=new UAVProgressDialog(this);

        amount.setEnabled(false);

        amount=findViewById(R.id.amount);
        back_activity_button=findViewById(R.id.back_activity_button1);
        proceed=findViewById(R.id.proceed);
        fetchbill=findViewById(R.id.fetchbill);
        amountlayout=findViewById(R.id.amountlayout);
        operator=findViewById(R.id.operator);
        dynamicCardViewContainer =findViewById(R.id.dynamiccards);
        fetchbilllayout=findViewById(R.id.fetchbilllayout);
        fetchbillcard =findViewById(R.id.fetchbillcard);

        oxigenTransactionVOresp=new OxigenTransactionVO();

        amountlayout.setVisibility(View.GONE);

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
                            Intent intent =new Intent(Water.this, Listview_With_Image.class);
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
        String operator= Session.getSessionByKey(Water.this,Session.CACHE_WATER_OPERATOR);
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
                datalist.add(dataAdapterVO);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  datalist;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            operator.setEnabled(true);

            if(resultCode==RESULT_OK){
                switch (requestCode) {
                    case 100:

                        operatorname =data.getStringExtra("operatorname");
                        operatorcode=data.getStringExtra("operator");

                        amountlayout.setVisibility(View.VISIBLE);


                        DataAdapterVO dataAdapterVO = (DataAdapterVO) data.getSerializableExtra("datavo");
                        operator.setText(operatorname);
                        operator.setTag(operatorcode);

                        operator.setError(null);
                        amount.setError(null);

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

                                EditText et = Utility.getEditText(Water.this);
                                et.setId(View.generateViewId());
                                et.setHint(oxigenQuestionsVO.getQuestionLabel());
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
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Water.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.back_activity_button1:
                finish();
                break;
            case R.id.proceed:

                try {
                    valid=true;

                    JSONObject dataarray=getQuestionLabelDate(true);
                    if(!valid)return;



                    proceedRecharge(oxigenTransactionVOresp);

                }catch (Exception e){
                    e.printStackTrace();
                    Utility.exceptionAlertDialog(Water.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                }


                break;
            case R.id.fetchbill:
                try {

                    Utility.hideKeyboard(Water.this);

                    valid=true;
                    JSONObject dataarray=getQuestionLabelDate(false);
                    if(!valid)return;
                    JSONObject jsonObject =new JSONObject();

                    CustomerVO customerVO =new CustomerVO();
                    customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Water.this)));

                    ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                    serviceTypeVO.setServiceTypeId(ApplicationConstant.Landline);

                    OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                    oxigenTransactionVO.setOperateName(operatorcode);
                    oxigenTransactionVO.setCustomer(customerVO);
                    oxigenTransactionVO.setServiceType(serviceTypeVO);
                    oxigenTransactionVO.setAnonymousString(dataarray.toString());


                    proceedFetchBill(oxigenTransactionVO);

                }catch (Exception e){
                    e.printStackTrace();
                    Utility.exceptionAlertDialog(Water.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                }
                valid=true;
                break;
        }
    }

    private JSONObject getQuestionLabelDate(boolean fetchBill) throws Exception{
        amount.setError(null);
        operator.setError(null);

        if(fetchBill){
            if(amount.getText().toString().equals("")){
                amount.setError("this filed is required");
                valid=false;
            }
        }

        if(operator.getText().toString().equals("")){
            operator.setError("this filed is required");
            valid=false;
        }

        JSONObject jsonObject =new JSONObject();

        for(OxigenQuestionsVO oxigenQuestionsVO:questionsVOS){

            EditText editText =(EditText) findViewById(oxigenQuestionsVO.getElementId());
            editText.clearFocus();
            changeEdittextValue(editText);

            editText.setError(null);
            if(editText.getText().toString().equals("")){

                editText.setError(  Utility.getErrorSpannableStringDynamicEditText(this, "this field is required"));
                valid=false;
            }else if(oxigenQuestionsVO.getMinLength()!=null && (editText.getText().toString().length() < Integer.parseInt(oxigenQuestionsVO.getMinLength()))){
                editText.setError(oxigenQuestionsVO.getMinLength());
                valid=false;
            }else if(oxigenQuestionsVO.getMaxLength()!=null && (editText.getText().toString().length() > Integer.parseInt(oxigenQuestionsVO.getMaxLength()))){
                editText.setError(oxigenQuestionsVO.getMaxLength());
                valid=false;
            }

            jsonObject.put(oxigenQuestionsVO.getQuestionLabel(),editText.getText().toString());
            //oxigenQuestionsVO.getJsonKey();
            //editText.getText().toString();

        }
        return jsonObject;
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


    private  void proceedRecharge(OxigenTransactionVO oxigenTransactionVO){
        if(oxigenTransactionVO==null || oxigenTransactionVOresp.getTypeId()==null){
            Utility.showSingleButtonDialogconfirmation(Water.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ok.dismiss();
            }),"Alert","Bill fetch Id is null");
        }else {
            Toast.makeText(this, ""+oxigenTransactionVOresp.getTypeId(), Toast.LENGTH_SHORT).show();

        }
    }


    private void proceedFetchBill(OxigenTransactionVO oxigenTransactionVO) throws Exception{
        try {
            Gson gson =new Gson();

            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = Electricity_BillBO.oxiFetchBill();

            params.put("volley",gson.toJson(oxigenTransactionVO));

            Log.w("proceedFetchBill",gson.toJson(oxigenTransactionVO));
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(Water.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Log.w("respele",response.toString());
                    Gson gson = new Gson();
                    oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                    if(oxigenTransactionVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) oxigenTransactionVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        fetchbill.setVisibility(View.VISIBLE);
                        Utility.showSingleButtonDialog(Water.this,"Error !",sb.toString(),false);
                    }else if(oxigenTransactionVOresp.getStatusCode().equals("01")){
                        fetchbill.setVisibility(View.VISIBLE);
                        Utility.showSingleButtonDialogconfirmation(Water.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                        }),"Alert",oxigenTransactionVOresp.getAnonymousString());
                    }else {
                        fetchbill.setVisibility(View.GONE);
                        amount.setText(oxigenTransactionVOresp.getAmount()+"");

                        JSONArray dataArry =new JSONArray(oxigenTransactionVOresp.getAnonymousString());

                        Typeface typeface = ResourcesCompat.getFont(Water.this, R.font.poppinssemibold);
                        for(int i=0 ;i<dataArry.length();i++){
                            JSONObject jsonObject =dataArry.getJSONObject(i);

                            LinearLayout et = new LinearLayout(new ContextThemeWrapper(Water.this,R.style.confirmation_dialog_layout));

                            et.setPadding(Utility.getPixelsFromDPs(Water.this,10),Utility.getPixelsFromDPs(Water.this,10),Utility.getPixelsFromDPs(Water.this,10),Utility.getPixelsFromDPs(Water.this,10));

                            TextView text = new TextView(new ContextThemeWrapper(Water.this, R.style.confirmation_dialog_filed));
                            text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                            text.setText(jsonObject.getString("key"));
                            text.setMaxLines(1);
                            text.setEllipsize(TextUtils.TruncateAt.END);
                            text.setTypeface(typeface);
                            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                            TextView value = new TextView(new ContextThemeWrapper(Water.this, R.style.confirmation_dialog_value));
                            value.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT,1));
                            value.setText(jsonObject.getString("value"));
                            value.setTypeface(typeface);
                            value.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                            et.addView(text);
                            et.addView(value);
                            fetchbilllayout.addView(et);
                        }
                        fetchbillcard.setVisibility(View.VISIBLE);
                    }
                }

            });
        } catch (Exception e) {
            e.printStackTrace();
            Utility.exceptionAlertDialog(Water.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }
}

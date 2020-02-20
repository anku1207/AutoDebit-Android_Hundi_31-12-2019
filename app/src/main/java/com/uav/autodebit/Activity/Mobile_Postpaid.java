package com.uav.autodebit.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.uav.autodebit.BO.Electricity_BillBO;
import com.uav.autodebit.BO.OxigenPlanBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.override.DrawableClickListener;
import com.uav.autodebit.override.UAVEditText;
import com.uav.autodebit.override.UAVProgressDialog;
import com.uav.autodebit.permission.PermissionHandler;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.BackgroundAsyncService;
import com.uav.autodebit.util.BackgroundServiceInterface;
import com.uav.autodebit.util.CustomTextWatcherLengthAction;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.OxigenPlanVO;
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

public class Mobile_Postpaid extends AppCompatActivity implements View.OnClickListener {


    EditText amount,operator;
    ImageView back_activity_button;
    String operatorcode,operatorname=null;
    Button proceed;
    TextView fetchbill;
    CardView amountlayout;

    LinearLayout dynamicCardViewContainer , fetchbilllayout;

    List<OxigenQuestionsVO> questionsVOS= new ArrayList<OxigenQuestionsVO>();
    CardView fetchbillcard;

    boolean valid=true,isFetchBill=true;
    String operatorListDate;
    UAVProgressDialog pd;
    OxigenTransactionVO oxigenTransactionVOresp;
    Gson gson;

    HashMap<String,Object> eleMap;



    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile__postpaid);
        getSupportActionBar().hide();

        // disable autofill edittext
        disableAutofill();

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

        oxigenTransactionVOresp=new OxigenTransactionVO();
        gson =new Gson();

        eleMap=new HashMap<>();

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
                            operatorListDate = gson.toJson(getDataList());
                        }
                        @Override
                        public void doPostExecute() {
                            Intent intent =new Intent(Mobile_Postpaid.this, Listview_With_Image.class);
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
        String operator= Session.getSessionByKey(Mobile_Postpaid.this,Session.CACHE_POSTPAID_OPERATOR);
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
                        //clear element maplist
                        eleMap.clear();

                        operatorname =data.getStringExtra("operatorname");
                        operatorcode=data.getStringExtra("operator");

                        amountlayout.setVisibility(View.VISIBLE);

                        DataAdapterVO dataAdapterVO = (DataAdapterVO) data.getSerializableExtra("datavo");
                        operator.setError(null);
                        amount.setError(null);
                        operator.setText(operatorname);
                        operator.setTag(operatorcode);

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

                        //Remove dynamic cards from the layout and arraylist
                        if(dynamicCardViewContainer.getChildCount()>0) dynamicCardViewContainer.removeAllViews();
                        removefetchbilllayout();

                        questionsVOS.clear();

                        //Create dynamic cards of edit text
                        if(dataAdapterVO.getQuestionsData() !=null){
                            JSONArray jsonArray = new JSONArray(dataAdapterVO.getQuestionsData());
                            for(int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                OxigenQuestionsVO oxigenQuestionsVO = gson.fromJson(jsonObject.toString(), OxigenQuestionsVO.class);

                                CardView cardView = Utility.getCardViewStyle(this);
                                UAVEditText et = Utility.getUavEditText(Mobile_Postpaid.this);
                                et.setId(View.generateViewId());
                                et.setHint(oxigenQuestionsVO.getQuestionLabel());

                                if(oxigenQuestionsVO.getQuestionLabel().contains("Mobile")){
                                    eleMap.put("mobile",et);
                                    Drawable drawable = getResources().getDrawable(R.drawable.contacts);
                                    drawable = DrawableCompat.wrap(drawable);
                                    DrawableCompat.setTint(drawable, getResources().getColor(R.color.appbar));

                                    et.setCompoundDrawablesWithIntrinsicBounds(R.drawable.mobile,0 , R.drawable.contacts, 0);
                                    et.setDrawableClickListener(new DrawableClickListener() {
                                        @Override
                                        public void onClick(DrawablePosition target) {
                                            switch (target) {
                                                case RIGHT:
                                                        if(PermissionHandler.contactpermission(Mobile_Postpaid.this)){
                                                            Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                                                            startActivityForResult(intent, 101);
                                                    }
                                                    break;
                                            }
                                        }
                                    });
                                }

                              //  changeEdittextValue(et);

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
                    case 101:
                        Uri contactData = data.getData();
                        Cursor c = getContentResolver().query(contactData, null, null, null, null);
                        if (c.moveToFirst()) {
                            String contactId = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));
                            String hasNumber = c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            String num = "";
                            if (Integer.valueOf(hasNumber) == 1) {
                                Cursor numbers = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                                while (numbers.moveToNext()) {
                                    num = numbers.getString(numbers.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+","");
                                }
                                if (num.length()>10) {
                                    num=num.substring(num.length() - 10);
                                }
                                ((EditText)eleMap.get("mobile")).setText(num);
                                ((EditText)eleMap.get("mobile")).setSelection( ((EditText)eleMap.get("mobile")).getText().toString().length());
                                amount.setText("");
                            }
                        }
                        break;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Mobile_Postpaid.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
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
                    Utility.hideKeyboard(Mobile_Postpaid.this);
                    valid=true;

                    JSONObject dataarray=getQuestionLabelDate(true);
                    if(!valid)return;
                        OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                        oxigenTransactionVO.setOperateName(operatorcode);
                        oxigenTransactionVO.setAmount(Double.valueOf(amount.getText().toString()));
                        oxigenTransactionVO.setAnonymousString(dataarray.toString());
                    proceedRecharge(oxigenTransactionVOresp.getTypeId()!=null?oxigenTransactionVOresp:oxigenTransactionVO);

                }catch (Exception e){
                    e.printStackTrace();
                    Utility.exceptionAlertDialog(Mobile_Postpaid.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));

                }


                break;
            case R.id.fetchbill:
                try {
                    Utility.hideKeyboard(Mobile_Postpaid.this);

                    valid=true;
                    JSONObject dataarray=getQuestionLabelDate(false);
                    if(!valid)return;
                    JSONObject jsonObject =new JSONObject();

                    CustomerVO customerVO =new CustomerVO();
                    customerVO.setCustomerId(Integer.parseInt(Session.getCustomerId(Mobile_Postpaid.this)));

                    ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
                    serviceTypeVO.setServiceTypeId(ApplicationConstant.Electricity);

                    OxigenTransactionVO oxigenTransactionVO =new OxigenTransactionVO();
                    oxigenTransactionVO.setOperateName(operatorcode);
                    oxigenTransactionVO.setCustomer(customerVO);
                    oxigenTransactionVO.setServiceType(serviceTypeVO);
                    oxigenTransactionVO.setAnonymousString(dataarray.toString());

                    proceedFetchBill(oxigenTransactionVO);
                }catch (Exception e){
                    e.printStackTrace();
                    Utility.exceptionAlertDialog(Mobile_Postpaid.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
                }
                break;
        }
    }

    private JSONObject getQuestionLabelDate(boolean fetchBill) throws Exception{
        amount.setError(null);
        operator.setError(null);

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


        if(fetchBill && !isFetchBill && valid){
            if(amount.getText().toString().equals("")){
                amount.setError("this filed is required");
                valid=false;
            }
        }else if(fetchBill && isFetchBill && valid){
            if(amount.getText().toString().equals("")){
                Utility.showSingleButtonDialogconfirmation(Mobile_Postpaid.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                    ok.dismiss();
                }),"Alert","Bill Amount is null ");
                valid=false;
            }

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
        if(oxigenTransactionVO==null){
            Utility.showSingleButtonDialogconfirmation(Mobile_Postpaid.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ok.dismiss();
            }),"Alert","Empty Filed not Allow!");
        }else if(isFetchBill && oxigenTransactionVO.getTypeId()==null){
            Utility.showSingleButtonDialogconfirmation(Mobile_Postpaid.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                ok.dismiss();
            }),"Alert","Bill Fetch Is required!");
        }else {
            proceedBillPayment(oxigenTransactionVO);
        }
    }

    private void proceedBillPayment(OxigenTransactionVO oxigenTransactionVO) {

        try {
            HashMap<String, Object> params = new HashMap<String, Object>();

            ServiceTypeVO serviceTypeVO =new ServiceTypeVO();
            serviceTypeVO.setServiceTypeId(ApplicationConstant.MobilePostpaid);

            CustomerVO customerVO =new CustomerVO();
            customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(Mobile_Postpaid.this)));
            ConnectionVO connectionVO = OxigenPlanBO.oxiBillPayment();
            oxigenTransactionVO.setCustomer(customerVO);
            oxigenTransactionVO.setServiceType(serviceTypeVO);
            params.put("volley",gson.toJson(oxigenTransactionVO));
            connectionVO.setParams(params);

            Log.w("requestData",gson.toJson(oxigenTransactionVO));

            VolleyUtils.makeJsonObjectRequest(Mobile_Postpaid.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) {
                    JSONObject response = (JSONObject) resp;

                    oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                    if(oxigenTransactionVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) oxigenTransactionVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        Utility.showSingleButtonDialog(Mobile_Postpaid.this,"Error !",sb.toString(),false);
                    }else {
                        startActivity(new Intent(Mobile_Postpaid.this,History.class));
                        finish();
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            Utility.exceptionAlertDialog(Mobile_Postpaid.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }


    private void proceedFetchBill(OxigenTransactionVO oxigenTransactionVO) throws Exception{

        try {


            HashMap<String, Object> params = new HashMap<String, Object>();
            ConnectionVO connectionVO = Electricity_BillBO.oxiFetchBill();

            params.put("volley",gson.toJson(oxigenTransactionVO));

            Log.w("proceedFetchBill",gson.toJson(oxigenTransactionVO));
            connectionVO.setParams(params);

            VolleyUtils.makeJsonObjectRequest(Mobile_Postpaid.this,connectionVO, new VolleyResponseListener() {
                @Override
                public void onError(String message) {
                }
                @Override
                public void onResponse(Object resp) throws JSONException {
                    JSONObject response = (JSONObject) resp;

                    Log.w("respele",response.toString());

                    oxigenTransactionVOresp = gson.fromJson(response.toString(), OxigenTransactionVO.class);

                    if(oxigenTransactionVOresp.getStatusCode().equals("400")){
                        ArrayList error = (ArrayList) oxigenTransactionVOresp.getErrorMsgs();
                        StringBuilder sb = new StringBuilder();
                        for(int i=0; i<error.size(); i++){
                            sb.append(error.get(i)).append("\n");
                        }
                        fetchbill.setVisibility(View.VISIBLE);
                        Utility.showSingleButtonDialog(Mobile_Postpaid.this,"Error !",sb.toString(),false);
                    }else if(oxigenTransactionVOresp.getStatusCode().equals("01")){
                        fetchbill.setVisibility(View.VISIBLE);
                        Utility.showSingleButtonDialogconfirmation(Mobile_Postpaid.this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                            ok.dismiss();
                        }),"Alert",oxigenTransactionVOresp.getAnonymousString());
                    }else {
                        fetchbill.setVisibility(View.GONE);
                        amount.setText(oxigenTransactionVOresp.getAmount()+"");

                        JSONArray dataArry =new JSONArray(oxigenTransactionVOresp.getAnonymousString());

                        Typeface typeface = ResourcesCompat.getFont(Mobile_Postpaid.this, R.font.poppinssemibold);
                        for(int i=0 ;i<dataArry.length();i++){
                            JSONObject jsonObject =dataArry.getJSONObject(i);

                            LinearLayout et = new LinearLayout(new ContextThemeWrapper(Mobile_Postpaid.this,R.style.confirmation_dialog_layout));

                            et.setPadding(Utility.getPixelsFromDPs(Mobile_Postpaid.this,10),Utility.getPixelsFromDPs(Mobile_Postpaid.this,10),Utility.getPixelsFromDPs(Mobile_Postpaid.this,10),Utility.getPixelsFromDPs(Mobile_Postpaid.this,10));

                            TextView text = new TextView(new ContextThemeWrapper(Mobile_Postpaid.this, R.style.confirmation_dialog_filed));
                            text.setLayoutParams(new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, (float) 1));
                            text.setText(jsonObject.getString("key"));
                            text.setMaxLines(1);
                            text.setEllipsize(TextUtils.TruncateAt.END);
                            text.setTypeface(typeface);
                            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);


                            TextView value = new TextView(new ContextThemeWrapper(Mobile_Postpaid.this, R.style.confirmation_dialog_value));
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
            Utility.exceptionAlertDialog(Mobile_Postpaid.this,"Alert!","Something went wrong, Please try again!","Report",Utility.getStackTrace(e));
        }
    }
}

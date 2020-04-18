package com.uav.autodebit.Activity;

import android.content.Context;
import android.content.Intent;

import android.os.Bundle;

import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.uav.autodebit.BO.MandateBO;
import com.uav.autodebit.Interface.ConfirmationDialogInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.R;
import com.uav.autodebit.adpater.ListViewItemCheckboxBaseAdapter;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.constant.Content_Message;
import com.uav.autodebit.override.ExpandableHeightListView;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.ExceptionHandler;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.vo.DataAdapterVO;
import com.uav.autodebit.vo.ServiceTypeVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddBankAndServicelist extends AppCompatActivity implements View.OnClickListener {
    LinearLayout mainlayout;

    EditText acno,acholdername;
    Spinner account_type;
    ImageView back_activity_button;
    JSONArray accountTypeJsonArray,bankJsonArray;

    JSONObject selectBankJson,selectBankType;
    ExpandableHeightListView listview;
    Button nextbtn;
    ListViewItemCheckboxBaseAdapter myAdapter;
    ScrollView scrollview;

    EditText bankname;
    ArrayList<DataAdapterVO> datalist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_and_servicelist);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        getSupportActionBar().hide();

        setElementId();
        accountTypeJsonArray=new JSONArray();
        bankJsonArray=new JSONArray();

        selectBankJson = new JSONObject();

        scrollview.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event){
                if (event != null && event.getAction() == MotionEvent.ACTION_MOVE){
                    InputMethodManager imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
                    boolean isKeyboardUp = imm.isAcceptingText();
                    if (isKeyboardUp){
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
                return false;
            }
        });


        banklist(new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
            scrollview.setVisibility(View.VISIBLE);
            CustomerVO customerVO =(CustomerVO) success;



            bankname.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(MotionEvent.ACTION_UP == motionEvent.getAction()) {

                        Layout layout = ((EditText) view).getLayout();
                        int  x = (int) motionEvent.getX();
                        int  y = (int) motionEvent.getY();

                        Intent intent =new Intent(AddBankAndServicelist.this, PopapSimpleList.class);
                        intent.putExtra("datalist", new Gson().toJson( datalist));
                        intent.putExtra("title","Operator");
                        intent.putExtra("x",x);
                        intent.putExtra("y",y);
                        startActivityForResult(intent, ApplicationConstant.REQ_POPAPACTIVITYRESULT);

                    }
                    return false;
                }
            });




            account_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    try{
                        selectBankType =accountTypeJsonArray.getJSONObject(i);
                    }catch (Exception e){
                        Toast.makeText(AddBankAndServicelist.this, "Something went wrong, Please try again!", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        }));
    }

    private void setElementId() {
        acno=findViewById(R.id.acno);
        acholdername=findViewById(R.id.acholdername);
        bankname=findViewById(R.id.bankname);
        account_type=findViewById(R.id.account_type);
        back_activity_button=findViewById(R.id.back_activity_button);
        listview=findViewById(R.id.listview);
        nextbtn=findViewById(R.id.nextbtn);
        scrollview=findViewById(R.id.scrollview);

        nextbtn.setOnClickListener(this);
        back_activity_button.setOnClickListener(this);

        datalist = new ArrayList<>();


    }

    private void banklist(VolleyResponse volleyResponse){

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = MandateBO.getBankListAndAccountType();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
        Gson gson =new Gson();
        String json = gson.toJson(customerVO);
        params.put("volley", json);
        connectionVO.setParams(params);
        Log.w("banklist",params.toString());
        VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
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
                    Utility.showSingleButtonDialog(AddBankAndServicelist.this,"Alert",sb.toString(),false);
                }else {

                    JSONObject jsonObject =new JSONObject(customerVO.getAnonymousString());

                    JSONObject object = new JSONObject(jsonObject.getString("result"));
                    bankJsonArray=object.getJSONArray("data");

                    for(int i=0;i<bankJsonArray.length();i++){
                        JSONObject bankjson=(bankJsonArray.getJSONObject(i));
                        DataAdapterVO dataAdapterVO = new DataAdapterVO();
                        dataAdapterVO.setText(bankjson.getString("name"));
                        dataAdapterVO.setAssociatedValue(bankjson.getString("id"));
                        datalist.add(dataAdapterVO);
                    }


                    accountTypeJsonArray =new JSONArray(jsonObject.getString("accountType"));
                    ArrayList accountList=new ArrayList();
                    for(int i=0;i< accountTypeJsonArray.length();i++){
                        JSONObject object1=(accountTypeJsonArray.getJSONObject(i));
                        accountList.add(object1.getString("key"));
                    }
                    ArrayAdapter adapter = new ArrayAdapter<String>(AddBankAndServicelist.this,
                            R.layout.spinner_item,accountList);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    account_type.setAdapter(adapter);

                    ArrayList<ServiceTypeVO> serviceTypeVOS= (ArrayList<ServiceTypeVO>) new Gson().fromJson(jsonObject.getString("services"), new TypeToken<ArrayList<ServiceTypeVO>>() { }.getType());
                    myAdapter=new ListViewItemCheckboxBaseAdapter(AddBankAndServicelist.this, serviceTypeVOS, R.layout.checkbox_with_text);
                    listview.setAdapter(myAdapter);
                    listview.setExpanded(true);

                    volleyResponse.onSuccess(customerVO);
                }
            }
        });
    }



    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.nextbtn){
            ArrayList<Integer> addservice=new ArrayList<>();
            SparseBooleanArray checked = myAdapter.mCheckStates;
            int size = checked.size(); // number of name-value pairs in the array
            for (int i = 0; i < size; i++) {
                int key = checked.keyAt(i);
                boolean value = checked.get(key);
                if (value){
                    addservice.add(myAdapter.mCheckStates.keyAt(i));
                }
            }
            if(checkEmptyFiled(addservice)) {

               checkValidateAdoptService(addservice,new VolleyResponse((VolleyResponse.OnSuccess)(success)->{
                   Intent intent =new Intent(AddBankAndServicelist.this, Enach_Mandate.class);
                   intent.putExtra("selectservice", addservice);
                   intent.putExtra("forresutl", true);

                   intent.putExtra("addMoreServiceMandate", true);
                   intent.putExtra("bankJson", selectBankJson.toString());
                   intent.putExtra("accountType", selectBankType.toString());
                   intent.putExtra("accountNumber", acno.getText().toString());
                   intent.putExtra("accountHolderName",acholdername.getText().toString());

                   startActivityForResult(intent,ApplicationConstant.REQ_ENACH_MANDATE);
               }));
            }else {
                scrollview.fullScroll(ScrollView.FOCUS_UP);
            }
        }else if(view.getId()==R.id.back_activity_button){
            finish();
        }
    }

    private boolean checkEmptyFiled(ArrayList arrayList){
        boolean validation=true;

        if(acno.getText().toString().trim().equals("")){
            acno.setError("this filed is required");
            validation=false;
        }


        if(bankname.getText().toString().trim().equals("")){
            bankname.setError("this filed is required");
            validation=false;
        }

        if(acholdername.getText().toString().trim().equals("")){
            acholdername.setError("this filed is required");
            validation=false;
        }

        if( selectBankJson.length()==0){
            if(!bankname.getText().toString().equals("") ){
                Utility.alertDialog(this,"Alert","Bank is not selected","Ok");
                validation=false;
            }
       }

        if( selectBankType.length()==0){
            Utility.alertDialog(this,"Alert","Account Type is not selected","Ok");
            validation=false;
        }
        if(acno.getText().toString().trim().length()<5){
            acno.setError("Minimum length is 5");
            validation=false;
        }
        if(validation){
            if(arrayList.size()==0){
                Toast.makeText(this, "There is no service select", Toast.LENGTH_LONG).show();
                validation=false;
            }
        }
        return validation;
   }

   private void checkValidateAdoptService(ArrayList arrayList,VolleyResponse onSuccess){
       HashMap<String, Object> params = new HashMap<String, Object>();
       ConnectionVO connectionVO = MandateBO.checkValidateAdoptedServies();
       CustomerVO customerVO=new CustomerVO();
       customerVO.setCustomerId(Integer.valueOf(Session.getCustomerId(this)));
       customerVO.setAnonymousString(arrayList.toString());
       Gson gson =new Gson();
       String json = gson.toJson(customerVO);
       params.put("volley", json);
       connectionVO.setParams(params);
       Log.w("volley_request",params.toString());
       VolleyUtils.makeJsonObjectRequest(this,connectionVO, new VolleyResponseListener() {
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
                   Utility.showSingleButtonDialog(AddBankAndServicelist.this,"Alert",sb.toString(),false);
               }else {
                   onSuccess.onSuccess(customerVO);
               }
           }
       });
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode== RESULT_OK){
            if(requestCode==ApplicationConstant.REQ_ENACH_MANDATE){
                if(data !=null){
                    Utility.showSingleButtonDialogconfirmation(this,new ConfirmationDialogInterface((ConfirmationDialogInterface.OnOk)(ok)->{
                        if(data.getBooleanExtra("mandate_status",false)){
                            ok.dismiss();
                            Intent intent =  new Intent();
                            setResult(RESULT_OK,intent);
                            finish();
                        }else {
                            ok.dismiss();
                        }
                    }),"", Content_Message.mandate_Success_Message);
                }else {
                    Toast.makeText(this, Content_Message.error_message, Toast.LENGTH_SHORT).show();
                }

            }else if(requestCode==ApplicationConstant.REQ_POPAPACTIVITYRESULT){
                if(data!=null){
                    try {
                        bankname.setError(null);
                        bankname.setText(data.getStringExtra("operatorname"));

                        selectBankJson.put("name",data.getStringExtra("operatorname"));
                        selectBankJson.put("id",data.getStringExtra("operator"));
                    }catch (Exception ignored){

                    };
                }else {
                    Toast.makeText(this, "Bank Name Is Null Selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}

package com.uav.autodebit.Activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.paynimo.android.payment.PaymentActivity;
import com.paynimo.android.payment.PaymentModesActivity;
import com.paynimo.android.payment.model.Checkout;
import com.paynimo.android.payment.util.Constant;
import com.uav.autodebit.BO.SiBO;
import com.uav.autodebit.R;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.permission.Session;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.ConnectionVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.CustomerVO;
import com.uav.autodebit.volley.VolleyResponseListener;
import com.uav.autodebit.volley.VolleyUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class Paynimo_HDFC extends Base_Activity {
    Gson gson = new Gson();
    ImageView back_activity_button1;

 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paynimo__hdfc);
        getSupportActionBar().hide();

        back_activity_button1=findViewById(R.id.back_activity_button1);
        sifirstdata();

         back_activity_button1.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 finish();
             }
         });

    }

    public void sifirstdata(){

        String customerId= Session.getCustomerId(Paynimo_HDFC.this);
        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SiBO.getSIMandateProperties();
        CustomerVO customerVO=new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(customerId));
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
                JSONObject respjson = (JSONObject) resp;


                if(!respjson.getString("status").equals("200")){
                    Utility.alertDialog(Paynimo_HDFC.this,"Alert",respjson.getString("errorMsg"),"Ok");
                }else {
                    if(ApplicationConstant.SI_SERVICE.equals("hdfc")){
                        JSONObject object1=respjson.getJSONObject("consumerData");
                        Log.w("resp",object1.toString());

                        Checkout checkout=new Checkout();
                        checkout.setMerchantIdentifier(object1.getString("merchantId"));
                        checkout.setTransactionIdentifier("txnId");
                        checkout.setTransactionReference(object1.getString("transactionRefNumber"));
                        checkout.setTransactionType(PaymentActivity.TRANSACTION_TYPE_SALE);
                        checkout.setTransactionSubType (PaymentActivity.TRANSACTION_SUBTYPE_DEBIT);
                        checkout.setTransactionCurrency (object1.getString("currency"));
                        JSONArray jsonArray=object1.getJSONArray("items");
                        JSONObject object =jsonArray.getJSONObject(0);
                        checkout.setTransactionAmount (object.getString("amount"));
                        checkout.addCartItem(object.getString("itemId"),object.getString("amount"),"ProductSurchargeOrDiscountAmount", object.getString("comAmt"), "ProductSKU", "ProductReference", "ProductDescriptor","ProductProviderID");

                        checkout.setTransactionDateTime(object1.getString("transactionDate"));
                        checkout.setConsumerIdentifier (object1.getString("consumerId"));
                        checkout.setConsumerEmailID (object1.getString("consumerEmailId"));
                        checkout.setConsumerMobileNumber (object1.getString("consumerMobileNo"));
                        checkout.setConsumerMobileNumber (object1.getString("consumerMobileNo"));


                        checkout.setPaymentInstructionAction("Y");
                        checkout.setPaymentInstructionType(object1.getString("amountType"));
                        checkout.setPaymentInstructionLimit(object1.getString("maxAmount"));
                        checkout.setPaymentInstructionFrequency(object1.getString("frequency"));
                        checkout.setPaymentInstructionStartDateTime(object1.getString("debitStartDate"));
                        checkout.setPaymentInstructionEndDateTime(object1.getString("debitEndDate"));



                        Intent authIntent = new Intent(Paynimo_HDFC.this, PaymentModesActivity.class);

                        // Checkout Object
                        Log.d("Checkout Request Object",
                                checkout.getMerchantRequestPayload().toString());
                        authIntent.putExtra(Constant.ARGUMENT_DATA_CHECKOUT,
                                checkout);
                        // Public Key
                       /* authIntent.putExtra(PaymentActivity.EXTRA_PUBLIC_KEY,
                                "1234-6666-6789-56");*/
                        // Requested Payment Mode
                        authIntent.putExtra(PaymentActivity.EXTRA_REQUESTED_PAYMENT_MODE,
                                PaymentActivity.PAYMENT_METHOD_CARDS);

                        startActivityForResult(authIntent, PaymentActivity.REQUEST_CODE);


                    }

                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PaymentActivity.REQUEST_CODE) {

            // Make sure the request was successful
            if (resultCode == PaymentActivity.RESULT_OK) {
                Log.w( "Result Code :", String.valueOf(RESULT_OK));

                if (data != null) {
                    Checkout checkout_res = (Checkout) data
                            .getSerializableExtra(Constant
                                    .ARGUMENT_DATA_CHECKOUT);

                    String result = gson.toJson(checkout_res);

                    Log.w("respdata",result);

                    Log.w("Checkout Response Obj", data.toString());
                    String transactionType = checkout_res.
                            getMerchantRequestPayload().getTransaction().getType();
                    String transactionSubType = checkout_res.
                            getMerchantRequestPayload().getTransaction().getSubType();
                    Log.w("transactionType",transactionType);

                    if (checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                            PaymentActivity.TRANSACTION_STATUS_SALES_DEBIT_SUCCESS)) {
                        Log.v("TRANSACTION STATUS=>", "SUCCESS");

                        /*
                         * TRANSACTION STATUS - SUCCESS (status code
                         * 0300 means success), NOW MERCHANT CAN PERFORM
                         * ANY OPERATION OVER SUCCESS RESULT
                         */


                       // paynimorespSave("123");


                        if (checkout_res.getMerchantResponsePayload().
                                getPaymentMethod().getPaymentTransaction().
                                getInstruction().getId() != null && checkout_res.getMerchantResponsePayload().
                                getPaymentMethod().getPaymentTransaction().
                                getInstruction().getId().isEmpty()) {
                            Log.v("TRANSACTION SI STATUS=>",
                                    "SI Transaction Not Initiated");
                            showSingleButtonDialog(this,"SI Transaction Not Initiated");

                           // Toast.makeText(this, "TRANSACTION SI STATUS=>SI Transaction Not Initiated", Toast.LENGTH_SHORT).show();
                        } else if (checkout_res.getMerchantResponsePayload().
                                getPaymentMethod().getPaymentTransaction().
                                getInstruction().getId() != null && !checkout_res.getMerchantResponsePayload().
                                getPaymentMethod().getPaymentTransaction().
                                getInstruction().getId().isEmpty()) {

                               String id=checkout_res.getMerchantResponsePayload().
                                       getPaymentMethod().getPaymentTransaction().
                                       getInstruction().getId();


                                paynimorespSave(id);
                             /**
                             * SI TRANSACTION STATUS - SUCCESS (Mandate  Registration ID received means success)
                             */
                            Log.v("TRANSACTION SI STATUS=>", "SUCCESS");
                        }
                    }else if(checkout_res.getMerchantResponsePayload().getPaymentMethod().getPaymentTransaction().getStatusCode().equalsIgnoreCase(
                            "0399")){

                        //Toast.makeText(this, checkout_res.getMerchantResponsePayload().
                          //      getPaymentMethod().getPaymentTransaction().getErrorMessage(), Toast.LENGTH_SHORT).show();

                        Log.w("error",checkout_res.getMerchantResponsePayload().
                                      getPaymentMethod().getPaymentTransaction().getErrorMessage());
                        showSingleButtonDialog(this,checkout_res.getMerchantResponsePayload().
                                getPaymentMethod().getPaymentTransaction().getErrorMessage());
                    }




                }
            }else if (resultCode == PaymentActivity.RESULT_ERROR) {
                Log.w("got an error","error");

                if (data.hasExtra(PaymentActivity.RETURN_ERROR_CODE) &&
                        data.hasExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION)) {
                    String error_code = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_CODE);
                    String error_desc = (String) data
                            .getStringExtra(PaymentActivity.RETURN_ERROR_DESCRIPTION);
                    Toast.makeText(getApplicationContext(), " Got error :"
                            + error_code + "--- " + error_desc, Toast.LENGTH_SHORT)
                            .show();
                    Log.w( "Code=>", error_code);
                    Log.w(" Desc=>", error_desc);
                    showSingleButtonDialog(this,error_desc);

                }
            }
            else if (resultCode == PaymentActivity.RESULT_CANCELED) {
                finish();
                Log.w("User_pressed_back","User_pressed_back_button");
                showSingleButtonDialog(this,"User_pressed_back_button");
            }
        }
   }



    public void showSingleButtonDialog(Context var1, String var2) {
        final Dialog var3 = new Dialog(var1);
        var3.requestWindowFeature(1);
        var3.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        var3.setContentView(var1.getResources().getIdentifier("singlebuttondialog", "layout", var1.getPackageName()));
        var3.setCanceledOnTouchOutside(false);
        TextView var4 = (TextView)var3.findViewById(var1.getResources().getIdentifier("dialog_one_tv_title", "id", var1.getPackageName()));
        var4.setText("Error !");
        TextView var6 = (TextView)var3.findViewById(var1.getResources().getIdentifier("dialog_one_tv_text", "id", var1.getPackageName()));

        var6.setText(var2);
        Button var5 = (Button)var3.findViewById(var1.getResources().getIdentifier("dialog_one_btn", "id", var1.getPackageName()));
        var5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View var1) {
                var3.dismiss();
                finish();
            }
        });
        var3.show();
    }


    public void paynimorespSave(String id) {


        String customerId = Session.getCustomerId(Paynimo_HDFC.this);

        HashMap<String, Object> params = new HashMap<String, Object>();
        ConnectionVO connectionVO = SiBO.validateSIMandate();

        CustomerAuthServiceVO customerAuthServiceVO=new CustomerAuthServiceVO();
        customerAuthServiceVO.setProviderTokenId(id);


        CustomerVO customerVO = new CustomerVO();
        customerVO.setCustomerId(Integer.parseInt(customerId));
        customerAuthServiceVO.setCustomer(customerVO);


        String json = gson.toJson(customerAuthServiceVO);

        Log.w("datajson",json);

        params.put("volley", json);
        connectionVO.setParams(params);
        VolleyUtils.makeJsonObjectRequest(this, connectionVO, new VolleyResponseListener() {
            @Override
            public void onError(String message) {
            }

            @Override
            public void onResponse(Object resp) throws JSONException {
                JSONObject response = (JSONObject) resp;
                CustomerVO respcustomervo = gson.fromJson(response.toString(), CustomerVO.class);



                if (!respcustomervo.getStatusCode().equals("200")) {
                    ArrayList error = (ArrayList) respcustomervo.getErrorMsgs();
                    StringBuilder sb = new StringBuilder();
                    for(int i=0; i<error.size(); i++){
                        sb.append(error.get(i)).append("\n");
                    }
                    Utility.alertDialog(Paynimo_HDFC.this,"Alert",sb.toString(),"Ok");
                } else {

                    String json = gson.toJson(respcustomervo);
                    Session.set_Data_Sharedprefence(Paynimo_HDFC.this,Session.CACHE_CUSTOMER,json);

                    Intent newIntent = new Intent(Paynimo_HDFC.this, Home.class);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(newIntent);

                }
            }
        });
    }

}

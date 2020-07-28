package com.uav.autodebit.CustomDialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.uav.autodebit.Activity.CheckMandateAndShowDialog;
import com.uav.autodebit.Activity.Enach_Mandate;
import com.uav.autodebit.Activity.Mobile_Prepaid_Recharge_Service;
import com.uav.autodebit.Interface.AlertSelectDialogClick;
import com.uav.autodebit.Interface.CallBackInterface;
import com.uav.autodebit.Interface.MandateAndRechargeInterface;
import com.uav.autodebit.Interface.VolleyResponse;
import com.uav.autodebit.constant.ApplicationConstant;
import com.uav.autodebit.exceptions.ExceptionsNotification;
import com.uav.autodebit.util.DialogInterface;
import com.uav.autodebit.util.Utility;
import com.uav.autodebit.vo.AuthServiceProviderVO;
import com.uav.autodebit.vo.CustomerAuthServiceVO;
import com.uav.autodebit.vo.OxigenTransactionVO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class BeforeRecharge {

    public static void beforeRechargeAddMandate(Context context , OxigenTransactionVO oxigenTransactionVOresp , MandateAndRechargeInterface mandateAndRechargeInterface){
        CheckMandateAndShowDialog.oxiServiceMandateCheck(context,oxigenTransactionVOresp.getServiceId(),oxigenTransactionVOresp.getProvider().getProviderId(),new VolleyResponse((VolleyResponse.OnSuccess)(mandatecheckresp)->{
            OxigenTransactionVO oxigenTransactionVO = (OxigenTransactionVO) mandatecheckresp;
            if(oxigenTransactionVO!=null){
                if(oxigenTransactionVO.getStatusCode().equals("ap102")) {
                    mandateAndRechargeInterface.onMandate(oxigenTransactionVO.getStatusCode());
                   /* if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                        startSIActivity(context,oxigenTransactionVOresp, ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                    }else if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                        ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                    }*/

                }else if(oxigenTransactionVO.getStatusCode().equals("ap103")){
                    String[] buttons = {" New ", " Existing "};
                    Utility.showDoubleButtonDialogConfirmation(new DialogInterface() {
                        @Override
                        public void confirm(Dialog dialog) {
                            dialog.dismiss();
                            createBankListInDialog(context,oxigenTransactionVOresp.getProvider().getProviderId(),oxigenTransactionVO,new CallBackInterface((CallBackInterface.OnSuccess)(onclick)->{
                                String bankId = (String) onclick;
                                if(!bankId.equals("0")){
                                    mandateAndRechargeInterface.onRecharge(bankId);
                                    //proceedToRecharge(oxigenTransactionVOresp.getTypeId().toString(), bankId, oxigenTransactionVOresp.getProvider().getProviderId(),false);
                                }else {

                                    mandateAndRechargeInterface.onMandate(oxigenTransactionVO.getStatusCode());
                                   /* if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                                        startSIActivity(context,oxigenTransactionVOresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                                    }else if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                                        ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                                    }*/
                                }
                            }));
                        }
                        @Override
                        public void modify(Dialog dialog) {
                            dialog.dismiss();
                            mandateAndRechargeInterface.onMandate(oxigenTransactionVO.getStatusCode());
                            /*if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.AUTOPE_PG){
                                startSIActivity(context,oxigenTransactionVOresp,ApplicationConstant.PG_MANDATE_AND_RECHARGE);
                            }else if(oxigenTransactionVOresp.getProvider().getProviderId()== AuthServiceProviderVO.ENACHIDFC){
                                ((Activity) context).startActivityForResult(new Intent(context, Enach_Mandate.class).putExtra("forresutl", true).putExtra("selectservice", new ArrayList<Integer>(Arrays.asList(oxigenTransactionVOresp.getServiceId()))), ApplicationConstant.REQ_MANDATE_FOR_FIRSTTIME_RECHARGE);
                            }*/
                        }
                    }, context, oxigenTransactionVO.getErrorMsgs().get(0), "", buttons);
                }
            }
        }));
    }


    public static void createBankListInDialog(Context context, Integer providerId, OxigenTransactionVO checkMandateResponse , CallBackInterface callBackInterface){
        try {
            JSONArray arryjson = new JSONArray(checkMandateResponse.getAnonymousString());
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
            Utility.alertselectdialog(context, providerId, customerAuthServiceArry, new AlertSelectDialogClick((AlertSelectDialogClick.OnSuccess) callBackInterface::onSuccess));
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionsNotification.ExceptionHandling(context , Utility.getStackTrace(e));
        }
    }
}

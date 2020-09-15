package com.uav.autodebit.vo;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.List;

public class BaseVO implements Serializable {

    private String statusCode;
    private List<String> errorMsgs;
    private String anonymousString;
    private String anonymousString1;
    private Integer otpExpiredMobile;
    private Integer otpExpiredEmail;
    private String actionname;
    private String image;
    private Integer anonymousInteger;
    private String localCache;
    private String enachDetails;
    private Integer serviceId;
    private Double serviceAdopteBMA;
    private boolean eventIs;
    private List<String> paymentType;
    private String htmlString;
    private Boolean paymentDialogShowMandate;
    private String customerDetails;

    private Double anonymousAmount;
    private Integer anonymounsDay;
    private Long  anonymousDate;
    private Integer custoemrHistoryId;

    private String customerBankName;
    private String customerBankAccountNo;
    private Boolean showDialog;
    private String dialogTitle;
    private Integer notificationId;
    private String appHash;
    private String paymentTypeObject;
    private String siMandateType;
    private String dialogMessage;

    private String siMandateHtml;
    private String bankMandateHtml;
    private String upiMandateHtml;
    private Integer anonymousInteger1;
    private String dmrcFeeCharges;


    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public List<String> getErrorMsgs() {
        return errorMsgs;
    }

    public void setErrorMsgs(List<String> errorMsgs) {
        this.errorMsgs = errorMsgs;
    }

    public String getAnonymousString() {
        return anonymousString;
    }

    public void setAnonymousString(String anonymousString) {
        this.anonymousString = anonymousString;
    }

    public Integer getOtpExpiredMobile() {
        return otpExpiredMobile;
    }

    public void setOtpExpiredMobile(Integer otpExpiredMobile) {
        this.otpExpiredMobile = otpExpiredMobile;
    }

    public Integer getOtpExpiredEmail() {
        return otpExpiredEmail;
    }

    public void setOtpExpiredEmail(Integer otpExpiredEmail) {
        this.otpExpiredEmail = otpExpiredEmail;
    }

    public String getActionname() {
        return actionname;
    }

    public void setActionname(String actionname) {
        this.actionname = actionname;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getAnonymousInteger() {
        return anonymousInteger;
    }

    public void setAnonymousInteger(Integer anonymousInteger) {
        this.anonymousInteger = anonymousInteger;
    }

    public String getLocalCache() {
        return localCache;
    }

    public void setLocalCache(String localCache) {
        this.localCache = localCache;
    }

    public String getEnachDetails() {
        return enachDetails;
    }

    public void setEnachDetails(String enachDetails) {
        this.enachDetails = enachDetails;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }


    public Double
    getServiceAdopteBMA() {
        return serviceAdopteBMA;
    }

    public void setServiceAdopteBMA(Double serviceAdopteBMA) {
        this.serviceAdopteBMA = serviceAdopteBMA;
    }

    public boolean isEventIs() {
        return eventIs;
    }

    public void setEventIs(boolean eventIs) {
        this.eventIs = eventIs;
    }

    public List<String> getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(List<String> paymentType) {
        this.paymentType = paymentType;
    }

    public String getHtmlString() {
        return htmlString;
    }

    public void setHtmlString(String htmlString) {
        this.htmlString = htmlString;
    }

    public Boolean getPaymentDialogShowMandate() {
        return paymentDialogShowMandate;
    }

    public void setPaymentDialogShowMandate(Boolean paymentDialogShowMandate) {
        this.paymentDialogShowMandate = paymentDialogShowMandate;
    }

    public String getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(String customerDetails) {
        this.customerDetails = customerDetails;
    }


    public Double getAnonymousAmount() {
        return anonymousAmount;
    }

    public void setAnonymousAmount(Double anonymousAmount) {
        this.anonymousAmount = anonymousAmount;
    }

    public Integer getAnonymounsDay() {
        return anonymounsDay;
    }

    public void setAnonymounsDay(Integer anonymounsDay) {
        this.anonymounsDay = anonymounsDay;
    }

    public Long getAnonymousDate() {
        return anonymousDate;
    }

    public void setAnonymousDate(Long anonymousDate) {
        this.anonymousDate = anonymousDate;
    }

    public Integer getCustoemrHistoryId() {
        return custoemrHistoryId;
    }

    public void setCustoemrHistoryId(Integer custoemrHistoryId) {
        this.custoemrHistoryId = custoemrHistoryId;
    }

    public String getCustomerBankName() {
        return customerBankName;
    }

    public void setCustomerBankName(String customerBankName) {
        this.customerBankName = customerBankName;
    }

    public String getCustomerBankAccountNo() {
        return customerBankAccountNo;
    }

    public void setCustomerBankAccountNo(String customerBankAccountNo) {
        this.customerBankAccountNo = customerBankAccountNo;
    }

    public Boolean getShowDialog() {
        return showDialog;
    }

    public void setShowDialog(Boolean showDialog) {
        this.showDialog = showDialog;
    }

    public String getDialogTitle() {
        return dialogTitle;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public Integer getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(Integer notificationId) {
        this.notificationId = notificationId;
    }

    public String getAppHash() {
        return appHash;
    }

    public void setAppHash(String appHash) {
        this.appHash = appHash;
    }


    public String getPaymentTypeObject() {
        return paymentTypeObject;
    }

    public void setPaymentTypeObject(String paymentTypeObject) {
        this.paymentTypeObject = paymentTypeObject;
    }

    public String getSiMandateType() {
        return siMandateType;
    }

    public void setSiMandateType(String siMandateType) {
        this.siMandateType = siMandateType;
    }

    public String getDialogMessage() {
        return dialogMessage;
    }

    public void setDialogMessage(String dialogMessage) {
        this.dialogMessage = dialogMessage;
    }

    public String getSiMandateHtml() {
        return siMandateHtml;
    }

    public void setSiMandateHtml(String siMandateHtml) {
        this.siMandateHtml = siMandateHtml;
    }

    public String getBankMandateHtml() {
        return bankMandateHtml;
    }

    public void setBankMandateHtml(String bankMandateHtml) {
        this.bankMandateHtml = bankMandateHtml;
    }

    public String getUpiMandateHtml() {
        return upiMandateHtml;
    }

    public void setUpiMandateHtml(String upiMandateHtml) {
        this.upiMandateHtml = upiMandateHtml;
    }

    public Integer getAnonymousInteger1() {
        return anonymousInteger1;
    }

    public void setAnonymousInteger1(Integer anonymousInteger1) {
        this.anonymousInteger1 = anonymousInteger1;
    }

    public String getAnonymousString1() {
        return anonymousString1;
    }

    public void setAnonymousString1(String anonymousString1) {
        this.anonymousString1 = anonymousString1;
    }

    public String getDmrcFeeCharges() {
        return dmrcFeeCharges;
    }

    public void setDmrcFeeCharges(String dmrcFeeCharges) {
        this.dmrcFeeCharges = dmrcFeeCharges;
    }


}

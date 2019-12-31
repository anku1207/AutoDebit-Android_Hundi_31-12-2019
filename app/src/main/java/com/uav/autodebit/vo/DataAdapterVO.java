package com.uav.autodebit.vo;

import org.json.JSONObject;

import java.io.Serializable;

public class DataAdapterVO implements Serializable {

    private String text;
    private String text2;
    private Integer image;
    private String imagename;
    private String associatedValue;
    private Class activityname;

    private String validity;
    private String imageUrl;




    private String quantity;
    private  String mop;
    private  String total;
    private  String productcode;
    private  String productname;
    private  String pcsid;
    private  JSONObject response;
    private  String producttype;
    private  Integer associativeCombo;


    private String passengerName;
    private String invoiceDate;
    private String posSaleId;
    private String questionsData;


    public DataAdapterVO(){

    }

    public DataAdapterVO(String text, String text2){
        this.text=text;
        this.text2=text2;
    }




    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }



    public String getAssociatedValue() {
        return associatedValue;
    }

    public void setAssociatedValue(String associatedValue) {
        this.associatedValue = associatedValue;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }


    public Class getActivityname() {
        return activityname;
    }

    public void setActivityname(Class activityname) {
        this.activityname = activityname;
    }


    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getMop() {
        return mop;
    }

    public void setMop(String mop) {
        this.mop = mop;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getProductcode() {
        return productcode;
    }

    public void setProductcode(String productcode) {
        this.productcode = productcode;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }


    public String getPcsid() {
        return pcsid;
    }

    public void setPcsid(String pcsid) {
        this.pcsid = pcsid;
    }

    public JSONObject getResponse() {
        return response;
    }

    public void setResponse(JSONObject response) {
        this.response = response;
    }


    public Integer getImage() {
        return image;
    }

    public void setImage(Integer image) {
        this.image = image;
    }

    public String getProducttype() {
        return producttype;
    }

    public void setProducttype(String producttype) {
        this.producttype = producttype;
    }

    public Integer getAssociativeCombo() {
        return associativeCombo;
    }

    public void setAssociativeCombo(Integer associativeCombo) {
        this.associativeCombo = associativeCombo;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getPosSaleId() {
        return posSaleId;
    }

    public void setPosSaleId(String posSaleId) {
        this.posSaleId = posSaleId;
    }

    public String getImagename() {
        return imagename;
    }

    public void setImagename(String imagename) {
        this.imagename = imagename;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getQuestionsData() {
        return questionsData;
    }

    public void setQuestionsData(String questionsData) {
        this.questionsData = questionsData;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}

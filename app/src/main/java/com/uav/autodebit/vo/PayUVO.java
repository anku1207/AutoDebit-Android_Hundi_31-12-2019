package com.uav.autodebit.vo;

import java.io.Serializable;

public class PayUVO extends BaseVO  implements Serializable {

    private Integer payuId;
    private CustomerVO customer;
    private PaymentGatewayVO paymentGateway;
    private CCTransactionStatusVO ccTransactionStatus;
    private String CardNo;
    private String operatorTxnID;
    private String transactionMsg;
    private Double amount;
    private String pgType  ;
    private String pgMode  ;
    private String pgBankRefNum  ;
    private String pgBankCode  ;
    private String pgAddedon ;
    private String key;
    private String pGPaymentID;
    private String productinfo;
    private String payUEncriptData;
    private String cardhash;
    private Integer payuMoneyId;
    private Double discount;
    private Double netAmountdDebit;

    public Integer getPayuId() {
        return payuId;
    }

    public void setPayuId(Integer payuId) {
        this.payuId = payuId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public PaymentGatewayVO getPaymentGateway() {
        return paymentGateway;
    }

    public void setPaymentGateway(PaymentGatewayVO paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public CCTransactionStatusVO getCcTransactionStatus() {
        return ccTransactionStatus;
    }

    public void setCcTransactionStatus(CCTransactionStatusVO ccTransactionStatus) {
        this.ccTransactionStatus = ccTransactionStatus;
    }

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public String getOperatorTxnID() {
        return operatorTxnID;
    }

    public void setOperatorTxnID(String operatorTxnID) {
        this.operatorTxnID = operatorTxnID;
    }

    public String getTransactionMsg() {
        return transactionMsg;
    }

    public void setTransactionMsg(String transactionMsg) {
        this.transactionMsg = transactionMsg;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPgType() {
        return pgType;
    }

    public void setPgType(String pgType) {
        this.pgType = pgType;
    }

    public String getPgMode() {
        return pgMode;
    }

    public void setPgMode(String pgMode) {
        this.pgMode = pgMode;
    }

    public String getPgBankRefNum() {
        return pgBankRefNum;
    }

    public void setPgBankRefNum(String pgBankRefNum) {
        this.pgBankRefNum = pgBankRefNum;
    }

    public String getPgBankCode() {
        return pgBankCode;
    }

    public void setPgBankCode(String pgBankCode) {
        this.pgBankCode = pgBankCode;
    }

    public String getPgAddedon() {
        return pgAddedon;
    }

    public void setPgAddedon(String pgAddedon) {
        this.pgAddedon = pgAddedon;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getpGPaymentID() {
        return pGPaymentID;
    }

    public void setpGPaymentID(String pGPaymentID) {
        this.pGPaymentID = pGPaymentID;
    }

    public String getProductinfo() {
        return productinfo;
    }

    public void setProductinfo(String productinfo) {
        this.productinfo = productinfo;
    }

    public String getPayUEncriptData() {
        return payUEncriptData;
    }

    public void setPayUEncriptData(String payUEncriptData) {
        this.payUEncriptData = payUEncriptData;
    }

    public String getCardhash() {
        return cardhash;
    }

    public void setCardhash(String cardhash) {
        this.cardhash = cardhash;
    }

    public Integer getPayuMoneyId() {
        return payuMoneyId;
    }

    public void setPayuMoneyId(Integer payuMoneyId) {
        this.payuMoneyId = payuMoneyId;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getNetAmountdDebit() {
        return netAmountdDebit;
    }

    public void setNetAmountdDebit(Double netAmountdDebit) {
        this.netAmountdDebit = netAmountdDebit;
    }
}

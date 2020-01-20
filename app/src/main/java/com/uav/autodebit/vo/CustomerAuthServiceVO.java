package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerAuthServiceVO extends BaseVO implements Serializable {
    private String providerTokenId;
    private CustomerVO customer;


    private Integer customerAuthId;
    private AuthStatusVO authStatus;
    private String accountNumber;
    private String  bankName;
    private String  accountHolderName;
    private Double mandateAmount;



    public CustomerAuthServiceVO() {
    }


    public String getProviderTokenId() {
        return providerTokenId;
    }

    public void setProviderTokenId(String providerTokenId) {
        this.providerTokenId = providerTokenId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }


    public Integer getCustomerAuthId() {
        return customerAuthId;
    }

    public void setCustomerAuthId(Integer customerAuthId) {
        this.customerAuthId = customerAuthId;
    }

    public AuthStatusVO getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(AuthStatusVO authStatus) {
        this.authStatus = authStatus;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountHolderName() {
        return accountHolderName;
    }

    public void setAccountHolderName(String accountHolderName) {
        this.accountHolderName = accountHolderName;
    }

    public Double getMandateAmount() {
        return mandateAmount;
    }

    public void setMandateAmount(Double mandateAmount) {
        this.mandateAmount = mandateAmount;
    }
}

package com.uav.autodebit.vo;

import java.io.Serializable;

public class CustomerBankVO implements Serializable {





    private Integer customerBankId;
    private Integer accountNumber;
    private String beneficialName;
    private String ifscCode;
    private Integer  verifiyBank;
    private ITZBankVO bank;
    private CustomerVO customer;

    public CustomerBankVO() {
    }


    public Integer getCustomerBankId() {
        return customerBankId;
    }

    public void setCustomerBankId(Integer customerBankId) {
        this.customerBankId = customerBankId;
    }

    public Integer getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(Integer accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBeneficialName() {
        return beneficialName;
    }

    public void setBeneficialName(String beneficialName) {
        this.beneficialName = beneficialName;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public Integer getVerifiyBank() {
        return verifiyBank;
    }

    public void setVerifiyBank(Integer verifiyBank) {
        this.verifiyBank = verifiyBank;
    }

    public ITZBankVO getBank() {
        return bank;
    }

    public void setBank(ITZBankVO bank) {
        this.bank = bank;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }
}




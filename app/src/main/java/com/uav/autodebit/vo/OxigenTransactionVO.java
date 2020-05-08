package com.uav.autodebit.vo;

import java.io.Serializable;

public class OxigenTransactionVO extends BaseVO  implements Serializable {


    private Integer typeId;
    private CustomerVO customer;
    private ServiceTypeVO serviceType;

    private String referenceName;
    private String referenceValue;
    private Double amount;
    private String stateRegion ;
    private String operateName;
    private AuthServiceProviderVO provider;
    private Integer providerAuthKeyId;
    private Double netAmount;

    private String successBankApporve;
    private String clinkingOnBankMandate;



    public OxigenTransactionVO() {
    }


    public Integer getTypeId() {
        return typeId;
    }

    public void setTypeId(Integer typeId) {
        this.typeId = typeId;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public ServiceTypeVO getServiceType() {
        return serviceType;
    }

    public void setServiceType(ServiceTypeVO serviceType) {
        this.serviceType = serviceType;
    }



    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public String getReferenceValue() {
        return referenceValue;
    }

    public void setReferenceValue(String referenceValue) {
        this.referenceValue = referenceValue;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getStateRegion() {
        return stateRegion;
    }

    public void setStateRegion(String stateRegion) {
        this.stateRegion = stateRegion;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public AuthServiceProviderVO getProvider() {
        return provider;
    }

    public void setProvider(AuthServiceProviderVO provider) {
        this.provider = provider;
    }

    public Integer getProviderAuthKeyId() {
        return providerAuthKeyId;
    }

    public void setProviderAuthKeyId(Integer providerAuthKeyId) {
        this.providerAuthKeyId = providerAuthKeyId;
    }

    public Double getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(Double netAmount) {
        this.netAmount = netAmount;
    }

    public String getSuccessBankApporve() {
        return successBankApporve;
    }

    public void setSuccessBankApporve(String successBankApporve) {
        this.successBankApporve = successBankApporve;
    }

    public String getClinkingOnBankMandate() {
        return clinkingOnBankMandate;
    }

    public void setClinkingOnBankMandate(String clinkingOnBankMandate) {
        this.clinkingOnBankMandate = clinkingOnBankMandate;
    }
}

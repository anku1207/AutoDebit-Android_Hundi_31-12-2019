package com.uav.autodebit.vo;

import java.io.Serializable;

public class ITZBankVO  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer bankId;
    private String bankCode;
    private String bankName;

    public ITZBankVO() {
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(Integer bankId) {
        this.bankId = bankId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
}

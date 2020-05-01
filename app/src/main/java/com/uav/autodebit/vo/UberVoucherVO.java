package com.uav.autodebit.vo;

import java.io.Serializable;

public class UberVoucherVO extends BaseVO implements Serializable {
    private Integer voucherId;
    private Integer  voucherNo;
    private String  voucherLinke;
    private CustomerVO customer ;
    private Long issueAt;
    private Long createdAt;
    private String createBy;
    private Long voucherExprieDate;
    private boolean showAddVoucherBtn;


    public Integer getVoucherId() {
        return voucherId;
    }

    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    public Integer getVoucherNo() {
        return voucherNo;
    }

    public void setVoucherNo(Integer voucherNo) {
        this.voucherNo = voucherNo;
    }

    public String getVoucherLinke() {
        return voucherLinke;
    }

    public void setVoucherLinke(String voucherLinke) {
        this.voucherLinke = voucherLinke;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public Long getIssueAt() {
        return issueAt;
    }

    public void setIssueAt(Long issueAt) {
        this.issueAt = issueAt;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Long createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Long getVoucherExprieDate() {
        return voucherExprieDate;
    }

    public void setVoucherExprieDate(Long voucherExprieDate) {
        this.voucherExprieDate = voucherExprieDate;
    }

    public boolean isShowAddVoucherBtn() {
        return showAddVoucherBtn;
    }

    public void setShowAddVoucherBtn(boolean showAddVoucherBtn) {
        this.showAddVoucherBtn = showAddVoucherBtn;
    }
}

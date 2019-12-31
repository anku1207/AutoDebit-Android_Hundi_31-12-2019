package com.uav.autodebit.vo;

import java.io.Serializable;
import java.util.List;

public class DMRC_Customer_CardVO extends BaseVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer dmrcid;
    private String mobileNumber ;
    private String customerName ;
    private String cardNo ;
    private String address;
    private CustomerVO customer;
    private StateVO stateRegion ;
    private CityVO city ;
    private DmrcCardStatusVO dmrccardStaus;
    private String pincode;
    private List<DMRC_Customer_CardVO> dmrcCustomerList;

    private Long issueDate;

    public DMRC_Customer_CardVO() {
    }

    public Integer getDmrcid() {
        return dmrcid;
    }

    public void setDmrcid(Integer dmrcid) {
        this.dmrcid = dmrcid;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public StateVO getStateRegion() {
        return stateRegion;
    }

    public void setStateRegion(StateVO stateRegion) {
        this.stateRegion = stateRegion;
    }

    public CityVO getCity() {
        return city;
    }

    public void setCity(CityVO city) {
        this.city = city;
    }

    public DmrcCardStatusVO getDmrccardStaus() {
        return dmrccardStaus;
    }

    public void setDmrccardStaus(DmrcCardStatusVO dmrccardStaus) {
        this.dmrccardStaus = dmrccardStaus;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public List<DMRC_Customer_CardVO> getDmrcCustomerList() {
        return dmrcCustomerList;
    }

    public void setDmrcCustomerList(List<DMRC_Customer_CardVO> dmrcCustomerList) {
        this.dmrcCustomerList = dmrcCustomerList;
    }

    public Long getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Long issueDate) {
        this.issueDate = issueDate;
    }
}

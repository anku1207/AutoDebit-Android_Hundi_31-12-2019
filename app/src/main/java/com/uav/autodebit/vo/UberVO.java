package com.uav.autodebit.vo;

import java.util.List;

public class UberVO extends BaseVO{

    private Integer uberId;
    private String  firstName;
    private String  lastName;
    private String email ;
    private Long emailVerified;
    private CustomerVO customer;
    private List<UberVO> uberCustomerList;


    public UberVO() {
    }

    public Integer getUberId() {
        return uberId;
    }

    public void setUberId(Integer uberId) {
        this.uberId = uberId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(Long emailVerified) {
        this.emailVerified = emailVerified;
    }

    public CustomerVO getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerVO customer) {
        this.customer = customer;
    }

    public List<UberVO> getUberCustomerList() {
        return uberCustomerList;
    }

    public void setUberCustomerList(List<UberVO> uberCustomerList) {
        this.uberCustomerList = uberCustomerList;
    }
}

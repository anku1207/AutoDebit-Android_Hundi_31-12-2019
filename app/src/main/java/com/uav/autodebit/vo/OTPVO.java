package com.uav.autodebit.vo;

import java.io.Serializable;

public class OTPVO extends BaseVO implements Serializable {

    public OTPVO(){

    }

    private String otp;
    private String mobileNo;
    private String emailId;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
}

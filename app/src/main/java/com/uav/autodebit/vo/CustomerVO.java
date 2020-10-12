package com.uav.autodebit.vo;

import android.util.Log;

import java.io.Serializable;
import java.util.Date;


public class CustomerVO extends BaseVO implements Serializable {

        private static final long serialVersionUID = 1L;


        private Integer customerId;
        private String timeOutMobile;
        private String timeOutEmail;
        private String userid;
        private String loginType;
        private String panNo;
        private Long dateOfBirth;
        private String address1;
        private String pincode;
        private String name;
        private CustomerStatusVO status;
        private String emailId;
        private String mobileNumber;
        private String password;
        private LevelVO level;
        private String panFirstName;
        private String panMiddleName;
        private String panLastName;
        private Integer creditScore;
        private StateVO stateRegion ;
        private CityVO city ;
        private Long emailVerified;
        private Long mobileVerified;
        private String panHolderName;
        private String cirMobileNumber;
        private String tokenId;
        private Double highestMandateAmount;
        private String installApps;
        private String cirPdfPath;






        public CustomerVO(){

        }

        public String getTimeOutMobile() {
                return timeOutMobile;
        }

        public void setTimeOutMobile(String timeOutMobile) {
                this.timeOutMobile = timeOutMobile;
        }

        public String getTimeOutEmail() {
                return timeOutEmail;
        }

        public void setTimeOutEmail(String timeOutEmail) {
                this.timeOutEmail = timeOutEmail;
        }

        public String getUserid() {
                return userid;
        }

        public void setUserid(String userid) {
                this.userid = userid;
        }

        public String getLoginType() {
                return loginType;
        }

        public void setLoginType(String loginType) {
                this.loginType = loginType;
        }

        public String getPanNo() {
                return panNo;
        }

        public void setPanNo(String panNo) {
                this.panNo = panNo;
        }

        public Long getDateOfBirth() {
                return dateOfBirth;
        }

        public void setDateOfBirth(Long dateOfBirth) {
                this.dateOfBirth = dateOfBirth;
        }

        public String getAddress1() {
                return address1;
        }

        public void setAddress1(String address1) {
                this.address1 = address1;
        }

        public String getPincode() {
                return pincode;
        }

        public void setPincode(String pincode) {
                this.pincode = pincode;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public CustomerStatusVO getStatus() {
                return status;
        }

        public void setStatus(CustomerStatusVO status) {
                this.status = status;
        }

        public Integer getCustomerId() {
                return customerId;
        }

        public void setCustomerId(Integer customerId) {
                this.customerId = customerId;
        }

        public String getEmailId() {
                return emailId;
        }

        public void setEmailId(String emailId) {
                this.emailId = emailId;
        }

        public String getMobileNumber() {
                return mobileNumber;
        }

        public void setMobileNumber(String mobileNumber) {
                this.mobileNumber = mobileNumber;
        }

        public String getPassword() {
                return password;
        }

        public void setPassword(String password) {
                this.password = password;
        }

        public LevelVO getLevel() {
                return level;
        }

        public void setLevel(LevelVO level) {
                this.level = level;
        }

        public String getPanFirstName() {
                return panFirstName;
        }

        public void setPanFirstName(String panFirstName) {
                this.panFirstName = panFirstName;
        }

        public String getPanMiddleName() {
                return panMiddleName;
        }

        public void setPanMiddleName(String panMiddleName) {
                this.panMiddleName = panMiddleName;
        }

        public String getPanLastName() {
                return panLastName;
        }

        public void setPanLastName(String panLastName) {
                this.panLastName = panLastName;
        }

        public Integer getCreditScore() {
                return creditScore;
        }

        public void setCreditScore(Integer creditScore) {
                this.creditScore = creditScore;
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

        public Long getEmailVerified() {
                return emailVerified;
        }

        public void setEmailVerified(Long emailVerified) {
                this.emailVerified = emailVerified;
        }

        public Long getMobileVerified() {
                return mobileVerified;
        }

        public void setMobileVerified(Long mobileVerified) {
                this.mobileVerified = mobileVerified;
        }

        public String getPanHolderName() {
                return panHolderName;
        }

        public void setPanHolderName(String panHolderName) {
                this.panHolderName = panHolderName;
        }

        public String getCirMobileNumber() {
                return cirMobileNumber;
        }

        public void setCirMobileNumber(String cirMobileNumber) {
                this.cirMobileNumber = cirMobileNumber;
        }

        public String getTokenId() {
                return tokenId;
        }

        public void setTokenId(String tokenId) {
                this.tokenId = tokenId;
        }

        public Double getHighestMandateAmount() {
                return highestMandateAmount;
        }

        public void setHighestMandateAmount(Double highestMandateAmount) {
                this.highestMandateAmount = highestMandateAmount;
        }

        public String getInstallApps() {
                return installApps;
        }

        public void setInstallApps(String installApps) {
                this.installApps = installApps;
        }

        public String getCirPdfPath() {
                return cirPdfPath;
        }

        public void setCirPdfPath(String cirPdfPath) {
                this.cirPdfPath = cirPdfPath;
        }
}

package com.galaxy.ishare.model;

import java.io.Serializable;

/**
 * Created by liuxiaoran on 15/4/24.
 */
public class User implements Serializable {

    private String gender;

    private String avatar;

    private String userName;

    private String userId;

    private String userPhone;


    private String key;

    private  UserLocation userLocation;

    // 个人信用的真实姓名
    private String realTime;

    // 个人信用的工作单位
    private String jobLocation;


    // 个人信用的个人照片链接
    private String personPicUrl;

    // 个人信用中的身份证链接
    private String idCardPicUrl;

    // 个人信用中的工牌链接
    private String jobCardUrl;

    public void setUserLocation(UserLocation  location){
        this.userLocation = location;
    }

    public UserLocation getUserLocation(){
        return userLocation;
    }

    public User (){

    }

    public String getGender (){
        return gender;
    }

    public void setGender(String gender){
        this.gender=gender;
    }
    public String getAvatar(){
        return avatar;
    }

    public void setAvatar(String avatar){
       this. avatar=avatar;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getRealTime() {
        return realTime;
    }

    public void setRealTime(String realTime) {
        this.realTime = realTime;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public String getPersonPicUrl() {
        return personPicUrl;
    }

    public void setPersonPicUrl(String personPicUrl) {
        this.personPicUrl = personPicUrl;
    }

    public String getIdCardPicUrl() {
        return idCardPicUrl;
    }

    public void setIdCardPicUrl(String idCardPicUrl) {
        this.idCardPicUrl = idCardPicUrl;
    }

    public String getJobCardUrl() {
        return jobCardUrl;
    }

    public void setJobCardUrl(String jobCardUrl) {
        this.jobCardUrl = jobCardUrl;
    }


    public static class UserLocation implements Serializable{

        private String district;
        private String province;
        private String city;
        private String locationStr;
        private double longitude;
        private double latitude;

        public double getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = longitude;
        }

        public double getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = latitude;
        }



        public UserLocation(String city,String province,String district,String location,double longitude,double latitude ){
            this.city=city;
            this.province=province;
            this.district=district;
            this.locationStr=location;
            this.longitude=longitude;
            this.latitude=latitude;
        }

        public String getLocationStr() {
            return locationStr;
        }

        public void setLocationStr(String locationStr) {
            this.locationStr = locationStr;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }


        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }



    }
}

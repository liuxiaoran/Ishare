package com.galaxy.ishare.model;

import java.io.Serializable;

/**
 * Created by liuxiaoran on 15/4/24.
 */
public class User implements Serializable {

    private String userName;

    private String userId;

    private String userPhone;


    private String key;


    public User (){

    }
    public User (String phone , String key){
        this.userPhone = phone;
        this.key=key;

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


    public static class UserLocation {

        private String district;
        private String province;
        private String city;
        private String locationStr;
        private String longitude;
        private String latitude;

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }



        public UserLocation(String city,String province,String district,String location,String longitude,String latitude ){
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

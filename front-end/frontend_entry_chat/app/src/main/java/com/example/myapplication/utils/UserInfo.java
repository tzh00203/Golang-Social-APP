package com.example.myapplication.utils;

public class UserInfo {

    //用户纬度
    private Double location_latitude;
    private Double location_longitude;

    public UserInfo(Double lat, Double lon ){
        this.location_latitude = lat;
        this.location_longitude = lon;
    }

    public Double getLocation_longitude() {
        return location_longitude;
    }
    public Double getLocation_latitude() {
        return location_latitude;
    }
    public void setLocation(Double lat, Double lon ){
        this.location_latitude = lat;
        this.location_longitude = lon;
    }
}

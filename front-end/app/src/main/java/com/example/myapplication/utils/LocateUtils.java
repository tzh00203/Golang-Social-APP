package com.example.myapplication.utils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;

public class LocateUtils {

    public static float getDistance(AMapLocation aMapLocationA, AMapLocation aMapLocationB){
        LatLng pointA = new LatLng(aMapLocationA.getLatitude(), aMapLocationA.getLongitude());
        LatLng pointB = new LatLng(aMapLocationB.getLatitude(), aMapLocationB.getLongitude());
        return AMapUtils.calculateLineDistance(pointA, pointB);
    }
}

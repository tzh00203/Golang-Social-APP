package com.example.myapplication.utils;



import static com.example.myapplication.entry.activity.MainActivity.userInfo;
import static com.example.myapplication.utils.LocateUtils.getDistance;

import android.util.Log;

import com.amap.api.location.AMapLocation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;


public class UserInfo {

    //用户纬度
    public static String username_inFront;
    private Double location_latitude;
    private Double location_longitude;
    public  static HashMap<String, Float> all_user_location;
    public static double[] sharedArray_long = {116.252426, 116.252427, 116.252428};
    public static double[] sharedArray_lat = {39.939290, 39.939280, 39.939299};
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
        all_user_location = new HashMap<>();

    }

    public void UpdateAllLocation2User(String jsonString){
        try {
            // 将字符串解析为JSON对象
            JSONObject json = new JSONObject(jsonString);

            // 访问JSON对象的data属性，是列表,列表每个元素是三个键值对的字典
            JSONArray dataArray = json.getJSONArray("data");

            // 遍历计算每个用户到自己的距离，如果小于1km，就存储
            for (int i = 0; i < dataArray.length(); i++) {
                JSONObject value = dataArray.getJSONObject(i);
                String name_tmp = value.getString("name");
                double lon_tmp = Double.parseDouble(value.getString("location_longitude"));
                double lat_tmp = Double.parseDouble(value.getString("location_latitude"));

                if(name_tmp.equals(username_inFront)) continue;
                Log.e("本地用户名和获得的用户名分别为", name_tmp + " " +username_inFront);
                AMapLocation aMapLocationa = new AMapLocation("");
                aMapLocationa.setLatitude(lat_tmp);
                aMapLocationa.setLongitude(lon_tmp);

                AMapLocation aMapLocationb = new AMapLocation("");
                aMapLocationb.setLatitude(userInfo.getLocation_latitude());
                aMapLocationb.setLongitude(userInfo.getLocation_longitude());

                Float distance_tmp = getDistance(aMapLocationb, aMapLocationa);
                String dis_str = distance_tmp.toString();
                Log.e("计算用户"+name_tmp+"到本地用户的距离为：", dis_str);
                if(distance_tmp<1000){
                    all_user_location.put(name_tmp, distance_tmp);
                    Log.e("加入本地用户的定位表中，username：", name_tmp);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}

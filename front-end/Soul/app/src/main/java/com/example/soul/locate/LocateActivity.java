package com.example.soul.locate;

import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.soul.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LocateActivity extends AppCompatActivity {

    private LocationManager lm;
    private TextView tv_show;
    public static List list = new ArrayList();
    public static ArrayAdapter<List> adapter;

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locate);

        tv_show = findViewById(R.id.tv_show);

        //LocationManager类提供访问系统位置服务的访问。这些服务允许应用程序获得定期更新设备的地理位置
        lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!isGpsAble(lm)) {
            Toast.makeText(LocateActivity.this, "请打开GPS", Toast.LENGTH_SHORT).show();
            openGPS2();
        }
        if (ContextCompat.checkSelfPermission(LocateActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {//未开启定位权限
            //开启定位权限,200是标识码
            ActivityCompat.requestPermissions(LocateActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 200);
        } else {
            startLocation();
            Toast.makeText(LocateActivity.this, "已开启定位权限", Toast.LENGTH_LONG).show();
        }

        Button btn1 = findViewById(R.id.button1);
        //list格式的listview
        ListView lv1 = findViewById(R.id.lv1);
        //list.add("获取数据");

        adapter=new ArrayAdapter<List>(this,android.R.layout.simple_expandable_list_item_1,list){
            @Override
            public View getView(int position, View convertView, @NotNull ViewGroup parent) {//重载该方法，在这个方法中，将每个Item的Gravity设置为CENTER
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextSize(40);//修改listview中数据字体的大小
                return textView;
            }
        };

        lv1.setAdapter(adapter);
        //点击listview事件
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(LocateActivity.this, "选择了"+list.get(position),Toast.LENGTH_SHORT).show();

                //list.remove(position);
                adapter.notifyDataSetChanged();
            }
        });


        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.clear();
                for (int i=0;i<10;i++){
                    list.add("数据"+i);
                }
                adapter.notifyDataSetChanged();
                Toast.makeText(LocateActivity.this, "我是按钮", Toast.LENGTH_LONG).show();
                //配置ArrayAdapter适配器

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 200) {//刚才的识别码
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意权限,执行我们的操作
                startLocation();//开始定位
            } else {//用户拒绝之后,当然我们也可以弹出一个窗口,直接跳转到系统设置页面
                Toast.makeText(LocateActivity.this, "未开启定位权限,请手动到设置去开启权限", Toast.LENGTH_LONG).show();
            }
        }
    }
    //定义一个更新显示的方法
    private void updateShow(Location location) {
        if (location != null) {
            StringBuilder sb = new StringBuilder();
            sb.append("当前的位置信息：\n");
            double a = location.getLongitude();
            if(a<=0){
                a = location.getLongitude()*-1;
            }
            sb.append("经度：" +  a + "\n");//显示正数
            sb.append("纬度：" + location.getLatitude() + "\n");
            sb.append("高度：" + location.getAltitude() + "\n");
            sb.append("速度：" + location.getSpeed() + "\n");
            sb.append("方向：" + location.getBearing() + "\n");
            sb.append("定位精度：" + location.getAccuracy() + "\n");
            tv_show.setText(sb.toString());
        } else{
            tv_show.setText("");
        }
    }

    @SuppressLint("MissingPermission")
    private void startLocation(){
        //从GPS获取最近的定位信息
        Location lc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        updateShow(lc);
        //设置间隔两秒获得一次GPS定位信息
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 8, new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // 当GPS定位信息发生改变时，更新定位
                updateShow(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                // 当GPS LocationProvider可用时，更新定位
                updateShow(lm.getLastKnownLocation(provider));
            }

            @Override
            public void onProviderDisabled(@NonNull String provider) {
                updateShow(null);
            }
        });
    }

    private boolean isGpsAble(LocationManager lm) {
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    //打开设置页面让用户自己设置
    private void openGPS2() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, 0);
    }

}

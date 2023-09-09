package com.example.myapplication.entry.activity;

import static com.example.myapplication.entry.activity.MainActivity.userInfo;
import static com.example.myapplication.utils.ServerInfo.ServerLocationURI;
import static com.example.myapplication.utils.ServerInfo.ServerMatchURI;
import static com.example.myapplication.utils.UserInfo.all_user_location;
import static com.example.myapplication.utils.UserInfo.username_inFront;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.R;
import com.example.myapplication.entry.adapter_datastruct.ChatListAdapter;
import com.example.myapplication.entry.adapter_datastruct.ChatListItem;
import com.example.myapplication.entry.adapter_datastruct.MatchItem;
import com.example.myapplication.entry.adapter_datastruct.MatchAdapter;
import com.example.myapplication.entry.adapter_datastruct.MatchListAdapterNew;
import com.example.myapplication.entry.adapter_datastruct.MatchListItemNew;
import com.example.myapplication.utils.UserInfo;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MatchListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MatchListAdapterNew adapter;

    private OkHttpClient client;
    private ImageButton matchReturn;
    String senderUsername;  //都在onCreate里面定义值
    String receiverUsername;
    List<MatchListItemNew> matchListItems = new ArrayList<>();
    int distance = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matchlist_new);
        client = new OkHttpClient();

        //获取传递的Intent
        Intent intent = getIntent();

        //从Intent中获取用户名
        senderUsername = intent.getStringExtra("sender_username");


        recyclerView = findViewById(R.id.match_list_recycler_view_new); //使用RecyclerView的ID
        matchReturn = findViewById(R.id.backButtonMatch);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        try {
            matchListItems = createMatchList();  //chatListItems是数据源
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        adapter = new MatchListAdapterNew(this, matchListItems);

        //设置聊天对象的点击事件监听器，点击后跳转到聊天界面
        adapter.setOnItemClickListener(new MatchListAdapterNew.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //获取被点击的聊天列表项的数据
                MatchListItemNew clickedItem = matchListItems.get(position);
                receiverUsername = clickedItem.getMatchPartner();
                // 显示一个消息提示谁被点击了
                String message = "匹配对象" + receiverUsername + "被点击了";
                Toast.makeText(MatchListActivity.this, message, Toast.LENGTH_SHORT).show();


            }
        });

        recyclerView.setAdapter(adapter);

        matchReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MatchListActivity.this, ChatListActivity.class);
                startActivity(intent);   //切换到MainActivity
            }

        });


    }

    //其他生命周期方法，先省略

    private List<MatchListItemNew> createMatchList() throws InterruptedException {

        //添加匹配列表项数据，示例：

        initMatchItemList();
        return matchListItems;
    }


    private void getAllUserLocation() throws InterruptedException {

        Log.e("开始向服务器发起get请求，获取所有用户经纬度", "666");
        MediaType contentType = MediaType.get("application/json; charset=utf-8");
        JSONObject jsonRequestBody = new JSONObject();
        RequestBody requestBody = RequestBody.create(contentType, jsonRequestBody.toString());
        Request request = new Request.Builder()
                .url(ServerLocationURI)
                .post(requestBody)   //指定用post
                .build();

        Thread childThread = new Thread(() -> {
            // 子线程执行一些任务
            Call call=client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MatchListActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                        }
                    });

                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    String result = response.body().string();
                    if(response.request().body() != null) {
                        Log.e("获取经纬度返回值：", result);
                        userInfo.UpdateAllLocation2User(result);
                    }
                    else {
                        Log.e("--------获取的经纬度信息为空", "--------------------");
                    }

                    //服务器响应成功
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MatchListActivity.this, "获取经纬度数据成功", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            });
        });

        childThread.start(); // 启动子线程
        childThread.join();  // 等待子线程执行完毕


    }

    private void initMatchItemList() throws InterruptedException {

        Log.e("进入定位信息渲染环节----", "----------------------------");


        getAllUserLocation();

        while (all_user_location.isEmpty()) {
            System.out.println("哈希表为空，等待元素添加...");
            Thread.sleep(1000); // 等待1秒
        }
        Iterator<Map.Entry<String, Float>> iterator = all_user_location.entrySet().iterator();

        while (iterator.hasNext()) {

            Map.Entry<String, Float> entry = iterator.next();
            String username_tmp = entry.getKey();
            Float distance_tmp = entry.getValue();
            Log.e("将用户定位信息渲染到UI上，username：", username_tmp);
            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            // 使用format方法将浮点数格式化为字符串
            String formattedNumber = decimalFormat.format(distance_tmp);

            int icon_tmp;
            if(Objects.equals(username_tmp, "tzh")){
                icon_tmp = R.drawable.tzh_icon;
            } else if (Objects.equals(username_tmp, "szj")) {
                icon_tmp = R.drawable.szj_icon;
            }
            else{
                icon_tmp = R.drawable.xqh_icon;
            }

            matchListItems.add(new MatchListItemNew(icon_tmp, username_tmp+"现在距离您" + formattedNumber + "m", username_tmp));

        }

//        matchItemList.add(new MatchItem(R.drawable.default_icon, "balabala"));
    }
}
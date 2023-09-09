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

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    private OkHttpClient client;
    String senderUsername;  //都在onCreate里面定义值
    String receiverUsername;
    List<ChatListItem> chatListItems = new ArrayList<>();
    List<MatchItem> matchItemLists = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);
        client = new OkHttpClient();

        //获取传递的Intent
        Intent intent = getIntent();

        //从Intent中获取用户名
        senderUsername = intent.getStringExtra("username");
        username_inFront = senderUsername;

        recyclerView = findViewById(R.id.chatlist_recycler_view); //使用RecyclerView的ID
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        chatListItems = createChatList();  //chatListItems是数据源
        adapter = new ChatListAdapter(this, chatListItems);

        //设置聊天对象的点击事件监听器，点击后跳转到聊天界面
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //获取被点击的聊天列表项的数据
                ChatListItem clickedItem = chatListItems.get(position);
                receiverUsername = clickedItem.getChatPartner();
                // 显示一个消息提示谁被点击了
                String message = "聊天对象" + receiverUsername + "被点击了";
                Toast.makeText(ChatListActivity.this, message, Toast.LENGTH_SHORT).show();

                //切换到 ChatActivity_UI，并传递用户名
                Intent intent = new Intent(ChatListActivity.this, com.example.myapplication.chat.activity.ChatActivity_UI.class);
                intent.putExtra("sender_username", senderUsername);
                intent.putExtra("receiver_username", receiverUsername);
                startActivity(intent);
            }
        });



        recyclerView.setAdapter(adapter);

        //创建和初始化数据源 matchItemLists
        try {
            matchItemLists = createMatchList();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }



        Button matchingButton = findViewById(R.id.matchingButton);
        matchingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = new Dialog(ChatListActivity.this);
                //设置 Dialog 使用的布局
                dialog.setContentView(R.layout.activity_match);

                //找到弹窗中的RecyclerView
                RecyclerView matchRecyclerView = dialog.findViewById(R.id.matchRecyclerView);
                LinearLayoutManager layoutManager = new LinearLayoutManager(ChatListActivity.this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL); //设置垂直方向
                matchRecyclerView.setLayoutManager(layoutManager);

                //使用 MatchAdapter适配器，将匹配项列表绑定到RecyclerView中
                MatchAdapter matchAdapter = new MatchAdapter(ChatListActivity.this, matchItemLists, new MatchAdapter.OnMatchButtonClickListener() {
                    @Override
                    public void onMatchButtonClick(String senderUsername, String receiverUsername) {
                        // 在这里处理匹配按钮的点击事件，可以调用发送请求的方法或执行其他操作
                        sendmatchRequest(senderUsername, receiverUsername);
                    }
                });
                matchRecyclerView.setAdapter(matchAdapter);


                Button matchReturnButton = dialog.findViewById(R.id.matchReturnButton);
                matchReturnButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss(); //关闭弹窗
                    }
                });

                //显示弹窗
                dialog.show();
            }
        });

    }

    //其他生命周期方法，先省略

    private List<ChatListItem> createChatList() {

        //添加聊天列表项数据，示例：
        chatListItems.add(new ChatListItem(senderUsername, "示例用户1", com.example.myapplication.R.drawable.default_icon));
        chatListItems.add(new ChatListItem(senderUsername, "示例用户2", com.example.myapplication.R.drawable.default_icon));
//        chatListItems.add(new ChatListItem(senderUsername, "tzh", com.example.myapplication.R.drawable.tzh_icon));
//        chatListItems.add(new ChatListItem(senderUsername, "szj", com.example.myapplication.R.drawable.szj_icon));
        return chatListItems;
    }


    private void sendmatchRequest(String senderUsername, String receiverUsername) {
        Log.e("--------开始发起匹配请求", "请求者为"+senderUsername+", 接收者为"+receiverUsername);

        MediaType contentType = MediaType.get("application/json; charset=utf-8");
        Map<String, String> params = new HashMap<>();
        params.put("senderUsername", senderUsername);
        params.put("receiverUsername", receiverUsername);
        String jsonRequestBody = JSON.toJSONString(params);  //请求体
        RequestBody requestBody = RequestBody.create(contentType, jsonRequestBody);
        String requestBodyString = requestBody.toString();
        Log.e("请求体内容：", requestBodyString);

        Request request = new Request.Builder()
                .url(ServerMatchURI)      //这是自己主机的ipv4地址。
                .post(requestBody)   //指定用POST方法
                .build();

//      Android网络请求需要在子线程里进行，不能在主线程  Thread是线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call = client.newCall(request);   //发送请求
                boolean waitingForResponse = true;

                while (waitingForResponse) {
                    try {
                        Response response = call.execute();
                        int statusCode = response.code();

                        if (statusCode == 200) {
                            //匹配成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChatListActivity.this, "匹配成功", Toast.LENGTH_SHORT).show();
                                    //匹配成功后添加项到聊天记录列表

                                    int icon_tmp;
                                    if(Objects.equals(receiverUsername, "tzh")){
                                        icon_tmp = R.drawable.tzh_icon;
                                    } else if (Objects.equals(receiverUsername, "szj")) {
                                        icon_tmp = R.drawable.szj_icon;
                                    }
                                    else{
                                        icon_tmp = R.drawable.xqh_icon;
                                    }

                                    chatListItems.add(new ChatListItem(senderUsername, receiverUsername, icon_tmp));
                                    adapter.notifyDataSetChanged();
                                }
                            });
                            waitingForResponse = false; //结束等待
                        } else if (statusCode == 234) {
                            //等待匹配
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(ChatListActivity.this, "等待匹配", Toast.LENGTH_SHORT).show();
                                }
                            });

                            //暂停一段时间，然后再次发起请求
                            try {
                                Thread.sleep(5000); //休眠5秒钟，再次发起请求
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // 其他状态码处理
                            waitingForResponse = false; // 结束等待
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();


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
                            Toast.makeText(ChatListActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(ChatListActivity.this, "获取经纬度数据成功", Toast.LENGTH_SHORT).show();

                        }
                    });

                }
            });
        });

        childThread.start(); // 启动子线程
        childThread.join();  // 等待子线程执行完毕


    }

    private List<MatchItem> createMatchList() throws InterruptedException {

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


            matchItemLists.add(new MatchItem(icon_tmp, username_tmp+"现在距离您" + formattedNumber + "m", senderUsername, username_tmp));
        }

       return matchItemLists;

//        matchItemList.add(new MatchItem(R.drawable.default_icon, "balabala"));
    }
}
package com.example.myapplication.entry.activity;

import static com.example.myapplication.entry.activity.MainActivity.userInfo;
import static com.example.myapplication.utils.ServerInfo.ServerLoginURI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.example.myapplication.R;
import com.example.myapplication.entry.adapter.ChatListAdapter;
import com.example.myapplication.entry.adapter.ChatListItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ChatListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatlist);

        //获取传递的Intent
        Intent intent = getIntent();

        //从Intent中获取用户名
        String username = intent.getStringExtra("username");

        recyclerView = findViewById(R.id.chatlist_recycler_view); // 使用RecyclerView的ID
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        List<ChatListItem> chatListItems = createChatList();  //chatListItems是数据源
        adapter = new ChatListAdapter(this, chatListItems);

        //设置点击事件监听器
        adapter.setOnItemClickListener(new ChatListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                //获取被点击的聊天列表项的数据
                ChatListItem clickedItem = chatListItems.get(position);
                // 获取发送者和接收者的用户名
                String senderUsername = clickedItem.getUsername();
                String receiverUsername = clickedItem.getChatPartner();
                // 切换到 ChatActivity_UI，并传递用户名
                Intent intent = new Intent(ChatListActivity.this, com.example.myapplication.chat.activity.ChatActivity_UI.class);
                intent.putExtra("sender_username", senderUsername);
                intent.putExtra("receiver_username", receiverUsername);
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        //在聊天列表界面处理匹配操作
        Button buttonMatch = findViewById(R.id.matching);
        //点击匹配按钮后，首先从服务器扒所有用户的经纬信息;
        buttonMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendGetLocateRequest()
            }

        });
    }

    //其他生命周期方法，先省略

    private List<ChatListItem> createChatList() {
        List<ChatListItem> chatListItems = new ArrayList<>();
        //添加聊天列表项数据，示例：
        chatListItems.add(new ChatListItem("szj","tzh" ,com.example.myapplication.R.drawable.szj_icon));
        chatListItems.add(new ChatListItem("tzh","szj" ,com.example.myapplication.R.drawable.xqh_icon));
        //添加更多聊天列表项
        return chatListItems;
    }


    private void sendLoginRequest(String username, String password) {
        MediaType contentType = MediaType.get("application/json; charset=utf-8");
//        String jsonRequestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";  //请求体
        Map<String,String> params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        // 经纬度double类型
        params.put("location_latitude", userInfo.getLocation_latitude().toString());
        params.put("location_longitude", userInfo.getLocation_longitude().toString());
        String jsonRequestBody = JSON.toJSONString(params);  //请求体
        RequestBody requestBody = RequestBody.create(contentType, jsonRequestBody);
        String requestBodyString = requestBody.toString();
        Log.e("请求体内容：", requestBodyString);


        Request request = new Request.Builder()
                .url(ServerLoginURI)      //这是自己主机的ipv4地址。
                .post(requestBody)   //指定用POST方法
                .build();

//        Android网络请求需要在子线程里进行，不能在主线程  Thread是线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call=client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {   //服务器没有响应
                                Toast.makeText(LoginActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String result=response.request().body().toString();
                        Log.e("登录返回值：",result);
                        int statusCode = response.code(); // 获取响应的状态码

                        //服务器响应成功
                        if (statusCode == 200) { //如果返回状态值是200说明登录成功
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                    // 登录成功后的操作
                                    Intent intent = new Intent(LoginActivity.this, ChatListActivity.class);
                                    intent.putExtra("username", username);
                                    startActivity(intent);   //切换到ChatListActivity
                                }
                            });
                        } else {  //登录失败
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "用户名或密码不正确", Toast.LENGTH_SHORT).show();

                                }
                            });
                        }

                    }
                });
            }
        }).start();
    }

    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatListActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

}

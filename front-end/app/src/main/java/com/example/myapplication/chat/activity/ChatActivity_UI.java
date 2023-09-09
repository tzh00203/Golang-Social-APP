package com.example.myapplication.chat.activity;

import static com.example.myapplication.utils.ServerInfo.ServerWSURI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.example.myapplication.R;
import com.example.myapplication.chat.adapter.MsgAdapter;
import com.example.myapplication.chat.data_struct.Msg;
import com.google.android.material.snackbar.Snackbar;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity_UI extends AppCompatActivity {

    private List<Msg> mMsgList = new ArrayList<>();
    private EditText inputText;
    private RecyclerView mRecyclerView;
    private MsgAdapter mAdapter;
    private ImageButton backButton;
    private Map<String, Integer> userAvatarMap = new HashMap<>(); //创建用户和头像的映射
    private WebSocketClient webSocketClient;    //WebSocket客户端
    private String senderUsername;
    private String receiverUsername;
    private int senderIconResId;
    private int receiverIconResId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bg);

    //初始化用户头像映射
        userAvatarMap.put("szj", R.drawable.szj_icon);
        userAvatarMap.put("xqh", R.drawable.xqh_icon);
        userAvatarMap.put("tzh", R.drawable.tzh_icon);

        //获取上一个活动传递过来的senderUsername和receiverUsername
        Intent intent = getIntent();
        senderUsername = intent.getStringExtra("sender_username");
        receiverUsername = intent.getStringExtra("receiver_username");
        //senderUsername = "tzh";
        //receiverUsername = "szj";

        // 根据senderUsername和receiverUsername确定用户图标资源 ID
        senderIconResId = userAvatarMap.containsKey(senderUsername)
                ? userAvatarMap.get(senderUsername)
                : R.drawable.default_icon;

        receiverIconResId = userAvatarMap.containsKey(receiverUsername)
                ? userAvatarMap.get(receiverUsername)
                : R.drawable.default_icon;


        //初始化WebSocket客户端
        try {
            URI serverURI = new URI(ServerWSURI);   //服务器地址
            webSocketClient = new WebSocketClient(serverURI) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    //WebSocket连接已打开
                    logMessage("WebSocket连接已打开");
                }

                @Override
                public void onMessage(String message) {
                    //收到服务器发送的消息
                    logMessage("收到服务器消息: " + message);
                    handleMessage(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    //WebSocket连接关闭
                    logMessage("WebSocket连接关闭，代码: " + code + ", 原因: " + reason + ", remote: " + remote);
                }

                @Override
                public void onError(Exception ex) {
                    //发生错误
                    logMessage("WebSocket错误: " + ex.getMessage());
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        //连接WebSocket服务器
        if (webSocketClient != null) {
            webSocketClient.connect();
        }

        //初始化界面元素
        inputText = findViewById(R.id.input_text);
        mRecyclerView = findViewById(R.id.meg_recycler_view);
        backButton = findViewById(R.id.backButton);

        TextView Talk2 = findViewById(R.id.topLabelChatList);

        Talk2.setText(receiverUsername);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgAdapter(mMsgList);
        mRecyclerView.setAdapter(mAdapter);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回按钮点击事件，处理返回逻辑
                finish();
            }
        });
    }

    //处理接收到的消息
    private void handleMessage(String message) {
        try {
            //解析服务器发送的JSON消息
            JSONObject jsonMessage = new JSONObject(message);
            String content = jsonMessage.getString("content");

            logMessage("收到消息的senderUsername为"+senderUsername);
            //根据发送者来设置消息类型
            int type = senderUsername.equals(receiverUsername) ? Msg.TYPE_SENT : Msg.TYPE_RECEIVED;

            //创建Msg对象并添加到消息列表
            Msg msg = new Msg(content, type, senderUsername, senderIconResId, receiverIconResId);
            mMsgList.add(msg);

            //刷新RecyclerView中的显示
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mAdapter.notifyItemInserted(mMsgList.size() - 1);
                    mRecyclerView.scrollToPosition(mMsgList.size() - 1);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //发送消息按钮点击事件
    public void sendMsg(View view) {
        String content = inputText.getText().toString().trim();
        logMessage("发送函数里的senderUsername为"+senderUsername);
        if (!content.isEmpty()) {
            if (webSocketClient != null && webSocketClient.isOpen()) {
                //构造包含 senderUsername和receiverUsername的JSON数据
                JSONObject messageData = new JSONObject();
                try {
                    messageData.put("senderUsername", senderUsername);
                    messageData.put("receiverUsername", receiverUsername);
                    messageData.put("content", content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //发送 JSON 数据给服务器
                webSocketClient.send(messageData.toString());
            } else {
                //WebSocket连接未打开，显示错误提示
                Snackbar.make(view, "WebSocket未连接", Snackbar.LENGTH_SHORT).show();
            }

            //将消息添加到本地消息列表
            Msg msg = new Msg(content, Msg.TYPE_SENT, senderUsername, senderIconResId, receiverIconResId);
            mMsgList.add(msg);

            mAdapter.notifyItemInserted(mMsgList.size() - 1);
            mRecyclerView.scrollToPosition(mMsgList.size() - 1);

            //清空输入框
            inputText.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //断开WebSocket连接
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    //用于日志输出
    private void logMessage(String message) {
        // 在这里添加日志输出逻辑，例如使用Log.d()方法
        Log.d("聊天日志", message);
    }
}

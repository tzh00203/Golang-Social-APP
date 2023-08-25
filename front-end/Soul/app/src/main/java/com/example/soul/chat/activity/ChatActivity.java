package com.example.soul.chat.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.soul.R;
import com.example.soul.chat.adapter.MsgAdapter;
import com.example.soul.chat.data.Msg;
import com.example.soul.registerNlogin.Entry2Soul;
import com.example.soul.registerNlogin.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private List<Msg> mMsgList = new ArrayList<>();

    private EditText inputText;

    private RecyclerView mRecyclerView;

    private MsgAdapter mAdapter;

    // 点击返回按钮
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_bg);

        //初始化数据
        initMsg();
        inputText = findViewById(R.id.input_text);
        mRecyclerView = findViewById(R.id.meg_recycler_view);
        backButton = findViewById(R.id.backButton);

        //登录按钮的点击事件
        backButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //切换到Login活动
               Intent intent = new Intent(ChatActivity.this, Entry2Soul.class);
               startActivity(intent);
           }
       }
        );

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new MsgAdapter(mMsgList);
        mRecyclerView.setAdapter(mAdapter);

        //将RecyclerView定位到最后一行
        mRecyclerView.scrollToPosition(mMsgList.size() - 1);


    }

    private void initMsg() {
        mMsgList.add(new Msg("这是一个测试的聊天框",Msg.TYPE_RECEIVED));

        mMsgList.add(new Msg("检查对话框加载是否正常",Msg.TYPE_SENT));

        mMsgList.add(new Msg("TODO: 气泡的颜色还需要修改",Msg.TYPE_RECEIVED));

        mMsgList.add(new Msg("试试加载一下长字符串====================================================================",Msg.TYPE_SENT));
        mMsgList.add(new Msg("可以吗",Msg.TYPE_RECEIVED));
        mMsgList.add(new Msg("还需要加载聊天双方的头像",Msg.TYPE_SENT));
        mMsgList.add(new Msg("try some English words",Msg.TYPE_RECEIVED));
        mMsgList.add(new Msg("asasassssssssssscsssssssssssssssssssssgrggggggggggggggg",Msg.TYPE_SENT));
        mMsgList.add(new Msg("1112334444444444444444444444444",Msg.TYPE_RECEIVED));
        mMsgList.add(new Msg("可以有滚轮吗",Msg.TYPE_SENT));
        mMsgList.add(new Msg("可以有滚轮吗",Msg.TYPE_SENT));
        mMsgList.add(new Msg("可以有滚轮吗",Msg.TYPE_SENT));
        mMsgList.add(new Msg("可以有滚轮吗",Msg.TYPE_SENT));
        mMsgList.add(new Msg("可以有滚轮吗",Msg.TYPE_SENT));
        mMsgList.add(new Msg("可以有滚轮吗",Msg.TYPE_SENT));

    }

    public void sendMsg(View view) {
        String content = inputText.getText().toString();

        if(!"".equals(content)) {
            Msg msg = new Msg(content,Msg.TYPE_SENT);
            mMsgList.add(msg);

            //有新消息时，刷新RecyclerView中的显示
            mAdapter.notifyItemInserted(mMsgList.size() - 1);

            //将RecyclerView定位到最后一行
            mRecyclerView.scrollToPosition(mMsgList.size() - 1);

            //清空输入框中的内容
            inputText.setText("");

        }
    }


}
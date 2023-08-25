package com.example.soul.registerNlogin;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.soul.R;
import com.example.soul.registerNlogin.login.LoginActivity;


public class Entry2Soul extends AppCompatActivity {

    private Button loginButton;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);

        //初始化按钮
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);

        //登录按钮的点击事件
        loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //切换到Login活动
                    Intent intent = new Intent(Entry2Soul.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        );

        //注册按钮的点击事件
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //注册逻辑还没写，登录已经整麻了
                showToast("注册按钮被点击");
            }
        });
    }

    //显示提示消息
    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}

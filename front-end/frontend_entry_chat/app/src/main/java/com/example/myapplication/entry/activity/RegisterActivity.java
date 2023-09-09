package com.example.myapplication.entry.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.myapplication.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameRegister;
    private EditText passwordRegister;
    private Button registerButton;
    private Button registerReturn;

    private OkHttpClient client;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        usernameRegister = findViewById(R.id.usernameRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        registerButton = findViewById(R.id.sendRegister);
        registerReturn = findViewById(R.id.registerReturn);
        client = new OkHttpClient();

        registerReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);   //切换到MainActivity
            }

        });

        //点击登录按钮
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的用户名和密码
                String username = usernameRegister.getText().toString();
                String password = passwordRegister.getText().toString();
                sendregisterRequest(username, password);      //发送请求
            }
        });
    }

    private void sendregisterRequest(String username, String password) {
        MediaType contentType = MediaType.get("application/json; charset=utf-8");
        Map<String,String> params=new HashMap<>();
        params.put("username",username);
        params.put("password",password);
        String jsonRequestBody = JSON.toJSONString(params);  //请求体
        Log.e("请求体内容：", jsonRequestBody);

        RequestBody requestBody = RequestBody.create(contentType, jsonRequestBody);

        Request request = new Request.Builder()
                .url("http://192.168.3.62:8080/register")      //这是自己主机的ipv4地址。
                .post(requestBody)   //指定用POST方法
                .build();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Call call=client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    @Override
                    public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                        String result=response.request().body().toString();
                        Log.e("注册返回值：",result);
                        //服务器响应成功
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                });
            }
        }).start();
    }

    private void showToast(String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

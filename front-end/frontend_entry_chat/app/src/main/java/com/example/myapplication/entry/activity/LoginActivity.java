package com.example.myapplication.entry.activity;

import static com.example.myapplication.entry.activity.MainActivity.userInfo;
import static com.example.myapplication.utils.ServerInfo.ServerLoginURI;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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


public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private Button loginReturn;

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameRegister);
        passwordEditText = findViewById(R.id.passwordRegister);
        loginButton = findViewById(R.id.sendLogin);
        loginReturn = findViewById(R.id.loginReturn);
        client = new OkHttpClient();

        //点击返回按钮
        loginReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);   //切换到MainActivity
            }

        });

        //点击登录按钮
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的用户名和密码
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                sendLoginRequest(username, password);      //发送请求
            }
        });
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
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

package com.example.soul.registerNlogin.login;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.soul.R;
import com.example.soul.chat.activity.ChatActivity;

import java.io.IOException;
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

    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);           //把activity_login页面弄出来

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.sendLogin);
        client = new OkHttpClient();

        //点击登录按钮
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取用户输入的用户名和密码
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                sendLoginRequest(username, password);                   //发送请求，服务器收不到，终端没反应，蚌埠住了
            }
        });
    }

    private void sendLoginRequest(String username, String password) {

        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);


        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String jsonRequestBody = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";  //请求体
        RequestBody requestBody = RequestBody.create(JSON, jsonRequestBody);

        Request request = new Request.Builder()
                .url("http://192.168.3.62:8080/login")      //这是自己主机的ipv4地址。
                .post(requestBody)   //指定用POST方法，把请求体放进去
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast("连接超时");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    //服务器响应成功
                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
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

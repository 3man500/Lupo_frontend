package com.example.registerloginexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    // 서버에서 받는 response 인 messege와 access_token을 loginactivity에서 가지고 온다.
    private TextView res_message, res_access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        res_message = findViewById(R.id.res_message);
        res_access_token = findViewById(R.id.res_access_token);

        Intent intent = getIntent();
        String message = intent.getStringExtra("message");
        String access_token = intent.getStringExtra("access_token");

        res_message.setText(message);
        res_access_token.setText(access_token);
    }
}
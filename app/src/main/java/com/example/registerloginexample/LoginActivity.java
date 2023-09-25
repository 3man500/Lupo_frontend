package com.example.registerloginexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    private EditText et_login_id, et_login_password;
    private Button btn_login, btn_login_register;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        et_login_id = findViewById(R.id.et_login_id);
        et_login_password = findViewById(R.id.et_login_password);
        btn_login = findViewById(R.id.btn_login);
        btn_login_register = findViewById(R.id.btn_login_register);

        btn_login_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText에 현재 입력되어 있는 값을 get해온다. 가져온다.
                String userID = et_login_id.getText().toString();
                String userPass = et_login_password.getText().toString();

                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // php 서버에서 선언한 success 구문.. 백엔드랑 연동할 때 수정할 수도 있다.
                            // 서버 통신이 잘됐는지에 대한 여부
                            boolean success = jsonObject.getBoolean("success");
                            // 로그인에 성공한 경우
                            if (success) {
                                // 서버 key값에 따라 달라진다. key값이 userID, userPass인 경우이다. 수정할 수도 있다.
                                String userID = jsonObject.getString("userID");
                                String userPass = jsonObject.getString("userPass");

                                Toast.makeText(getApplicationContext(), "보금자리에 온 것을 환영합니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                // data를 담아주는 곳
                                intent.putExtra("userID", userID);
                                intent.putExtra("userPass", userPass);
                                startActivity(intent);
                            } else { // 로그인에 실패한 경우
                                Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                //LoginRequest class를 만들어서 쓰는 인자.
                LoginRequest loginRequest = new LoginRequest(userID, userPass, responseListener);
                RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                queue.add(loginRequest);
            }
        });
    }
}
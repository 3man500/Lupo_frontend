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

public class RegisterActivity extends AppCompatActivity {

    private EditText et_register_id, et_register_password, et_name, et_age;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //액티비티 시작시 처음으로 실행하는 생명주기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 아이디 값 알려주기
        et_register_id = findViewById(R.id.et_register_id);
        et_register_password = findViewById(R.id.et_register_password);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);

        // 회원가입 버튼 클릭 시 수행
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText에 현재 입력되어 있는 값을 get해온다. 가져온다.
                String userID = et_register_id.getText().toString();
                String userPass = et_register_password.getText().toString();
                String userName = et_name.getText().toString();
                int userAge = Integer.parseInt(et_age.getText().toString());

                // 해당 데이터를 운반하기 위한 것
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            // php 서버에서 선언한 success 구문.. 백엔드랑 연동할 때 수정할 수도 있다.
                            // 서버 통신이 잘됐는지에 대한 여부
                            boolean success = jsonObject.getBoolean("success");
                            // 회원 등록에 성공한 경우
                            if (success) {
                                Toast.makeText(getApplicationContext(), "Lupo 일원이 된 것을 환영합니다", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                            } else { // 회원 등록에 실패한 경우
                                Toast.makeText(getApplicationContext(), "회원 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                                return;
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                };
                // 서버를 Volley를 이용해서 요청을 한다.
                RegisterRequest registerRequest = new RegisterRequest(userID, userPass, userName, userAge, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);
            }
        });
    }
}
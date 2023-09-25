package com.example.registerloginexample;

import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
        Log.v("테스트", "실행됨");
        System.out.println(1);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText에 현재 입력되어 있는 값을 get해온다. 가져온다.
                String userID = et_register_id.getText().toString();
                String userPass = et_register_password.getText().toString();
                String userName = et_name.getText().toString();
                int userAge = Integer.parseInt(et_age.getText().toString());
                Log.v("테스트", "1");
                sendRequest(userID, userPass, userName, userAge);

                // 해당 데이터를 운반하기 위한 것
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.v("응답", response);
                            JSONObject jsonObject = new JSONObject(response);
                            // php 서버에서 선언한 success 구문.. 백엔드랑 연동할 때 수정할 수도 있다.
                            // 서버 통신이 잘됐는지에 대한 여부
                            boolean success = jsonObject.getBoolean("success");
                            Log.v("응답", response);
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

    public void sendRequest(String userID, String userPassword, String userName, int userAge) {
        String url = "http://10.0.2.2:3000/auth/signup";
        StringRequest request = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                    @Override
                    public void onResponse(String response) {
                        System.out.println("응답 -> " + response);
                        Toast.makeText(getApplicationContext(), "Lupo 일원이 된 것을 환영합니다", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("에러 -> " + error.getMessage());
                        Toast.makeText(getApplicationContext(), "회원 등록에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("userID", userID);
                params.put("password", userPassword);
                params.put("username", userName);
                params.put("userAge", userAge + "");
                return params;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");

    }
}
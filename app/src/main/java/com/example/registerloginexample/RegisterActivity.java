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

    private EditText et_register_id, et_register_password, et_name, et_age, et_gender, et_nickname, et_phone;
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
        et_gender = findViewById(R.id.et_gender);
        et_nickname = findViewById(R.id.et_nickname);
        et_phone = findViewById(R.id.et_phone);

        // 회원가입 버튼 클릭 시 수행
        btn_register = findViewById(R.id.btn_register);
        Log.v("테스트", "실행됨");
        System.out.println(1);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText에 현재 입력되어 있는 값을 get해온다. 가져온다.
                String userName = et_register_id.getText().toString();
                String userPass = et_register_password.getText().toString();
                String realname = et_name.getText().toString();
                int userAge = Integer.parseInt(et_age.getText().toString());
                String nickname = et_nickname.getText().toString();
                String gender = et_gender.getText().toString();
                String phone = et_phone.getText().toString();
                Log.v("테트스트", et_age.getText().toString());
                //sendloginRequest로 서버에 userPass와 userID를 보낸다. 이때 userID는 서버의 username과 같다. 순서 바꾸면 안된다.
                // 나중에 서버의 username을 userid로 고칠 예정
                sendRequest(userName, userPass, realname, userAge, nickname, gender, phone);
            }
        });
    }

    public void sendRequest(String userName, String userPassword, String realname, int userAge, String nickname, String gender, String phone) {
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
                params.put("username", userName);
                params.put("password", userPassword);
                // lupo id
                params.put("nickname", nickname);
                params.put("age", userAge + "");
                params.put("realname", realname);
                params.put("gender", gender);
                params.put("phone", phone);
                return params;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");

    }
}
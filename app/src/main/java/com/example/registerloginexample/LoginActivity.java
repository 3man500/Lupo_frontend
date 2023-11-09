package com.example.registerloginexample;

import static java.sql.DriverManager.println;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.handlers.InitResultHandler;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.adapter.SendBirdUIKitAdapter;
import com.sendbird.uikit.interfaces.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private EditText et_login_id, et_login_password;
    private Button btn_login, btn_login_register;

    private Context context;
    SharedPreferences sharedPreferences;
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
                Log.v("테스트", "1");
                startActivity(intent);
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // editText에 현재 입력되어 있는 값을 get해온다. 가져온다.
                String userID = et_login_id.getText().toString();
                String userPass = et_login_password.getText().toString();
                //sendloginRequest로 서버에 userPass와 userID를 보낸다.
                sendloginRequest(userID, userPass);
            }
        });

        context = getApplicationContext();

    }

    public void sendloginRequest(String userID, String userPassword) {
        String url = "http://10.0.2.2:3000/auth/signin";
        StringRequest request = new StringRequest(
                // POST는 리소스를 생성/ 업데이트 하기 위해 서버에 데이터를 보내는 데 사용됩니다.
                Request.Method.POST,
                url,
                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            // 서버 key값으로 messege, access_token을 받는다
                            String message = jsonObject.getString("message");
                            String access_token = jsonObject.getString("access_token");
                            // String nickname = jsonObject.getString("nickname");

                            Toast.makeText(getApplicationContext(), "보금자리에 온 것을 환영합니다", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            // data를 담아주는 곳
                            intent.putExtra("message", message);
                            intent.putExtra("access_token", access_token);
                            //Editor를 preferences에 쓰겠다고 연결

                            // 반환받은 accesstoken을 파일에 저장하는 작업
                            // 서버에 계속 요청하는 것보다 저장하는 것이 필요
                            sharedPreferences = context.getSharedPreferences("myPref", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("access_token", access_token);
                            editor.apply();

                            // 메소드 호출
                            // callback execute가 실행되는 코드
                            // (): callback 함수를 의미한다.
                            // userID: 사용자 lupo id
                            signInSendbird(userID, () -> {
                                getPreferences(context);
                                // init이 설정됐으면, Main Activity를 실행한다.
                                startActivity(intent);
                            });
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "로그인에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String,String>();
                params.put("username", userID);
                params.put("password", userPassword);
                return params;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");

    }
    @FunctionalInterface
    interface Callback {
        void execute();
    }
    public void signInSendbird(String userID, Callback callback) {
        // 초기화를 uikit의 init만 있어도 할 수 있다.
        SendBirdUIKit.init(new SendBirdUIKitAdapter() {
            @NonNull
            @Override

            // lupo와 sendbird를 연결해주기 위한 id
            public String getAppId() {
                return "96E21ACA-9F16-4971-A775-87BE1BD8804D";  // Specify your Sendbird application ID.
            }

            @Nullable
            @Override
            public String getAccessToken() {
                return "";
            }

            @NonNull
            @Override
            public UserInfo getUserInfo() {
                return new UserInfo() {
                    @Override
                    public String getUserId() {
                        return userID;  // Specify your user ID.
                    }

                    @Nullable
                    @Override
                    public String getNickname() {
                        return userID;  // Specify your user nickname.
                    }

                    @Nullable
                    @Override
                    public String getProfileUrl() {
                        return "";
                    }
                };
            }

            @NonNull
            @Override
            public InitResultHandler getInitResultHandler() {
                return new InitResultHandler() {
                    @Override
                    public void onMigrationStarted() {
                        // DB migration has started.
                    }

                    @Override
                    public void onInitFailed(SendBirdException e) {
                        // If DB migration fails, this method is called.
                    }

                    @Override
                    public void onInitSucceed() {
                        SendBirdUIKit.connect(new SendBird.ConnectHandler() {
                            @Override
                            public void onConnected(User user, SendBirdException e) {
                                callback.execute();
                            }
                        });
                    }
                };
            }
        }, this);
    }

    public static SharedPreferences getPreferences(Context context){
        // Context 객체를 입력으로 받아와서 SharedPreferences 객체를 반환
        // Context는 Android 애플리케이션의 환경과 상태에 대한 정보를 제공하는 클래스
        return context.getSharedPreferences("access_token", Context.MODE_PRIVATE);
    }
}
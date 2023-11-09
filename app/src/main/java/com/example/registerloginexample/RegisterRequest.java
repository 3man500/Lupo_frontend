package com.example.registerloginexample;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RegisterRequest extends StringRequest {

    // 서버 URL 설정
    final static private String URL = "http://localhost:3000/auth/signup";
    private Map<String, String> map;

    public RegisterRequest(String userID, String userPassword, String userName, int userAge, String nickname, String realname, String gender, String phone, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("password", userPassword);
        // 유저 lupo 아이디
        map.put("username", userName);
        map.put("age", userAge + "");
        // 유저 닉네임
        map.put("nickname", nickname);
        // 실명
        map.put("realname", realname);
        // 성별: M - Male F - Female
        map.put("gender", gender);
        // 짝대기 없이 11자리
        map.put("phone", phone);
    };

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

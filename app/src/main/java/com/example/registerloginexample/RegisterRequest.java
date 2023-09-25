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

    public RegisterRequest(String userID, String userPassword, String userName, int userAge, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
        map.put("password", userPassword);
        map.put("username", userName);
        map.put("userAge", userAge + "");
    };

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

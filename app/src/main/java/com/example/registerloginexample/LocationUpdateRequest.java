package com.example.registerloginexample;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LocationUpdateRequest extends StringRequest {
    // 서버 URL 설정
    final static private String URL = "http://localhost:3000/auth/location";
    private Map<String, String> map;

    public LocationUpdateRequest(Double latitude, Double longitude, Response.Listener<String> listener) {
        super(Method.PATCH, URL, listener, null);

        map = new HashMap<>();
        // 현재 서버에서 userID를 username으로 설정, userPassword를 password로 설정
        map.put("lat", latitude.toString());
        map.put("lon", longitude.toString());

    };

    @Nullable
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}

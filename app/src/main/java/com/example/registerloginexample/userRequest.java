package com.example.registerloginexample;

import static android.content.Context.MODE_PRIVATE;
import static java.sql.DriverManager.println;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sendbird.android.shadow.com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import android.content.SharedPreferences;

public class userRequest {
    interface Callback {
        void execute(JSONArray jsonArray);
    };

    public void sendUpdateuserRequest(Callback callback) {
        String url = "http://10.0.2.2:3000/auth/users/adjacency";

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                    @Override

                    public void onResponse(String response) {
                        JSONArray jsonArray = null;

                        try {
                            jsonArray = new JSONArray(response);
                            Log.i("testa", jsonArray.toString());
                            // return 값 넣기
                            callback.execute(jsonArray);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("testa", error);
                        Toast.makeText(MainActivity.mContext, "위치 정보 업데이트에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
        ) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // body값 http 요청 본문에 포함

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences preferences = MainActivity.mContext.getSharedPreferences("myPref", MODE_PRIVATE);
                String accessToken = preferences.getString("access_token", "dd");

                Map headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                // 로컬 스토리지에 저장되는 쿠키
                headers.put("Cookie", "access_token=" + accessToken);
//                Map<String,String> headers = new HashMap<String, String>();
//                headers.put("Accept","application/json");
//
//
//                if(!MyApplication.getCookie(context).equals("")){
//                    String cookie = MyApplication.getCookie(context);
//                    Show.m("Cookie to load from preferences: " + cookie);
//                    headers.put("Cookie", cookie);
//                }

                return headers;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(MainActivity.mContext); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");



    }
}

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

    // 콜백 함수는 일반적으로 비동기 작업이나 이벤트 처리와 관련이 있으며, 이를 통해 이벤트가 발생하면 특정 작업이 실행
    interface Callback {
        void execute(JSONArray jsonArray);
    };


    public static void sendUpdateuserRequest(Callback callback) {
        String url = "http://10.0.2.2:3000/auth/users/adjacency";

        StringRequest request = new StringRequest(
                // GET: 리소스에서 데이터를 요청
                Request.Method.GET,
                url,
                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                    @Override

                    // onResponse: 네트워크 요청에 대한 응답 문자열을 받아와서 처리하는 역할
                    public void onResponse(String response) {
                        JSONArray jsonArray = null;

                        try {
                            // 문자열 response를 기반으로 새로운 JSON 배열 jsonArray를 생성
                            jsonArray = new JSONArray(response);
                            Log.i("모든 json Array", jsonArray.toString());
                            // return 값 넣기
                            // 콜백(callback)을 호출하여 jsonArray를 전달
                            // 여기까지 오면 실행할 것들을 빈칸 뚫어 놓기
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
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences preferences = MainActivity.mContext.getSharedPreferences("myPref", MODE_PRIVATE);

                // preference 파일에 저장한 access token 값을 이용하는 코드
                String accessToken = preferences.getString("access_token", "dd");

                Map headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                // 로컬 스토리지에 저장되는 쿠키
                headers.put("Cookie", "access_token=" + accessToken);

                return headers;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(MainActivity.mContext); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");



    }
}

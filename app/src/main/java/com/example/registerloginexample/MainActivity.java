package com.example.registerloginexample;


import static java.sql.DriverManager.println;
import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.registerloginexample.databinding.ActivityMapsBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sendbird.android.GroupChannel;
import com.sendbird.android.GroupChannelListQuery;
import com.sendbird.android.GroupChannelParams;
import com.sendbird.android.SendBirdException;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.activities.ChannelActivity;
import com.sendbird.uikit.activities.ChannelListActivity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

// , AppCompatActivity

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    static Context mContext;
    private Button btn_open, btn_open2;
    private TextView tv_id, tv_pass;
    private DrawerLayout drawerLayout;
    private View drawerView;
    // View: UI 요소를 참조하거나 조작할 수 있게 함
    private View chattingView;
    private View profileView;
    SupportMapFragment mapFragment;
    Marker myMarker;
    MarkerOptions myLocationMarker;
    Circle circle;
    CircleOptions circle1KM;
    LocationManager manager;
    LocationListener locationListener;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double cur_lat;
    private double cul_lon;

    // 서버에서 받은 response 인 messege와 access_token을 loginactivity에서 가지고 온다.
    // private TextView res_message, res_access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.mContext = getApplicationContext();

        // activity_main.xml과 연결시켜주는 코드
        setContentView(R.layout.activity_main);

        // drawer_layout의 id를 찾는 코드
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);


        // main.xml에 drawer.xml을 include했으므로 id를 main.java에서 찾을 수 있다.
        drawerView = (View) findViewById(R.id.drawer);

        // chatting.xml의 id를 찾는 코드
        chattingView = (View) findViewById(R.id.chatting_available);

        // profile.xml의 id를 찾는 코드
        profileView = (View) findViewById(R.id.profile);

        Button btn_open_profile = (Button) findViewById(R.id.btn_open_profile);

        Button btn_open_can_talk_people = (Button) findViewById(R.id.btn_open_can_talk_people);

        Button btn_open = (Button) findViewById(R.id.btn_open);



        // 주변 사람이 누가 있는지 알려주는 xml을 여는 코드
        btn_open_can_talk_people.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // MainActivity에서 ChattingActivity로 연결해주는 intent
                Intent intent = new Intent(MainActivity.this, ChattingAvailableActivity.class);
                Log.v("테스트", "1");
                // ChattingActivity를 실행하라
                startActivity(intent);
            }
        });

        btn_open_profile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // MainActivity에서 ProfileActivity로 연결해주는 intent
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                Log.v("테스트", "1");
                // ProfileActivity를 실행하라
                startActivity(intent);
            }
        });


        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 네비게이션 메뉴가 열리는 코드
                drawerLayout.openDrawer(drawerView);
                LoginActivity loginActivity = new LoginActivity();

            }
        });

        Button btn_close = (Button) findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

//        drawerLayout.setDrawerListener(listener);
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        // 아래는 login activity에서 token 값과 messege를 얻기 위해 필요한 코드들 (지우지 말기)
        Intent intent = getIntent();

        // 서버의 response인 messege와 access_token을 저장하는 변수값 생성
        String message = intent.getStringExtra("message");
        String access_token = intent.getStringExtra("access_token");


        // drawer layout을 오른쪽이나 왼쪽으로 슬라이드 했을 때 이곳에서 상태값을 받아온다.
        // 추가 기능 구현 할 때 함수안에 적으면 된다.
        DrawerLayout.DrawerListener listener = new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        };
        btn_open = findViewById(R.id.btn_open);
        btn_open2 = findViewById(R.id.btn_open2);
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


        try {
            MapsInitializer.initialize(this);
        } catch (Exception e) {
        }


//    현재 위치 받아오기
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                Log.i("MyLocation", "위도" + location.getLatitude());
                Log.i("MyLocation", "경도" + location.getLongitude());
                double latitude = location.getLatitude();
                String updown_latitude = String.format("%.5f", latitude);

                // sendUpdateLocationRequest(Double.toString(location.getLongitude()), Double.toString(location.getLatitude()));
                sendUpdateLocationRequest(updown_latitude, Double.toString(location.getLongitude()));

            }
        };


        Location loc_Current =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc_Current != null) {
            cur_lat = loc_Current.getLatitude(); //위도
            cul_lon = loc_Current.getLongitude(); //경도

        }

        //1초마다 위치 갱신 , 10미터마다 위치 갱신
        locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER,10000,0,locationListener);

    }
    // on create 문 끝

    //    위치 권한 설정
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {
            // 권한 요청
            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,  android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION,  android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    // 서버에서 받은 response 인 messege와 access_token을 loginactivity에서 가지고 온다.
    // private TextView res_message, res_access_token;


    public void sendUpdateLocationRequest(String latitude, String longitude) {
        String url = "http://10.0.2.2:3000/auth/location";
        Log.i("latitude", latitude);
        Log.i("longitude", longitude);
        StringRequest request = new StringRequest(
                Request.Method.PATCH,
                url,
                new Response.Listener<String>() { //응답을 잘 받았을 때 이 메소드가 자동으로 호출
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            Log.i("update location",jsonObject.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() { //에러 발생시 호출될 리스너 객체
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.v("error response", error);
                        Toast.makeText(getApplicationContext(), "위치 정보 업데이트에 실패하였습니다", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
        ) {

            @Override
            public Map<String, String> getParams() throws AuthFailureError {
                 Map<String,String> params = new HashMap<String,String>();
                 // body값 http 요청 본문에 포함
                 params.put("lat", latitude);
                 params.put("lon", longitude);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {

                SharedPreferences preferences = getSharedPreferences("myPref", MODE_PRIVATE);
                String accessToken = preferences.getString("access_token", "dd");

                Map headers = new HashMap();
                //headers.put("Content-Type", "application/json");
                // 로컬 스토리지에 저장되는 쿠키
                headers.put("Cookie", "access_token=" + accessToken );
                return headers;
            }
        };
        request.setShouldCache(false); //이전 결과 있어도 새로 요청하여 응답을 보여준다.
        AppHelper.requestQueue = Volley.newRequestQueue(this); // requestQueue 초기화 필수
        AppHelper.requestQueue.add(request);
        println("요청 보냄.");

        // Handler 선언
        Handler mHandler = new Handler();
        // 데이터를 업데이트하는 지연 시간을 설정 (5초)
        final int UPDATE_DELAY = 5000;


        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mMap);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                mMap = googleMap;
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                    return;
                }
                LatLng curpoint = new LatLng(cur_lat, cul_lon);

                //    마커로 위치 표시
//                    showMyLocationMarker(curpoint);
                // 현재위치로 카메라 이동 및 확대
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curpoint, 13));
                mMap.setMyLocationEnabled(true);
                circle1KM = new CircleOptions()
                        .center(curpoint)
                        .radius(5000)       // 반지름 단위 : m
                        .strokeWidth(1.0f)
                        .fillColor(Color.parseColor("#880000ff"));


                circle = mMap.addCircle(circle1KM);
                circle.setCenter(curpoint);
                circle.setClickable(true);

                private void animateCircleColorChange(circle, int newColor) {
                    ObjectAnimator objectAnimator = ObjectAnimator.ofObject(circle, "fillColor", new ArgbEvaluator(), circle.getFillColor(), newColor);
                    objectAnimator.setDuration(2000); // 애니메이션 지속 시간 (ms)
                    objectAnimator.start();
                }

                animateCircleColorChange(circle, Color.parseColor("#88aa00ff"));

                GoogleMap.OnCircleClickListener circleClickListener = new GoogleMap.OnCircleClickListener() {
                    @Override
                    public void onCircleClick(Circle circle) {
                        // Circle이 클릭되었을 때 실행되는 코드
                        // 예시로 채널 목록 액티비티를 열도록 설정
                        Intent intent = ChannelListActivity.newIntent(MainActivity.this);
                        startActivity(intent);

                    }
                };

                mMap.setOnCircleClickListener(circleClickListener);


                // 1. 유저 목록을 불러온다.
                // "userRequest"라는 변수에 "userRequest" 클래스(또는 데이터 타입)의 새로운 객체 인스턴스를 할당하는 것
                // 앞에 있는 userRequest는 클래스 명, 뒤에 있는 userRequest는 변수



                userRequest.sendUpdateuserRequest((jsonArray) -> {
                    // userRequest 객체의 sendUpdateuserRequest 메소드를 호출하고, 람다식을 전달하여 콜백을 설정.
                    // 이 콜백은 jsonArray 매개변수를 받아와서 처리

//                    mHandler.postDelayed(mUpdateRunnable, UPDATE_DELAY);
                    int count = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        try {
                            // JSONArray에서 각 항목을 가져옵니다.
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            // 이제 jsonObject를 사용하여 원하는 작업을 수행할 수 있습니다. username으로 sendbird id 연동 가능
                            String id = jsonObject.getString("id"); // JSON 객체에서 필요한 데이터를 추출
                            String username = jsonObject.getString("username");
                            String gender = jsonObject.getString("gender");
                            String nickname = jsonObject.getString("nickname");
                            double lat = jsonObject.getDouble("lat");
                            double lon = jsonObject.getDouble("lon");
                            int age = jsonObject.getInt("age");
                            // LatLng 객체를 생성합니다.
                            LatLng location = new LatLng(lat, lon);

                            if (mMap != null) {
                                mMap.addMarker(new MarkerOptions()
                                        .position(location)
                                        .title(username)
                                        .snippet("Age: " + age + ", Gender: " + gender + ", Nickname: " + nickname));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
                            };

                            // 여기에서 가져온 데이터를 사용하여 작업 수행열

                            Log.i(count + "번째 가까운 user", "id: " + id + " username: " + username + " age: " + age + " gender: " + gender + " nickname: " + nickname);
                            count++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        // 마커를 클릭했을 때의 동작을 정의합니다.
                        // 여기에서 원하는 정보를 가져와서 토스트로 출력하는 코드를 추가하면 됩니다.
                        String username = marker.getTitle();
                        Log.i("센드버드 아이디 -> ", username);
                        String snippet = marker.getSnippet();
                        String[] parts = snippet.split(", ");
                        String nickname = parts[2].substring("Nickname: ".length());

                        GroupChannelParams params = new GroupChannelParams();
                        params.setName(nickname);
                        params.addUserId(username);
                        params.setDistinct(true);

                        GroupChannel.createChannel(params, new GroupChannel.GroupChannelCreateHandler() {
                            @Override
                            public void onResult(GroupChannel groupChannel, SendBirdException e) {
                                if (e != null) {
                                    Log.d("XXXX", e.getMessage());
                                }

                                // Open the created chat channel
                                Intent intent = ChannelActivity.newIntent(getApplicationContext(), groupChannel.getUrl());
                                startActivity(intent);
                            }
                        });

                        // 토스트로 정보 출력
                        Toast.makeText(getApplicationContext(), "Username: " + username + "\n" + snippet, Toast.LENGTH_SHORT).show();

                        // true를 반환하면 마커 클릭 이벤트가 소비되었음을 나타냅니다.
                        // false를 반환하면 이후 기본 동작도 함께 수행됩니다.
                        return true;
                    }
                });



                // 2. 불러온 유저 목록을 맵 위에 렌더링 한다.
                // 3. 렌더링 한 컴포넌트는 클릭이 가능하다.
                // 4. 클릭했을때는 유저 프로필을 보여준다.
                // 5. 3초마다 인접 모든 사용자의 jsonarray 달라진 lat, lon 갱신
            }
        });

    }

}




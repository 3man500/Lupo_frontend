package com.example.registerloginexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.example.registerloginexample.databinding.ActivityMapsBinding;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    LocationManager locationManager;
    LocationListener locationListener;
    private double longitude, latitude;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private double cur_lat;
    private double cul_lon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//    현재 위치 받아오기
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
            return;
        }

        Location loc_Current =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (loc_Current != null){
        cur_lat = loc_Current.getLatitude(); //위도
        cul_lon = loc_Current.getLongitude(); //경도

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                3000, -1, locationListener); // 3초 마다 위치 갱신
}
//        지도 표시
        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

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


//    마커로 위치 표시
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng mylocation = new LatLng(cur_lat,cul_lon);
        // 반경 1KM원
        CircleOptions circle1KM = new CircleOptions().center(mylocation) //원점
                .radius(5gi000)      //반지름 단위 : m
                .strokeWidth(0f)  //선너비 0f : 선없음
                .fillColor(Color.parseColor("#880000ff")); //배경색
        mMap.addMarker(new MarkerOptions().position(mylocation).title("당신의 위치"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
        //원추가
        this.mMap.addCircle(circle1KM);
    }
}



class MainActivity2 extends AppCompatActivity {


    private TextView tv_id, tv_pass;


    private DrawerLayout drawerLayout;
    private View drawerView;

    // 서버에서 받은 response 인 messege와 access_token을 loginactivity에서 가지고 온다.
    // private TextView res_message, res_access_token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerView = (View)findViewById(R.id.drawer);

        Button btn_open = (Button)findViewById(R.id.btn_open);
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 네비게이션 메뉴가 열리는 코드
                drawerLayout.openDrawer(drawerView);
            }
        });

        Button btn_close = (Button)findViewById(R.id.btn_close);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawers();
            }
        });

        drawerLayout.setDrawerListener(listener);
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

        // 해당 messege와 access_token을 화면에 띄울 필요가 없으므로 주석처리 (xml과 연결하는 코드 필요 없음)
        // res_message = findViewById(R.id.res_message);
        // res_access_token = findViewById(R.id.res_access_token);
        // res_message.setText(message);
        // res_access_token.setText(access_token);
    }


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


}
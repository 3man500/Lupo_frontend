package com.example.registerloginexample;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.handlers.InitResultHandler;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private View drawerView;

    // View: UI 요소를 참조하거나 조작할 수 있게 함
    private View chattingView;

    private View profileView;

    // 서버에서 받은 response 인 messege와 access_token을 loginactivity에서 가지고 온다.
    // private TextView res_message, res_access_token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


        // chat SDK 초기화
        // 다음 단계 부터는 모두 비동기이므로 handler가 필요하다. 안그러면 무한 콜백 지옥에 빠진다.
        // When the useLocalCaching is set to true. (caching이 true로 세팅된 경우 동작.)
        SendBird.init("96E21ACA-9F16-4971-A775-87BE1BD8804D", getApplicationContext(), true, new InitResultHandler() {
            @Override
            public void onMigrationStarted() {
                Log.i("Application", "Called when there's an update in Sendbird server.");
            }

            @Override
            public void onInitFailed(SendBirdException e) {
                Log.i("Application", "Called when initialize failed. SDK will still operate properly as if useLocalCaching is set to false.");
            }

            @Override
            public void onInitSucceed() {
                Log.i("Application", "Called when initialization is completed.");
            }
        });

        // activity_main.xml과 연결시켜주는 코드
        setContentView(R.layout.activity_main);

        // drawer_layout의 id를 찾는 코드
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        // main.xml에 drawer.xml을 include했으므로 id를 main.java에서 찾을 수 있다.
        drawerView = (View)findViewById(R.id.drawer);

        // chatting.xml의 id를 찾는 코드
        chattingView = (View)findViewById(R.id.chatting_available);

        // profile.xml의 id를 찾는 코드
        profileView = (View) findViewById(R.id.profile);

        Button btn_open_profile = (Button) findViewById(R.id.btn_open_profile);

        Button btn_open_can_talk_people = (Button) findViewById(R.id.btn_open_can_talk_people);

        Button btn_open = (Button)findViewById(R.id.btn_open);

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
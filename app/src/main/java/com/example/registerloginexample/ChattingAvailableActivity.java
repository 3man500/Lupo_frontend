package com.example.registerloginexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;

public class ChattingAvailableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_available);

        //여기까지 함
        Button btn_open_chatting_people_3 = (Button) findViewById(R.id.btn_open_chatting_people_3);

        btn_open_chatting_people_3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // ChattingAvailableActivity에서 ChattingActivity로 연결해주는 intent
                Intent intent = new Intent(ChattingAvailableActivity.this, ChattingActivity.class);
                Log.v("테스트", "1");
                // ChattingActivity를 실행하라
                startActivity(intent);
            }
        });

         //sendbird 서버에 접속
         //When useLocalCaching is set to true.
         //You must receive the result of the InitResultHandler() before calling the connect().
         //Any methods can be called once the user is connected to Sendbird server.
        SendBird.connect("sendbird_desk_agent_id_faee4825-8cf4-4763-acd9-e797c3e80071", new SendBird.ConnectHandler() {
            @Override
            public void onConnected(User user, SendBirdException e) {
                if (user != null) {
                    if (e != null) {
                        // Proceed in offline mode with the data stored in the local database.
                        // Later, connection will be made automatically
                        // and can be notified through the ConnectionHandler.onReconnectSucceeded().
                    } else {
                        // Proceed in online mode.
                    }
                } else {
                    // Handle error.
                }
            }
        });

        // 새 공개 채널 만들기. 모든 사용자가 초대없이 쉽게 참여할 수 있는곳.
        // The following sample code continues from Step 6.
        OpenChannel.createChannel(new OpenChannel.OpenChannelCreateHandler() {
            @Override
            public void onResult(OpenChannel openChannel, SendBirdException e) {
                if (e != null) {
                    // Handle error.
                }

                // Call the instance method of the result object in the openChannel parameter of the onResult() callback method.
                openChannel.enter(new OpenChannel.OpenChannelEnterHandler() {
                    @Override
                    public void onResult(SendBirdException e) {
                        if (e != null) {
                            // Handle error.
                        }

                        // The current user successfully enters the open channel,
                        // and can chat with other users in the channel by using APIs.
                    }
                });

                openChannel.sendUserMessage("hello", new BaseChannel.SendUserMessageHandler() {
                    @Override
                    public void onSent(UserMessage userMessage, SendBirdException e) {
                        if (e != null) {
                            // Handle error.
                        }

                        // The message is successfully sent to the channel.
                        // The current user can receive messages from other users
                        // through the onMessageReceived() method of an event handler.
                    }
                });

            }
        });
         //sendbird api 끝

    }
}
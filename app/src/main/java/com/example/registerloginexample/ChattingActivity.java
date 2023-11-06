package com.example.registerloginexample;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.app.Application;
import android.util.Log;

import com.sendbird.android.BaseChannel;
import com.sendbird.android.OpenChannel;
import com.sendbird.android.SendBird;
import com.sendbird.android.SendBirdException;
import com.sendbird.android.User;
import com.sendbird.android.UserMessage;
import com.sendbird.uikit.SendBirdUIKit;
import com.sendbird.uikit.adapter.SendBirdUIKitAdapter;
import com.sendbird.uikit.interfaces.UserInfo;
import com.sendbird.android.handlers.InitResultHandler;
//import com.sendbird.android.exception.SendbirdException;
import com.sendbird.uikit.activities.ChannelListActivity;

public class ChattingActivity extends ChannelListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    // protected void onCreate(Bundle savedInstanceState) {
        // super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);

    }
}
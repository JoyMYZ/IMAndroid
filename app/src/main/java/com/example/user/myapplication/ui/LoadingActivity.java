package com.example.user.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.example.user.myapplication.R;
import com.example.user.myapplication.ui.LoginActivity;

import java.util.concurrent.TimeUnit;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/27 21:49
 * Description:    TODO
 **/

public class LoadingActivity extends AppCompatActivity {
    private static final int MSG_OK=1;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_OK:
                    Intent intent=new Intent(LoadingActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        handler.sendEmptyMessageDelayed(MSG_OK,3000);
    }
}

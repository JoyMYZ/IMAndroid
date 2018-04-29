package com.example.user.myapplication.application;

import android.app.Application;
import android.widget.Toast;

import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.handler.MessageReceiveHandler;
import com.example.user.myapplication.utils.StaticClass;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import cn.bmob.newim.BmobIM;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.application
 * User:           ${User}
 * Date&Time:      2018/4/20 16:58
 * Description:    TODO
 **/

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Bmob.initialize(this, StaticClass.BMOB_KEY);
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new MessageReceiveHandler());
        }
    }

    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

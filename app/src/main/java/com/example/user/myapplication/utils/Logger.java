package com.example.user.myapplication.utils;

import android.util.Log;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.utils
 * User:           ${User}
 * Date&Time:      2018/4/7 16:49
 * Description:    TODO
 **/

public class Logger {
    //logger开关
    private static boolean debug=true;

    public static void info(String tag,String message){
        if(checkLoggerState()) {
            Log.i(tag, message);
        }
    }

    public static void error(String tag,String message){
        if(checkLoggerState()) {
            Log.e(tag, message);
        }
    }

    public static void debug(String tag,String message){
        if(checkLoggerState()) {
            Log.d(tag, message);
        }
    }

    public static void warn(String tag,String message){
        if(checkLoggerState()){
            Log.w(tag,message);
        }
    }

    private static boolean checkLoggerState() {
        return debug;
    }
}

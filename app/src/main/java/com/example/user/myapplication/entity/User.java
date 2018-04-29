package com.example.user.myapplication.entity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import java.util.function.Function;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/7 16:34
 * Description:    TODO
 **/

public class User extends BmobObject {
    private String uid;
    private String username;
    private String password;
    private String avatar;
    private boolean isOnline;

    private volatile static User current_user;

    public User(){
        this.uid="";
    }

    public User(String uid, String username, String avatar) {
        this.uid=uid;
        this.setObjectId(uid);
        this.username = username;
        this.avatar = avatar;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid){
        this.uid=uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public static User getCurrentUser() {
        return current_user;
    }

    //设置当前用户的方法
    public static void setCurrentUser(User user) {
        current_user = new User(user.getObjectId(),user.getUsername(),user.getAvatar());
    }

    public static User getUser(BmobIMUserInfo info) {
        if (info != null) {
            User user = new User(info.getUserId(),info.getName(),info.getAvatar());
            return user;
        } else
            return null;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", isOnline=" + isOnline +
                '}';
    }

    public static void loadUser(String uid, final Handler handler){
        BmobQuery<User> query=new BmobQuery<>();
        query.getObject(uid, new QueryListener<User>() {
            @Override
            public void done(User user, BmobException e) {
                if (e == null) {
                    //异步操作结束时，发送消息
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putString("uid", user.getObjectId());
                    bundle.putString("username", user.getUsername());
                    bundle.putString("avatar", user.getAvatar());
                    message.setData(bundle);
                    handler.sendMessage(message);
                } else {
                    Logger.error(StaticClass.LOGGER_TAG, "User:" + e.getErrorCode() + "," + e.getMessage());
                }
            }
        });
    }

    //对应loadUser发送消息的处理方法
    public static User parseMessage(Message msg){
        User user=new User();
        Bundle bundle=msg.getData();
        if(bundle!=null){
            user.setUid(bundle.getString("uid"));
            user.setUsername(bundle.getString("username"));
            user.setAvatar(bundle.getString("avatar"));
        }
        return user;
    }
}

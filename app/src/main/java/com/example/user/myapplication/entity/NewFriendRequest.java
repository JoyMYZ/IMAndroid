package com.example.user.myapplication.entity;

import java.io.Serializable;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/22 11:40
 * Description:    TODO
 **/

public class NewFriendRequest implements Serializable {
    //状态：未读、已读、已添加、已拒绝等
    public enum State{
        UNREAD,//未读
        READ,//已读

        ACCEPTED,//已添加
        REFUSED//已拒绝
    }
    //消息标识
    private Long id;
    //用户uid
    private String uid;
    //留言消息
    private String msg;
    //用户名
    private String name;
    //头像
    private String avatar;
    private State state;
    //请求时间
    private Long time;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public State getStatus() {
        return state;
    }

    public void setStatus(State state) {
        this.state= state;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "NewFriendRequest{" +
                "id=" + id +
                ", uid='" + uid + '\'' +
                ", msg='" + msg + '\'' +
                ", name='" + name + '\'' +
                ", avatar='" + avatar + '\'' +
                ", state=" + state +
                ", time=" + time +
                '}';
    }
}

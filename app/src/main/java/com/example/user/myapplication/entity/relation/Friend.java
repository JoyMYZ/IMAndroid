package com.example.user.myapplication.entity.relation;

import cn.bmob.v3.BmobObject;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/21 17:00
 * Description:    Friend关系表
 **/

public class Friend extends BmobObject{
    //用户
    private String user;
    //好友
    private String friendUser;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getFriendUser() {
        return friendUser;
    }

    public void setFriendUser(String friendUser) {
        this.friendUser = friendUser;
    }
}

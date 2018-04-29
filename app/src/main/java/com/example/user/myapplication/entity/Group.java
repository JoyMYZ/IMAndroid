package com.example.user.myapplication.entity;

import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/25 23:05
 * Description:    TODO
 **/

public class Group extends BmobObject{
    private String groupName;
    private String avatar;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}

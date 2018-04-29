package com.example.user.myapplication.utils;

import com.example.user.myapplication.entity.User;

import cn.bmob.newim.BmobIM;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.utils
 * User:           ${User}
 * Date&Time:      2018/4/21 17:07
 * Description:    TODO
 **/

public class BmobUtil {
    public static User getCurrentUser(){
        return User.getUser(BmobIM.getInstance().getUserInfo(StaticClass.uid));
    }
}

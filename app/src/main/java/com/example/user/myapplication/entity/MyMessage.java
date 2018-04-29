package com.example.user.myapplication.entity;

import cn.bmob.newim.bean.BmobIMMessage;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/25 15:18
 * Description:    TODO
 **/

public class MyMessage extends BmobIMMessage {

    public static final String MSG_TYPE = "my";

    @Override
    public String getMsgType() {
        return MSG_TYPE;
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        return false;
    }
}

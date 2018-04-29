package com.example.user.myapplication.entity;

import cn.bmob.newim.bean.BmobIMExtraMessage;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity
 * User:           ${User}
 * Date&Time:      2018/4/22 11:42
 * Description:    TODO
 **/

public class AddFriendMessage extends BmobIMExtraMessage {

    public AddFriendMessage(){}

    @Override
    public String getMsgType() {
        //自定义一个`add`的消息类型
        return "add";
    }

    @Override
    public boolean isTransient() {
        //设置为true,表明为暂态消息，那么这条消息并不会保存到本地db中，SDK只负责发送出去
        return true;
    }
}

package com.example.user.myapplication.entity.relation;

import cn.bmob.v3.BmobObject;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.entity.relation
 * User:           ${User}
 * Date&Time:      2018/4/25 23:22
 * Description:    组的成员表
 **/

public class Member extends BmobObject {
    //既包含groupId和groupName是为了只需要一次查询就能够有完全可用的信息
    private String groupId;
    private String uid;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public String toString() {
        return "Member{" +
                "groupId='" + groupId + '\'' +
                ", uid='" + uid + '\'' +
                '}';
    }
}

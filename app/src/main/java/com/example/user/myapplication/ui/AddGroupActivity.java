package com.example.user.myapplication.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.Group;
import com.example.user.myapplication.entity.UpdateGroupMessage;
import com.example.user.myapplication.entity.relation.Member;
import com.example.user.myapplication.utils.StaticClass;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.ui
 * User:           ${User}
 * Date&Time:      2018/4/20 20:32
 * Description:    TODO
 **/

public class AddGroupActivity extends AppCompatActivity implements View.OnClickListener{
    private EditText mEditText;
    private Button create,cancel;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        initView();
    }

    private void initView() {
        mEditText=findViewById(R.id.et_group_name);
        create=findViewById(R.id.btn_create);
        cancel=findViewById(R.id.btn_cancel);

        create.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_create:
                String groupName=mEditText.getText().toString().trim();
                if(!groupName.isEmpty()) {
                    createGroup(groupName);
                }else{
                    Toast.makeText(this, "请填写要创建的组名", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_cancel:
                finish();
        }
    }

    private void createGroup(final String groupName) {
        //首先要判断组名是否被使用，没有才可以创建
        BmobQuery<Group> query=new BmobQuery<>();
        query.addWhereEqualTo("groupName",groupName);
        query.findObjects(new FindListener<Group>() {
            @Override
            public void done(List<Group> list, BmobException e) {
                if(e==null){
                    if(list.isEmpty()) {
                        //可以创建组
                        Group group = new Group();
                        group.setGroupName(groupName);
                        //TODO:设置群头像
                        group.setAvatar("");
                        group.save(new SaveListener<String>() {
                            @Override
                            public void done(String s, BmobException e) {
                                //将自己添加到Member表里
                                if(e==null){
                                    Member member=new Member();
                                    member.setGroupId(s);
                                    member.setUid(StaticClass.uid);
                                    member.save(new SaveListener<String>() {
                                        @Override
                                        public void done(String s, BmobException e) {
                                            if(e==null){
                                                Toast.makeText(AddGroupActivity.this, "创建组成功",
                                                        Toast.LENGTH_SHORT).show();
                                                EventBus.getDefault().post(new UpdateGroupMessage());
                                                finish();
                                            }else{
                                                Toast.makeText(AddGroupActivity.this, e.getErrorCode()+":"+e.getMessage()
                                                        , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(AddGroupActivity.this, e.getErrorCode()+":"+e.getMessage()
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }else{
                        Toast.makeText(AddGroupActivity.this, "该组名已被使用", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(AddGroupActivity.this, e.getErrorCode()+":"+e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

package com.example.user.myapplication.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication
 * User:           ${User}
 * Date&Time:      2018/4/7 15:51
 * Description:    TODO
 **/

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private ProgressBar progressBar;
    private EditText edit_username, edit_password;
    private Button btn_login, btn_regist;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
    }

    private void initView() {
        progressBar = findViewById(R.id.login_progress);
        edit_username = findViewById(R.id.username);
        edit_password = findViewById(R.id.password);
        btn_login = findViewById(R.id.login_button);
        btn_regist = findViewById(R.id.regist_button);

        btn_login.setOnClickListener(this);
        btn_regist.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_button:
                //登录逻辑
                String username = edit_username.getText().toString().trim();
                String password = edit_password.getText().toString().trim();
                if (username.equals("")) {
                    Toast.makeText(this, "请填写用户名", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    doLogin(username, password);
                }
                break;
            case R.id.regist_button:
                startActivity(new Intent(this, RegisterActivity.class));
                //不杀死应用，这样用户注册完毕可以回到该界面
                break;
        }
    }

    private void doLogin(final String username, final String password) {
        //首先检查用户是否存在
        //用户存在则比较密码是否相同
        //如果用户存在且密码正确，检查用户是否已经登陆
        //如果用户未登陆，则返回true
        BmobQuery<User> query = new BmobQuery<>();
        query.addWhereEqualTo("username", username).findObjects(new FindListener<User>() {
            @Override
            public void done(List<User> list, BmobException e) {
                if (null == e) {
                    if (list.isEmpty()) {
                        //没有查询到用户
                        Toast.makeText(LoginActivity.this, "未查询到用户信息",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        //查询到了用户
                        User user = list.get(0);
                        String real_pass = user.getPassword();
                        if (real_pass.equals(password)) {
                            //密码正确
                            if (user.isOnline()) {
                                Toast.makeText(LoginActivity.this, "用户已经登陆，" +
                                        "请勿重复登陆", Toast.LENGTH_SHORT).show();
                            } else {
                                //可以登录
                                Logger.info(StaticClass.LOGGER_TAG,"可以登录");
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                StaticClass.uid = user.getObjectId();
                                startActivity(intent);
                                //保存本地和云端的用户信息
                                User.setCurrentUser(user);
                                finish();
                                //updateUserState(user);
                            }
                        }else{
                            Toast.makeText(LoginActivity.this, "密码错误，请重新输入",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    e.printStackTrace();
                }
            }
        });
    }

/*  转移到MainActivity中执行
    private void updateUserState(User user) {
        //本地更新
        //将用户的objectId更新到本地数据库
        BmobIM.getInstance().updateUserInfo(new BmobIMUserInfo(user.getObjectId(), user.getUsername(), null));
        //远程更新
        user.setOnline(true);
        user.update(user.getObjectId(), new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (null == e) {
                    //远程更新成功
                }
            }
        });
    }*/
}

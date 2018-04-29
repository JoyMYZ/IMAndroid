package com.example.user.myapplication.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.SelectIconAdapter;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication
 * User:           ${User}
 * Date&Time:      2018/4/7 15:48
 * Description:    TODO
 **/

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    //保存用户选中的头像序号
    private int avatarIndex;
    //系统头像
    private List<Integer> mList;
    private SelectIconAdapter iconAdapter;

    private EditText edit_username, edit_password;
    private Button btn_regist;
    private CircleImageView civ_select;
    private AlertDialog dialog_select_avatar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initData();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    private void initData() {
        mList=new ArrayList<>();
        mList.add(R.mipmap.icon_my);
        mList.add(R.mipmap.icon_beard);
        mList.add(R.mipmap.icon_cartoon);
        mList.add(R.mipmap.icon_robot);

        iconAdapter=new SelectIconAdapter(this, mList);
    }

    private void initView() {
        edit_username = findViewById(R.id.username);
        edit_password = findViewById(R.id.password);
        btn_regist = findViewById(R.id.regist_button);
        civ_select=findViewById(R.id.civ_select);

        btn_regist.setOnClickListener(this);
        civ_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regist_button:
                //登录逻辑
                final String username = edit_username.getText().toString().trim();
                final String password = edit_password.getText().toString().trim();
                if (username.equals("")) {
                    Toast.makeText(this, "请填写用户名", Toast.LENGTH_SHORT).show();
                } else if (password.equals("")) {
                    Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
                } else {
                    //注册逻辑
                    BmobQuery<User> query = new BmobQuery<User>();
                    query.addWhereEqualTo("username", username);
                    query.findObjects(new FindListener<User>() {
                        @Override
                        public void done(List<User> list, BmobException e) {
                            if (e != null) {
                                Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                            } else {
                                if (list.isEmpty()) {
                                    //没有查询到用户信息,可以注册
                                    doRegist(username,password);
                                } else {
                                    //查询到了用户信息
                                    Toast.makeText(RegisterActivity.this, "用户名已被注册", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });
                }
                break;
            case R.id.civ_select:
                GridView gridView= (GridView) getLayoutInflater().inflate(R.layout.dialog_select_icon,null);
                gridView.setAdapter(iconAdapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        avatarIndex=position;
                        civ_select.setImageResource(mList.get(position));
                        civ_select.setBorderColor(Color.TRANSPARENT);
                        dialog_select_avatar.dismiss();
                    }
                });
                dialog_select_avatar=new AlertDialog.Builder(this)
                        .setTitle("选择头像")
                        .setView(gridView)
                        .setCancelable(true)
                        .setMessage("请选择合适的头像")
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .create();
                dialog_select_avatar.show();
        }
    }

    private void doRegist(String username, String password) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAvatar(String.valueOf(mList.get(avatarIndex)));
        user.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (null == e) {
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

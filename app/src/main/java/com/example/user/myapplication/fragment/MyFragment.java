package com.example.user.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.ui.FriendRequestActivity;
import com.example.user.myapplication.ui.LoginActivity;
import com.example.user.myapplication.ui.MainActivity;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.fragment
 * User:           ${User}
 * Date&Time:      2018/4/21 11:26
 * Description:    TODO
 **/

public class MyFragment extends Fragment implements View.OnClickListener{
    private TextView uid,username;
    private Button log_out,check_friend_request;
    private CircleImageView avatar;
    private User user;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my,null);
        initData();
        initView(view);
        return view;
    }

    private void initData() {
        //获取本地User状态
        user=User.getCurrentUser();
    }

    private void initView(View view) {
        uid=view.findViewById(R.id.tv_uid);
        uid.setText(user.getUid());
        username=view.findViewById(R.id.tv_username);
        username.setText(user.getUsername());
        avatar=view.findViewById(R.id.civ_avatar);
        avatar.setImageResource(Integer.parseInt(user.getAvatar()));

        check_friend_request=view.findViewById(R.id.btn_check_friend_request);
        check_friend_request.setOnClickListener(this);
        log_out=view.findViewById(R.id.btn_log_out);
        log_out.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_log_out:
                user.setOnline(false);
                user.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if(e==null){

                        }else{
                            Toast.makeText(getActivity(), e.getErrorCode()+" "+e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent1=new Intent(getActivity(), LoginActivity.class);
                startActivity(intent1);
                getActivity().finish();
                break;
            case R.id.btn_check_friend_request:
                Intent intent=new Intent(getActivity(), FriendRequestActivity.class);
                startActivity(intent);
        }
    }
}

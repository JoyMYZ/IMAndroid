package com.example.user.myapplication.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.ContactsAdapter;
import com.example.user.myapplication.entity.UpdateGroupMessage;
import com.example.user.myapplication.entity.relation.Friend;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.entity.relation.UpdateContacksMessage;
import com.example.user.myapplication.ui.ChatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.fragment
 * User:           ${User}
 * Date&Time:      2018/4/20 23:15
 * Description:    TODO
 **/

public class ContactsFragment extends Fragment {
    //关系集合
    private static List<Friend> friendList;
    //好友的用户信息集合
    private List<User> userList;
    private ContactsAdapter adapter;

    private TextView foreground;
    private RecyclerView mRecycleView;

    private Handler mHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        initData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    public void onNewFriend(UpdateContacksMessage message){
        queryContacks();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, null);
        initView(view);
        return view;
    }

    private void initData() {
        friendList = new ArrayList<>();
        userList = new ArrayList<>();

        //待所有的数据初始化完成后，才会进行Adapter的初始化工作
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        User user=User.parseMessage(msg);
                        userList.add(user);
                    case 1:
                        //数据完成消息
                        adapter = new ContactsAdapter(getActivity(), userList, new ContactsAdapter.onItemClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Intent intent = new Intent(getActivity(), ChatActivity.class);
                                User friend=userList.get(position);
                                Bundle bundle=new Bundle();
                                bundle.putString("uid",friend.getUid());
                                bundle.putString("username",friend.getUsername());
                                bundle.putString("avatar",friend.getAvatar());
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }
                        });
                        mRecycleView.setAdapter(adapter);
                        foreground.setVisibility(View.GONE);
                        mRecycleView.getAdapter().notifyDataSetChanged();
                        break;
                }
            }
        };
    }

    private void initView(View view) {
        foreground = view.findViewById(R.id.tv_foregroud);
        mRecycleView = view.findViewById(R.id.contacks_list);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity()) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        manager.setOrientation(LinearLayoutManager.VERTICAL);

        mRecycleView.setLayoutManager(manager);
        queryContacks();
    }

    //查询联系人列表
    public void queryContacks() {
        BmobQuery<Friend> query = new BmobQuery<>();
        User user = User.getCurrentUser();
        query.addWhereEqualTo("user", user.getObjectId());
        //findObjects方法应该是一个异步方法，在方法中修改的值不会立即检测到
        query.findObjects(new FindListener<Friend>() {
            @Override
            public void done(List<Friend> list, BmobException e) {
                if (null == e) {
                    if (list != null && !list.isEmpty()) {
                        friendList.clear();
                        friendList.addAll(list);
                        for(Friend friend:friendList){
                            //查询朋友的信息，返回给handler并由handler向friendList赋值
                            User.loadUser(friend.getFriendUser(),mHandler);
                        }
                        //当userList和friendList都初始化完成后
                        mHandler.sendEmptyMessage(1);
                    } else {
                        //没有查询到好友，
                        foreground.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getActivity(), e.getErrorCode() + ":" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

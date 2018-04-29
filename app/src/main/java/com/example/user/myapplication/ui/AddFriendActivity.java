package com.example.user.myapplication.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.AddFriendAdapter;
import com.example.user.myapplication.entity.AddFriendMessage;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.ui
 * User:           ${User}
 * Date&Time:      2018/4/20 20:31
 * Description:    TODO
 **/

public class AddFriendActivity extends AppCompatActivity{
    private List<User> user_list;
    //info为所要请求对象的信息
    private BmobIMUserInfo info;

    private SearchView search;
    private RecyclerView search_list;
    private AddFriendAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        initData();
        initView();
    }

    private void initData() {
        user_list=new ArrayList<>();
        adapter = new AddFriendAdapter(this, user_list);
        adapter.setOnItemClickListener(new AddFriendAdapter.OnItemClickListener() {
            @Override
            public void onClick(View v, int position) {
                //首先获取点击的用户信息
                User user=user_list.get(position);
                info=new BmobIMUserInfo(user.getObjectId(),user.getUsername(),user.getAvatar());
                //TODO:检测是否已经是好友的情况
                //给点击的用户发送信息
                sendAddFriendMessage();
            }
        });
    }

    private void initView() {
        search = findViewById(R.id.search);
        search_list = findViewById(R.id.search_list);

        search_list.setHasFixedSize(true);
        LinearLayoutManager manager=new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        search_list.setLayoutManager(manager);
        search_list.setAdapter(adapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //TODO:获取并初始化user_list

        search.onActionViewCollapsed();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //在服务器查询用户名相同的用户
                BmobQuery<User> userQuery=new BmobQuery<>();
                userQuery.addWhereEqualTo("username",query.trim());
                Logger.info(StaticClass.LOGGER_TAG,"username:"+query);
                userQuery.findObjects(new FindListener<User>(){
                    @Override
                    public void done(List<User> list, BmobException e) {
                        if (null == e) {
                            if (list != null) {
                                if (!list.isEmpty()) {
                                    Toast.makeText(AddFriendActivity.this, "共查询到"
                                            +list.size()+"条结果", Toast.LENGTH_SHORT).show();
                                    user_list.clear();
                                    user_list.addAll(list);
                                    //通知RecycleView重绘
                                    search_list.getAdapter().notifyDataSetChanged();
                                } else {
                                    Toast.makeText(AddFriendActivity.this, "未查询到用户", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Logger.info(StaticClass.LOGGER_TAG, "AddFriendActivity:" + "无法获取" +
                                        "查询列表");
                            }
                        }else{
                            Toast.makeText(AddFriendActivity.this, e.getErrorCode()
                                    +":"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //当搜索框内容改变时
                return false;
            }
        });
    }

    private void sendAddFriendMessage() {
        if(info!=null) {
            //创建一个暂态会话入口，发送好友请求
            BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
            //根据会话入口获取消息管理，发送好友请求
            BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

            AddFriendMessage msg = new AddFriendMessage();

            msg.setContent("很高兴认识你，可以加个好友吗?");
            messageManager.sendMessage(msg, new MessageSendListener() {
                @Override
                public void done(BmobIMMessage msg, BmobException e) {
                    if (e == null) {
                        //发送成功
                        Toast.makeText(AddFriendActivity.this, "好友请求发送成功，等待验证", Toast.LENGTH_SHORT).show();
                    } else {
                        //发送失败
                        Toast.makeText(AddFriendActivity.this, "发送失败:", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            Toast.makeText(this, "暂时无法添加用户", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }
}

package com.example.user.myapplication.ui;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.ChatAdapter;
import com.example.user.myapplication.entity.MyMessage;
import com.example.user.myapplication.entity.OthersMessage;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.items.SpaceItemDecoration;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMConversationType;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMTextMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.newim.listener.MessagesQueryListener;
import cn.bmob.v3.exception.BmobException;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.ui
 * User:           ${User}
 * Date&Time:      2018/4/7 16:47
 * Description:    TODO
 **/

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
    private BmobIMUserInfo info;
    //消息管理者
    private BmobIMConversation mMessageManager;
    //消息集合，包括MyMessage和OtherMessage
    private List<BmobIMMessage> mList;
    private ChatAdapter adapter;
    private MessageListHandler handler;
    private LinearLayoutManager layoutManager;

    private EditText mEditText;
    private Button mButton;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        initData();
        initView();
        queryMessage(null,20);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(handler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(handler);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        User user = new User(bundle.getString("uid"), bundle.getString("username"),
                bundle.getString("avatar"));
        info = new BmobIMUserInfo(user.getUid(), user.getUsername(), user.getAvatar());
        mList=new ArrayList<>();

        adapter=new ChatAdapter(this,mList,info);

        //TODO 会话：4.1、创建一个常态会话入口，好友聊天，陌生人聊天
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info,null);
        mMessageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);

        handler=new MessageListHandler() {
            @Override
            public void onMessageReceive(List<MessageEvent> list) {
                if(list!=null){
                    if(!list.isEmpty()){
                        for(MessageEvent event:list){
                            BmobIMMessage message=new BmobIMMessage();
                            message.setMsgType("others");
                            message.setContent(event.getMessage().getContent());
                            message.setBmobIMUserInfo(event.getFromUserInfo());
//                            Logger.info(StaticClass.LOGGER_TAG,event.getMessage().getFromId());
                            mList.add(message);
                            adapter.notifyDataSetChanged();
                            mRecyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                        }
                    }
                }
            }
        };
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }

    private void initView() {
        //设置一个返回按钮
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.rcv_chatList);
        layoutManager=new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        mRecyclerView.setAdapter(adapter);

        mEditText = findViewById(R.id.et_message);
        mButton = findViewById(R.id.btn_send);

        mButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_send:
                sendMessage();
                //清空EditText
                mEditText.setText("");
                break;
        }
    }

    private void sendMessage() {
        String text = mEditText.getText().toString();
        //TODO 发送消息：6.1、发送文本消息
        final BmobIMTextMessage msg = new BmobIMTextMessage();
        msg.setFromId(StaticClass.uid);
        msg.setContent(text);
        //可随意设置额外信息
        mMessageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if(e==null) {
                    //本地消息
                    BmobIMMessage message=new BmobIMMessage();
                    message.setMsgType("my");
                    message.setContent(bmobIMMessage.getContent());
                    mList.add(message);
//                    Logger.info(StaticClass.LOGGER_TAG,"将加入的message："+message.toString());
                    adapter.notifyDataSetChanged();
                    //页面滑动到最底部
                    mRecyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                }else{
                    Toast.makeText(ChatActivity.this, "消息无法发送", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void queryMessage(BmobIMMessage msg,int limit) {
        //首次加载，可设置msg为null，下拉刷新的时候，可用消息表的第一个msg作为刷新的起始时间点，默认按照消息时间的降序排列
        //TODO 消息：5.2、查询指定会话的消息记录
        mMessageManager.queryMessages(msg, limit, new MessagesQueryListener() {
            @Override
            public void done(List<BmobIMMessage> list, BmobException e) {
                if (e == null) {
                    if (null != list && list.size() > 0) {
                        for(BmobIMMessage message:list){
                            BmobIMMessage temp_message=new BmobIMMessage();
                            temp_message.setContent(message.getContent());
                            temp_message.setMsgType(
                                    message.getFromId().equals(StaticClass.uid)?
                                            "my":"others"
                            );
                            mList.add(temp_message);
                        }
                        adapter.notifyDataSetChanged();
                        mRecyclerView.smoothScrollToPosition(adapter.getItemCount()-1);
                    }
                } else {
                    Toast.makeText(ChatActivity.this, (e.getMessage() + "(" + e.getErrorCode() + ")"),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}


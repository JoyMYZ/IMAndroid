package com.example.user.myapplication.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.FriendRequestAdapter;
import com.example.user.myapplication.entity.AgreeAddFriendMessage;
import com.example.user.myapplication.entity.NewFriendRequest;
import com.example.user.myapplication.entity.RefuseAddFriendMessage;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.entity.relation.Friend;
import com.example.user.myapplication.entity.relation.UpdateContacksMessage;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.core.BmobIMClient;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.newim.listener.MessageSendListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.ui
 * User:           ${User}
 * Date&Time:      2018/4/22 12:16
 * Description:    管理好友请求的界面,可以同意/拒绝好友请求，
 * 还可以接收同意/拒绝好友的消息
 **/

public class FriendRequestActivity extends AppCompatActivity {
    private List<NewFriendRequest> request_list;

    private FriendRequestAdapter adapter;
    private FriendRequestAdapter.OnItemClickListener listener;
    //接受的消息处理Handler
    private MessageListHandler messageHanlder;

    private RecyclerView request_view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);
        initData();
        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(messageHanlder);
    }

    @Override
    protected void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(messageHanlder);
    }

    private void initView() {
        request_view = findViewById(R.id.recycle_friend_request);
        request_view.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this) {
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        request_view.setLayoutManager(manager);
    }

    private void initData() {
        request_list = new ArrayList<>();

        listener = new FriendRequestAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, final int position) {
                switch (view.getId()) {
                    case R.id.btn_accept:
                        //同意按钮被点击
                        sendAgreeAddFriendMessage(request_list.get(position));
                        //更新本地视图
                        request_list.get(position).setStatus(NewFriendRequest.State.ACCEPTED);
                        request_view.getAdapter().notifyDataSetChanged();
                        EventBus.getDefault().post(new UpdateContacksMessage());
                        //更新云端信息
                        new Thread() {
                            @Override
                            public void run() {
                                saveFriendToWeb(request_list.get(position));
                            }
                        }.start();
                        break;
                    case R.id.btn_refuse:
                        //拒绝按钮被点击
                        sendRefuseAddFriendMessage(request_list.get(position));
                        //更新本地视图
                        request_list.get(position).setStatus(NewFriendRequest.State.REFUSED);
                        request_view.getAdapter().notifyDataSetChanged();
                        break;
                }
            }
        };

        adapter = new FriendRequestAdapter(this, request_list, listener);

        messageHanlder = new MessageListHandler() {
            @Override
            public void onMessageReceive(List<MessageEvent> list) {
                /*
                当接受到新的消息时：
                * 1.将消息添加到消息列表中
                * 2.通知view重绘
                * 3.改变消息的消息类型为未读
                * */
                for (MessageEvent event : list) {
                    switch (event.getMessage().getMsgType()) {
                        case "add":
                            Toast.makeText(FriendRequestActivity.this, "接收到新的好友消息", Toast.LENGTH_SHORT).show();
                            //对接受的"add"消息的处理
                            processAddMessage(event.getMessage(), event.getFromUserInfo());
                            break;
                        case "agree":
                            //对接受的"agree"消息的处理
                            processAgreeMessage(event.getMessage(), event.getFromUserInfo());
                            break;
                        case "refuse":
                            //对接受的"refuse"消息的处理
                            break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    private void saveFriendToWeb(NewFriendRequest request) {
        Friend friend = new Friend();
        Friend friend2 = new Friend();
        friend.setUser(StaticClass.uid);
        friend.setFriendUser(request.getUid());
        friend2.setUser(request.getUid());
        friend2.setFriendUser(StaticClass.uid);

        friend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
                    Logger.error(StaticClass.LOGGER_TAG, "FriendRequestActivity.saveFriendToWeb():" +
                            e.getErrorCode() + e.getMessage());
                }
            }
        });

        friend2.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
                    Logger.error(StaticClass.LOGGER_TAG, "FriendRequestActivity.saveFriendToWeb():" +
                            e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    //发送一个"agree"消息
    private void sendAgreeAddFriendMessage(final NewFriendRequest add) {
        //对方的信息
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //创建一个暂态会话入口，发送同意好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //根据会话入口获取消息管理，发送同意好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        //而AgreeAddFriendMessage的isTransient设置为false，表明我希望在对方的会话数据库中保存该类型的消息
        AgreeAddFriendMessage msg = new AgreeAddFriendMessage();
        User currentUser = User.getCurrentUser();
        msg.setContent("我通过了你的好友验证请求，我们可以开始聊天了!");//这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("msg", currentUser.getUsername() + "同意添加你为好友");//显示在通知栏上面的内容
        map.put("uid", add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage msg, BmobException e) {
                if (e == null) {//发送成功

                } else {//发送失败
                    Logger.error(StaticClass.LOGGER_TAG, "FriendRequestActivity.sendAgreeAddFriendMessage():" +
                            e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    //发送一个"refuse"消息
    private void sendRefuseAddFriendMessage(final NewFriendRequest add) {
        //对方的信息
        BmobIMUserInfo info = new BmobIMUserInfo(add.getUid(), add.getName(), add.getAvatar());
        //创建一个暂态会话入口，发送同意好友请求
        BmobIMConversation conversationEntrance = BmobIM.getInstance().startPrivateConversation(info, true, null);
        //根据会话入口获取消息管理，发送同意好友请求
        BmobIMConversation messageManager = BmobIMConversation.obtain(BmobIMClient.getInstance(), conversationEntrance);
        RefuseAddFriendMessage msg = new RefuseAddFriendMessage();
        User currentUser = User.getCurrentUser();
        msg.setContent(currentUser.getUsername() + "拒绝了你的请求");//这句话是直接存储到对方的消息表中的
        Map<String, Object> map = new HashMap<>();
        map.put("msg", currentUser.getUsername() + "拒绝添加你为好友");//显示在通知栏上面的内容
        map.put("uid", add.getUid());//发送者的uid-方便请求添加的发送方找到该条添加好友的请求
        map.put("time", add.getTime());//添加好友的请求时间
        msg.setExtraMap(map);
        messageManager.sendMessage(msg, new MessageSendListener() {
            @Override
            public void done(BmobIMMessage bmobIMMessage, BmobException e) {
                if (e == null) {
                    //发送成功
                } else {
                    Logger.error(StaticClass.LOGGER_TAG, "FriendRequestActivity.sendRefuseAddFriendMessage():" +
                            e.getErrorCode() + e.getMessage());
                }
            }
        });
    }

    private void processAddMessage(BmobIMMessage message, BmobIMUserInfo info) {
        //TODO:获得消息内容(AddFriendMessage)
        //获得消息发送者信息
        NewFriendRequest request = new NewFriendRequest();
        request.setUid(info.getUserId());
        request.setName(info.getName());
        request.setAvatar(info.getAvatar());
        //获得留言消息
        request.setMsg(message.getContent());
        //设置消息类型为未读
        request.setStatus(NewFriendRequest.State.UNREAD);
        request.setTime(System.currentTimeMillis());
        //将消息添加进处理列表，同时通知RecycleView重绘
        Logger.info(StaticClass.LOGGER_TAG,request.toString());
        request_list.add(request);
        request_view.getAdapter().notifyDataSetChanged();
    }

    //info:User's infomation where the message come from
    private void processAgreeMessage(BmobIMMessage message, BmobIMUserInfo info) {
        //当前用户的好友请求被同意时，用户接受到的响应信息
        /*
        * 1.更新自己的好友页面(如果使用本地数据库的话)
        * 2.将"对方-我"的好友信息更新到云端
        * */

        //2:将"对方-我"好友信息更新到云端
        Friend friend = new Friend();
        friend.setUser(StaticClass.uid);
        friend.setFriendUser(info.getUserId());
        friend.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {

                } else {
                    Logger.error(StaticClass.LOGGER_TAG, "FriendRequestActivity.processAgreeMessage():" +
                            e.getErrorCode() + ":" + e.getMessage());
                }
            }
        });
    }
}
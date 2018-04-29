package com.example.user.myapplication.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.GroupChatAdapter;
import com.example.user.myapplication.entity.GroupMessage;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.utils.StaticClass;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.ui
 * User:           ${User}
 * Date&Time:      2018/4/28 10:57
 * Description:    TODO
 **/
public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {
    private List<GroupMessage> messageList;
    private String groupName;
    private String groupId;
    private GroupChatAdapter adapter;

    private Date lastUpdateDate;
    private Runnable updateMessageTask;
    private ScheduledExecutorService ses=Executors.newSingleThreadScheduledExecutor();

    private RecyclerView mRecycle;
    private EditText mEditText;
    private Button mButton;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_group_chat);
        initData();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ses.shutdown();
    }

    private void initData() {
//        intent.putExtra("groupName",group.getGroupName());
//        intent.putExtra("groupId",group.getObjectId());
        groupName=getIntent().getStringExtra("groupName");
        groupId=getIntent().getStringExtra("groupId");
        messageList=new ArrayList<>();
        adapter=new GroupChatAdapter(this,messageList);

        lastUpdateDate=new Date();
        updateMessageTask=new Runnable() {
            @Override
            public void run() {
                BmobQuery<GroupMessage> query=new BmobQuery<>();
                query.addWhereNotEqualTo("uid",StaticClass.uid)
                        .addWhereGreaterThanOrEqualTo("createdAt",new BmobDate(lastUpdateDate))
                        .setLimit(10);

                query.findObjects(new FindListener<GroupMessage>() {
                    @Override
                    public void done(List<GroupMessage> list, BmobException e) {
                        if(e==null) {
                            if(list!=null&&!list.isEmpty()) {
                                messageList.addAll(list);
                                lastUpdateDate.setTime(System.currentTimeMillis());
                                adapter.notifyDataSetChanged();
                                mRecycle.smoothScrollToPosition(adapter.getItemCount()-1);
                            }else{
                            }
                        }else{
                            Toast.makeText(GroupChatActivity.this, e.getErrorCode()+":"+e.getMessage()
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        };
        ses.scheduleWithFixedDelay(updateMessageTask,2,1, TimeUnit.SECONDS);
    }


    private void initView() {
        mRecycle=findViewById(R.id.rcv_chatList);
        mRecycle.setHasFixedSize(true);
        LinearLayoutManager laytoutManager=new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        laytoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycle.setLayoutManager(laytoutManager);
        mRecycle.setAdapter(adapter);

        mEditText=findViewById(R.id.et_message);

        mButton=findViewById(R.id.btn_send);
        mButton.setOnClickListener(this);
        loadHistoryMessage(20);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_send:
                String content=mEditText.getText().toString();
                mEditText.setText("");
                if(!content.isEmpty()) {
                    User user = User.getCurrentUser();
                    GroupMessage message = new GroupMessage();
                    message.setUid(user.getUid());
                    message.setAvatar(user.getAvatar());
                    message.setContent(content);
                    message.setGroupId(groupId);
                    saveMessage(message);
                }
        }
    }

    private void saveMessage(GroupMessage message) {
        //保存到本地
        messageList.add(message);
        adapter.notifyDataSetChanged();
        mRecycle.smoothScrollToPosition(adapter.getItemCount()-1);
        //保存到云端
        message.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e==null) {
                    //保存成功
                }else{
                    Toast.makeText(GroupChatActivity.this, e.getErrorCode()+":"+e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadHistoryMessage(int limit){
        BmobQuery<GroupMessage> query=new BmobQuery<>();
        query.addWhereEqualTo("groupId",groupId)
                .setLimit(limit);
        query.findObjects(new FindListener<GroupMessage>() {
            @Override
            public void done(List<GroupMessage> list, BmobException e) {
                if(e==null) {
                    messageList.addAll(list);
                    adapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(GroupChatActivity.this, "读取历史消息失败:"+e.getErrorCode()+e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

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
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.GroupAdapter;
import com.example.user.myapplication.entity.Group;
import com.example.user.myapplication.entity.UpdateGroupMessage;
import com.example.user.myapplication.entity.relation.Member;
import com.example.user.myapplication.ui.GroupChatActivity;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.fragment
 * User:           ${User}
 * Date&Time:      2018/4/20 23:16
 * Description:    TODO
 **/

public class GroupFragment extends Fragment {
    private List<Member> mList;
    //传递给Adapter的list
    private List<Group> groupList;
    //主Handler
    private MyHandler mHandler;
    //与其他用户交互的Handler
    private MessageListHandler mMessageListHandler;
    private GroupAdapter mGroupAdapter;

    private RecyclerView mRecycleView;

    class MyHandler extends Handler{
        //初始化mList返回
        public static final int MSG_FINISH_QUERYMEMBER=101;
        public static final int MSG_FINISH_QUERYGROUP=102;

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_FINISH_QUERYMEMBER:
                    queryGroup();
                    break;
                case MSG_FINISH_QUERYGROUP:
                    Logger.info(StaticClass.LOGGER_TAG,mList.toString());
                    Logger.info(StaticClass.LOGGER_TAG,groupList.toString());
                    mGroupAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_group,null);
        initData();
        initView(view);
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onFriendAdded(UpdateGroupMessage message){
        //更新组界面
        Toast.makeText(getActivity(), "更新group", Toast.LENGTH_SHORT).show();
        queryMembership();
    }

    private void initData() {
        mList=new ArrayList<>();
        groupList=new ArrayList<>();
        mHandler=new MyHandler();
        mGroupAdapter=new GroupAdapter(groupList, getActivity(), new GroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Group group=groupList.get(position);
                Intent intent=new Intent(getActivity(), GroupChatActivity.class);
                intent.putExtra("groupName",group.getGroupName());
                intent.putExtra("groupId",group.getObjectId());
                startActivity(intent);
            }
        });
    }


    private void initView(View view) {
        mRecycleView=view.findViewById(R.id.rcv_groupList);
        mRecycleView.setHasFixedSize(true);
        LinearLayoutManager layoutManager=new LinearLayoutManager(getActivity()){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(mGroupAdapter);
        queryMembership();
    }

    private void queryMembership() {
        BmobQuery<Member> query=new BmobQuery<>();
        query.addWhereEqualTo("uid", StaticClass.uid);
        query.findObjects(new FindListener<Member>() {
            @Override
            public void done(List<Member> list, BmobException e) {
                if(e==null) {
                    if (list != null && !list.isEmpty()){
                        mList.clear();
                        mList.addAll(list);
                        mHandler.sendEmptyMessage(MyHandler.MSG_FINISH_QUERYMEMBER);
                    }else{

                    }
                }else{
                    Toast.makeText(getActivity(), e.getErrorCode()+":"+e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //查询到所属的组号后，接着查询组信息
    private void queryGroup() {
        groupList.clear();
        for(Member member:mList) {
            Logger.info(StaticClass.LOGGER_TAG,member.toString());
            BmobQuery<Group> query=new BmobQuery<>();
            query.getObject(member.getGroupId(), new QueryListener<Group>() {
                @Override
                public void done(Group group, BmobException e) {
                    if(e==null) {
                        if (group != null) {
                            groupList.add(group);
                            Logger.info(StaticClass.LOGGER_TAG,group.getGroupName());
                            mHandler.sendEmptyMessage(MyHandler.MSG_FINISH_QUERYGROUP);
                        }
                    }else{
                        Toast.makeText(getActivity(), "获取组信息失败",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

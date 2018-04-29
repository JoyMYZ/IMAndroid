package com.example.user.myapplication.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.adapter.JoinGroupAdapter;
import com.example.user.myapplication.entity.Group;
import com.example.user.myapplication.entity.UpdateGroupMessage;
import com.example.user.myapplication.entity.relation.Member;
import com.example.user.myapplication.utils.StaticClass;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

/**
 * Project_name:   IMQQ
 * Package_name:   com.example.user.myapplication.ui
 * User:           ${User}
 * Date&Time:      2018/4/26 17:36
 * Description:    加入组
 **/

public class JoinGroupActivity extends AppCompatActivity{
    private List<Group> mList;
    private JoinGroupAdapter adapter;

    private SearchView mSearchView;
    private RecyclerView mRecycleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
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
            default:
        }
        return true;
    }

    private void initData() {
        mList=new ArrayList<>();
        adapter=new JoinGroupAdapter(this, mList, new JoinGroupAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final View view, int position) {
                Group group=mList.get(position);
                Member member=new Member();
                member.setUid(StaticClass.uid);
                member.setGroupId(group.getObjectId());
                member.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        if(e==null){
                            ((Button)view).setBackgroundColor(Color.GRAY);
                            ((Button) view).setText("已添加");
                            EventBus.getDefault().post(new UpdateGroupMessage());
                        }else{
                            Toast.makeText(JoinGroupActivity.this, "加入失败，请稍后重试"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        mSearchView=findViewById(R.id.sv_search_group);
        mRecycleView=findViewById(R.id.rcv_groupList);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this){
            @Override
            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycleView.setLayoutManager(layoutManager);
        mRecycleView.setAdapter(adapter);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String queryString=query.trim();
                if(!TextUtils.isEmpty(queryString)) {
                    queryGroups(queryString);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void queryGroups(String groupName) {
        BmobQuery<Group> query=new BmobQuery<>();
        query.addWhereEqualTo("groupName",groupName);
        query.findObjects(new FindListener<Group>() {
            @Override
            public void done(List<Group> list, BmobException e) {
                if(e==null){
                    if(list!=null&&!list.isEmpty()){
                        Toast.makeText(JoinGroupActivity.this, "查询到了"+list.size()+"条结果"
                                , Toast.LENGTH_SHORT).show();
                        mList.clear();
                        mList.addAll(list);
                        adapter.notifyDataSetChanged();
                    }
                }else{
                    Toast.makeText(JoinGroupActivity.this, e.getErrorCode()+":"+e.getMessage()
                            , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

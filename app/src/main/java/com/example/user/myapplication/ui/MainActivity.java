package com.example.user.myapplication.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.myapplication.R;
import com.example.user.myapplication.entity.User;
import com.example.user.myapplication.fragment.ContactsFragment;
import com.example.user.myapplication.fragment.GroupFragment;
import com.example.user.myapplication.fragment.MyFragment;
import com.example.user.myapplication.utils.Logger;
import com.example.user.myapplication.utils.StaticClass;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMUserInfo;
import cn.bmob.newim.listener.ConnectListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

//应用主界面
public class MainActivity extends AppCompatActivity {
    private User user;
    private List<String> mTitles;
    private List<Integer> mIcons;
    private List<Fragment> mFragments;
    private MyFragmentPagerAdapter mAdapter;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;

    class MyFragmentPagerAdapter extends FragmentPagerAdapter{
        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles.get(position);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }


        public View getView(int position){
            View view=getLayoutInflater().inflate(R.layout.item_adapter_tabview,null);
            ((TextView)view.findViewById(R.id.tv_title)).setText(mTitles.get(position));
            ((ImageView)view.findViewById(R.id.iv_icon)).setImageResource(mIcons.get(position));
            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        connectToServer(user);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BmobIM.getInstance().disConnect();
        //更新用户登陆状态
        User user = User.getCurrentUser();
        user.setOnline(false);
        user.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {

            }
        });
    }

    private void initData() {
        //获取并保存用户信息
        user = User.getCurrentUser();

        Resources resources = getResources();
        mTitles = new ArrayList<>();
        mTitles.add(resources.getString(R.string.contacts));
        mTitles.add(resources.getString(R.string.group));
        mTitles.add(resources.getString(R.string.my));

        mIcons=new ArrayList<>();
        mIcons.add(R.drawable.ic_chat_black_16dp);
        mIcons.add(R.drawable.ic_create_group_black_16dp);
        mIcons.add(R.drawable.ic_myinfo_black_16dp);

        mFragments = new ArrayList<>();
        mFragments.add(new ContactsFragment());
        mFragments.add(new GroupFragment());
        mFragments.add(new MyFragment());

        mAdapter=new MyFragmentPagerAdapter(getSupportFragmentManager());
    }

    private void initView() {

        mViewPager = findViewById(R.id.viewPager);
        mTabLayout = findViewById(R.id.tabs);


        mViewPager.setOffscreenPageLimit(mFragments.size());
        mViewPager.setAdapter(mAdapter);

        mViewPager.setCurrentItem(0);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                //TODO:高亮显示当前的fragment
                getSupportActionBar().setTitle(mTitles.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mTabLayout.setupWithViewPager(mViewPager);
        //给Tab添加图标
        for (int i = 0; i < mTabLayout.getTabCount(); i++) {
            TabLayout.Tab tab=mTabLayout.getTabAt(i);
            tab.setCustomView(mAdapter.getView(i));
        }

        mTabLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void connectToServer(final User user) {
        //TODO 连接：3.1、登录成功、注册成功或处于登录状态重新打开应用后执行连接IM服务器的操作
        if (!TextUtils.isEmpty(user.getObjectId())) {
            BmobIM.connect(user.getObjectId(), new ConnectListener() {
                @Override
                public void done(String uid, BmobException e) {
                    if (e == null) {
                        //连接成功
                        //更新用户信息
                        saveUserInfo(user);
                    } else {
                        //连接失败
                        Toast.makeText(MainActivity.this, e.getErrorCode() + " " + e.getMessage()
                                , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void saveUserInfo(User user) {
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
        Logger.info(StaticClass.LOGGER_TAG, "更新成功");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "添加好友").setIcon(R.drawable.ic_person_add_black_16dp);
        menu.add(0, 2, 0, "创建组").setIcon(R.drawable.ic_create_group_black_16dp);
        menu.add(0, 3, 0, "加入组").setIcon(R.drawable.ic_group_add_black_16dp);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                startActivity(intent);
                break;
            case 2:
                Intent intent2 = new Intent(MainActivity.this, AddGroupActivity.class);
                startActivity(intent2);
                break;
            case 3:
                Intent intent3 = new Intent(MainActivity.this, JoinGroupActivity.class);
                startActivity(intent3);
                break;
        }
        return true;
    }
}

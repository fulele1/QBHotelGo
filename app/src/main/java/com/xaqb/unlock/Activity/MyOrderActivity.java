package com.xaqb.unlock.Activity;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xaqb.unlock.Adapter.OrderFragmentAdapter;
import com.xaqb.unlock.Fragment.AllFragment;
import com.xaqb.unlock.Fragment.GotFragment;
import com.xaqb.unlock.Fragment.PayFragment;
import com.xaqb.unlock.Fragment.WaitFragment;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Views.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends FragmentActivity implements View.OnClickListener {

    private NoScrollViewPager mVpg;
    private List<Fragment> mFrags;
    private FragmentManager mFragmentManager;
    private RadioGroup mRgp;
    private RadioButton mRbWait;
    private RadioButton mRbPay;
    private RadioButton mRbGot;
    private RadioButton mRbAll;
    private TextView mTvBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // 透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // 透明导航栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
        setContentView(R.layout.activity_my_order);
        initView();
        initData();
        initEvent();
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        mVpg.setAdapter(new OrderFragmentAdapter(mFragmentManager, mFrags));
        mRgp.setOnCheckedChangeListener(new CheckedChange());
        mVpg.setOnPageChangeListener(new pageChange());
        mTvBack.setOnClickListener(this);
    }


    /**
     * 初始化数据
     */
    private void initData() {
        mFrags = new ArrayList<>();
        mFrags.add(new AllFragment());
        mFrags.add(new WaitFragment());
        mFrags.add(new PayFragment());
        mFrags.add(new GotFragment());
    }


    /**
     * 初始化view
     */
    private void initView() {
        mFragmentManager = this.getSupportFragmentManager();
        mTvBack = (TextView) findViewById(R.id.tv_back_order);
        mVpg = (NoScrollViewPager) findViewById(R.id.vpg_order);
        mRgp = (RadioGroup) findViewById(R.id.rp_order);
        mRbWait = (RadioButton) findViewById(R.id.rb_wait_m_order);
        mRbPay = (RadioButton) findViewById(R.id.rb_pay_m_order);
        mRbGot = (RadioButton) findViewById(R.id.rb_got_m_order);
        mRbAll = (RadioButton) findViewById(R.id.rb_all_m_order);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_back_order://返回键
                MyOrderActivity.this.finish();
                break;
        }
    }


    /**
     * 页面滑动后设置当前为点击
     */
    class pageChange implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            int mainColor = getResources().getColor(R.color.main);
            int redioTextColor = getResources().getColor(R.color.redio_text);
            switch (position) {
                case 0:
                    mRbAll.setChecked(true);
                    mRbAll.setTextColor(mainColor);
                    mRbPay.setTextColor(redioTextColor);
                    mRbGot.setTextColor(redioTextColor);
                    mRbWait.setTextColor(redioTextColor);
                    break;
                case 1:
                    mRbWait.setChecked(true);
                    mRbWait.setTextColor(mainColor);
                    mRbPay.setTextColor(redioTextColor);
                    mRbGot.setTextColor(redioTextColor);
                    mRbAll.setTextColor(redioTextColor);
                    break;
                case 2:
                    mRbPay.setChecked(true);
                    mRbPay.setTextColor(mainColor);
                    mRbGot.setTextColor(redioTextColor);
                    mRbAll.setTextColor(redioTextColor);
                    mRbWait.setTextColor(redioTextColor);
                    break;
                case 3:
                    mRbGot.setChecked(true);
                    mRbGot.setTextColor(mainColor);
                    mRbWait.setTextColor(redioTextColor);
                    mRbAll.setTextColor(redioTextColor);
                    mRbPay.setTextColor(redioTextColor);
                    break;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    }

    /**
     * 点击改变页面的监听事件
     */
    class CheckedChange implements RadioGroup.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.rb_all_m_order://全部
                    mVpg.setCurrentItem(0);
                    break;
                case R.id.rb_wait_m_order://待收货
                    mVpg.setCurrentItem(1);
                    break;
                case R.id.rb_pay_m_order://已付款
                    mVpg.setCurrentItem(2);
                    break;
                case R.id.rb_got_m_order://已收货
                    mVpg.setCurrentItem(3);
                    break;
            }
        }
    }

}

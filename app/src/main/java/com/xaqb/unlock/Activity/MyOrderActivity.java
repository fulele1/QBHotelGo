package com.xaqb.unlock.Activity;


import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.xaqb.unlock.Adapter.OrderFragmentAdapter;
import com.xaqb.unlock.Fragment.AllFragment;
import com.xaqb.unlock.Fragment.GotFragment;
import com.xaqb.unlock.Fragment.PayFragment;
import com.xaqb.unlock.Fragment.WaitFragment;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.StatuBarUtil;
import com.xaqb.unlock.Views.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

public class MyOrderActivity extends FragmentActivity implements View.OnClickListener {

    private NoScrollViewPager mVpg;
    private List<Fragment> mFrags;
    private FragmentManager mFragmentManager;
    private RadioGroup mRgp;
    private RadioButton mRbWait,mRbPay,mRbGot,mRbAll;
    private TextView tvTitle;
    private ImageView ivBack;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order);
        StatuBarUtil.setStatusBarColor(this,getResources().getColor(R.color.main));

        initView();
        initData();
        initEvent();
        tvTitle.setText("我的订单");
    }


    /**
     * 初始化事件
     */
    private void initEvent() {
        mVpg.setAdapter(new OrderFragmentAdapter(mFragmentManager, mFrags));
        mRgp.setOnCheckedChangeListener(new CheckedChange());
        mVpg.setOnPageChangeListener(new pageChange());
    }


    /**
     * 初始化数据
     */
    private void initData() {
        mFrags = new ArrayList<>();
        mFrags.add(new AllFragment());//全部订单
        mFrags.add(new WaitFragment());//待付款
        mFrags.add(new PayFragment());//未付清
        mFrags.add(new GotFragment());//已完成
    }


    /**
     * 初始化view
     */
    private void initView() {
        mFragmentManager = this.getSupportFragmentManager();
        mVpg = (NoScrollViewPager) findViewById(R.id.vpg_order);
        mRgp = (RadioGroup) findViewById(R.id.rp_order);
        mRbWait = (RadioButton) findViewById(R.id.rb_wait_m_order);
        mRbPay = (RadioButton) findViewById(R.id.rb_pay_m_order);
        mRbGot = (RadioButton) findViewById(R.id.rb_got_m_order);
        mRbAll = (RadioButton) findViewById(R.id.rb_all_m_order);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        ivBack = (ImageView) findViewById(R.id.iv_backward);

    }


    @Override
    public void onClick(View v) {
    }


    /**
     * 返回按钮点击后触发
     *
     * @param backwardView
     */
    public void onBackward(View backwardView) {
//        Toast.makeText(this, "点击返回，可在此处调用finish()", Toast.LENGTH_LONG).show();
        finish();
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

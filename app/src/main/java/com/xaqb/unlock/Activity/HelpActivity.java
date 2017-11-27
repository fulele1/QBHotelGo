package com.xaqb.unlock.Activity;

import android.view.View;

import com.xaqb.unlock.Adapter.ViewPagerCompatAdapter;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Views.ViewPagerCompat.DepthPageTransformer;
import com.xaqb.unlock.Views.ViewPagerCompat.ViewPagerCompat;


/**
 * 使用帮助页面
 */
public class HelpActivity extends BaseActivity {
    private HelpActivity instance;
    private ViewPagerCompat viewPager;

    @Override
    public void initTitleBar() {
        setTitle("使用帮助");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.help_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        viewPager = (ViewPagerCompat) findViewById(R.id.vp_main);
        viewPager.setPageTransformer(true, new DepthPageTransformer());
        viewPager.setOffscreenPageLimit(10);
        viewPager.setAdapter(new ViewPagerCompatAdapter(null, instance));
    }

    @Override
    public void initData() {

    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}

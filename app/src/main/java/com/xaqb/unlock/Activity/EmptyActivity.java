package com.xaqb.unlock.Activity;

import android.view.View;

import com.xaqb.unlock.R;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class EmptyActivity extends BaseActivity {
    private EmptyActivity instance;

    @Override
    public void initTitleBar() {
        setTitle("");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.empty_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {

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

package com.xaqb.unlock.Activity;

import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.LogUtils;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class AboutActivity extends BaseActivityNew {
    private AboutActivity instance;
    private TextView tvVersion;
    private TextView tvTitle;

    @Override
    public void initViews() {
        StatusBarUtil.setTranslucent(this,0);
        setContentView(R.layout.about_activity);
        instance = this;
        assignViews();
        tvTitle.setText("关于我们");
    }

    private void assignViews() {
        tvVersion = (TextView) findViewById(R.id.tv_version);
        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void initData() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            tvVersion.setText("V"+info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

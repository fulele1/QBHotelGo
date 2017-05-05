package com.xaqb.unlock.Activity;

import android.content.pm.PackageInfo;
import android.view.View;
import android.widget.TextView;

import com.xaqb.unlock.R;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class AboutActivity extends BaseActivity {
    private AboutActivity instance;
    private TextView tvVersion;

    @Override
    public void initTitleBar() {
        setTitle("关于我们");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.about_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        tvVersion = (TextView) findViewById(R.id.tv_version);
    }

    @Override
    public void initData() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
            // 当前应用的版本名称
            tvVersion.setText("咚咚开锁"+info.versionName);
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

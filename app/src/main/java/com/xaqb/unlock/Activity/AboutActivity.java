package com.xaqb.unlock.Activity;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.StatuBarUtil;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class AboutActivity extends BaseActivityNew {
    private AboutActivity instance;
    private TextView tvVersion;
    private TextView tvTitle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.about_activity);
        instance = this;
        StatuBarUtil.setStatusBarColor(this,getResources().getColor(R.color.main));

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

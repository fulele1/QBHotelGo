package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class AboutActivity extends BaseActivityNew {

    private AboutActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {

        setContentView(R.layout.activity_about);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));

        title.setText("关于我们");
    }

    @Override
    public void initData() throws Exception {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);

            tv_version.setText("V"+info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void addListener() throws Exception {

    }
}

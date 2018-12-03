package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
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

    @BindView(R.id.txt_tel_about)
    TextView txt_tel_about;
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
        txt_tel_about = findViewById(R.id.txt_tel_about);
        txt_tel_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showAdialog(instance,"提示","呼叫客服"+"tel:029-87888612","确定",View.VISIBLE);

            }
        });

        title.setText("关于我们");
    }

    @Override
    protected void dialogOk() {
        super.dialogOk();
        Intent dialIntent =  new Intent(Intent.ACTION_DIAL, Uri.parse("tel:029-87888612"));//跳转到拨号界面，同时传递电话号码
        instance.startActivity(dialIntent);
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

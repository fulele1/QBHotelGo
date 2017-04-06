package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.SPUtils;


/**
 * Created by chengeng on 2016/12/2.
 * 设置页面
 */
public class SettingActivity extends BaseActivity {
    private SettingActivity instance;
    private Button btQuit;
    private LinearLayout llResetPsw;

    @Override
    public void initTitleBar() {
        setTitle("设置");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.setting_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        btQuit = (Button) findViewById(R.id.bt_quit_login);
        llResetPsw = (LinearLayout) findViewById(R.id.ll_reset_psw);


    }

    @Override
    public void initData() {

    }

    @Override
    public void addListener() {
        btQuit.setOnClickListener(instance);
        llResetPsw.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_quit_login:
                showDialog("提示", "您确定退出登录吗？", "确定", "取消", 0);
                break;
            case R.id.ll_reset_psw:
                startActivity(new Intent(instance, ResetPswActivity.class));
                break;
        }
    }

    @Override
    protected void dialogOk() {
        super.dialogOk();
        SPUtils.put(instance, "userName", "");
        SPUtils.put(instance, "userPsw", "");
        SPUtils.put(instance, "rememberPsw", false);
        startActivity(new Intent(instance, LoginActivity.class));
        finish();
    }
}
package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.xaqb.unlock.R;


/**
 * Created by lenovo on 2016/11/22.
 * 引导页面
 */
public class SplashActivity extends BaseActivity {

    private ImageView iv;

    @Override
    public void initTitleBar() {
        setTitleBarVisible(View.GONE);
    }

    @Override
    public void initViews() {

        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        assignViews();
    }

    private void assignViews() {
        iv = (ImageView) findViewById(R.id.iv);
    }

    @Override
    public void initData() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
//                animationDrawable.stop();
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000);
    }

    @Override
    public void addListener() {

    }
}

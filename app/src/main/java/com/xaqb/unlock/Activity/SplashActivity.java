package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.CheckNetwork;


/**
 * Created by lenovo on 2016/11/22.
 * 引导页面
 */
public class SplashActivity extends BaseActivityNew {

    private ImageView iv;


    @Override
    public void initViews() {

        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_activity);
        iv = (ImageView) findViewById(R.id.iv);
        AlphaAnimation animation = new AlphaAnimation(0.5f,1.0f);
        animation.setDuration(2000);
        iv.setAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            /**
             * 动画开始前
             * @param animation
             */
            @Override
            public void onAnimationStart(Animation animation) {
                //检查是否有网络连接
                CheckNetwork.checkNetwork(SplashActivity.this);
            }

            /**
             * 动画结束后
             * @param animation
             */
            @Override
            public void onAnimationEnd(Animation animation) {
                if (CheckNetwork.isNetworkAvailable(SplashActivity.this)){
                    startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                    SplashActivity.this.finish();
                }
            }

            /**
             * 动画重复的时候
             * @param animation
             */
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void initData() {

    }

    @Override
    public void addListener() {

    }

}

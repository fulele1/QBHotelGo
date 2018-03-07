package com.xaqb.unlock.Activity;


import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Base64Utils;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.QBCallback;
import com.xaqb.unlock.Utils.QBHttp;
import com.xaqb.unlock.Utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class ResetPicActivity extends BaseActivityNew{

    private ResetPicActivity instance;
    private TextView mTvTitle,mTvForward;
    private ImageView mIvPic;
    private String oldPic,newPic;
    @Override
    public void initViews(){
        setContentView(R.layout.activity_reset_pic);
        instance = this;
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvForward = (TextView) findViewById(R.id.tv_forward);
        mTvForward.setVisibility(View.VISIBLE);
        mIvPic = (ImageView) findViewById(R.id.iv_pic_rePic);
        mTvTitle.setText("个人头像");
        Intent intent = getIntent();
        oldPic = intent.getStringExtra("url");

        newPic = Base64Utils.photoToBase64(((BitmapDrawable) ((ImageView) mIvPic).getDrawable()).getBitmap(), 60);
            Picasso.with(instance)
                    .load(oldPic)
                    .error(R.mipmap.main_user)
                    .placeholder(R.mipmap.main_user)
                    .into(mIvPic);
    }

    @Override
    public void doThis() {
        resetNickName();
    }

    @Override
    public void initData(){

    }

    @Override
    public void addListener(){

    }

    private void resetNickName() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("正在修改");
        Map<String, String> map = new HashMap<>();
        map.put("headpic", newPic);
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), GsonUtil.GsonString(map));

        QBHttp.put(
                instance
                , HttpUrlUtils.getHttpUrl().getUpdataUserinfoUrl() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", "")
                , body
                , new QBCallback() {
                    @Override
                    public void doWork(Map<?, ?> map) {
                        try {
                            loadingDialog.dismiss();
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                showToast("修改头像成功");
                                finish();
                            } else if (map.get("state").toString().equals(Globals.httpTokenFailure)) {
                                ActivityController.finishAll();
                                showToast("登录失效，请重新登录");
                                startActivity(new Intent(instance, LoginActivity.class));
                            } else {
                                showToast(map.get("mess").toString());
                                return;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void doError(Exception e) {
                        e.printStackTrace();
                        loadingDialog.dismiss();
                        showToast("网络访问异常");
                    }

                    @Override
                    public void reDoWork() {

                    }
                }
        );

    }

}

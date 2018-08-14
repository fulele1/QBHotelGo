package com.xaqb.hotel.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.xaqb.hotel.Entity.Log;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.CodeUtils;
import com.xaqb.hotel.Utils.Globals;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by fl on 2016/11/22.
 */
public class BackPswActivity extends BaseActivityNew {

    private TextView tvGetVCode,tvTitle;
    private Button btComplete;
    private BackPswActivity instance;
    private EditText etPhone, etVCode, etPsw,edit_picCode;
    private String phone, vCode, psw, codeKey = "";
    private TimeCount time;
    private  ImageView img_code;
    private  FrameLayout layout_titleba;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.backpsw_activity);
        instance = this;
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        assignViews();
        layout_titleba.setBackgroundColor(getResources().getColor(R.color.white));
        tvTitle.setText("找回密码");
    }

    private void assignViews() {
        tvGetVCode = (TextView) findViewById(R.id.tv_get_v_code);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        etPhone = (EditText) findViewById(R.id.et_username);
        etVCode = (EditText) findViewById(R.id.et_v_code);
        etPsw = (EditText) findViewById(R.id.et_password);
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        btComplete = (Button) findViewById(R.id.bt_complete);
        img_code = (ImageView) findViewById(R.id.IMG_JFDSKFJ);
        edit_picCode = (EditText) findViewById(R.id.edit_pic_code_backpsw);
        layout_titleba = (FrameLayout) findViewById(R.id.layout_titlebar);
        getPicCode();

    }


    private String redoum = "";
    /**
     * 图片验证码生成
     */
    public void getPicCode(){
        redoum = CodeUtils.getInstance().createCode();
        Glide.with(instance)
                .load("http://hotel.qbchoice.cn/v1/governor/captchar?check="+redoum)
                .into(img_code);
    }


    @Override
    public void initData() {
    }

    @Override
    public void addListener() {
        tvGetVCode.setOnClickListener(instance);
        btComplete.setOnClickListener(instance);
        img_code.setOnClickListener(instance);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_v_code:
                getVCode();
                break;
            case R.id.bt_complete:
                getcheckData();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resetPsw();
                    }
                }, 1000);

                break;
            case R.id.IMG_JFDSKFJ:
                getPicCode();
                break;
        }
    }

    private void resetPsw() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        phone = etPhone.getText().toString().trim();
        vCode = etVCode.getText().toString().trim();
        psw = etPsw.getText().toString().trim();

            loadingDialog.show("正在修改");

            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getBackPswUrl())
                    .addParams("mp", phone)
                    .addParams("checktmp", codeKey)
                    .addParams("newpwd", psw)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            loadingDialog.dismiss();

                            LogUtils.e("找回密码"+s);
                            try {
                                Map<String, Object> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                    showToast("找回密码成功");
                                    finish();
                                } else {
                                    showToast(map.get("mess").toString());
                                    return;
                                }
                            } catch (Exception e) {
                                    showToast(e.toString());
                            }
                        }
                    });

    }

    /**
     * 找回密码数据校验
     * @return
     */
    private void getcheckData() {
        OkHttpUtils
                .post()
                .url(HttpUrlUtils.getHttpUrl().getcheckmpCode())
                .addParams("mp",etPhone.getText().toString().trim())
                .addParams("smscode",etVCode.getText().toString().trim())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e("生成校验码"+s);

                        try {
                            loadingDialog.dismiss();
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpFaildState)) {
                                showToast(map.get("mess").toString());
                                return;
                            }
                            codeKey = map.get("table").toString();
                             LogUtils.e("取得数据校验码"+codeKey);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }


    /**
     * 获取手机验证码
     */
    public void getVCode() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        phone = etPhone.getText().toString().trim();
        if (phone == null || phone.equals("")) {
            showToast("请输入手机号码");
        } else {
            loadingDialog.show("正在获取验证码");
            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getVerCode())
                    .addParams("imgcode",edit_picCode.getText().toString().trim())
                    .addParams("tel",etPhone.getText().toString().trim())
                    .addParams("check",redoum)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            try {
                                loadingDialog.dismiss();
                                Map<String, Object> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals(Globals.httpFaildState)) {
                                    showToast(map.get("mess").toString());
                                    return;
                                }
                                time.start();
                                codeKey = map.get("table").toString();
                                if (codeKey.contains("\"")) {
                                    codeKey = codeKey.substring(1, codeKey.length() - 1).toString();
                                }
                                showToast("已发送验证码至您的手机");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    /* 定义一个倒计时的内部类 */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            tvGetVCode.setText("重新获取");
            tvGetVCode.setBackgroundResource(R.drawable.bg_button);
            tvGetVCode.setClickable(true);
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            tvGetVCode.setClickable(false);
            tvGetVCode.setText(millisUntilFinished / 1000 + "S" + "后重新获取");
            tvGetVCode.setBackgroundResource(R.drawable.bg_button_gray);
        }
    }
}

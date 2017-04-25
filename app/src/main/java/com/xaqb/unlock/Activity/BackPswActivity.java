package com.xaqb.unlock.Activity;

import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by lenovo on 2016/11/22.
 */
public class BackPswActivity extends BaseActivity {

    private TextView tvGetVCode;
    private Button btComplete;
    private BackPswActivity instance;
    private EditText etPhone, etVCode, etPsw, etConfirmPsw;
    private int requestCode = 0;
    private String phone, vCode, psw, confirmPsw, codeKey = "";
    private TimeCount time;

    @Override
    public void initTitleBar() {
        setTitle("找回密码");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.backpsw_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        tvGetVCode = (TextView) findViewById(R.id.tv_get_v_code);
        etPhone = (EditText) findViewById(R.id.et_username);
        etVCode = (EditText) findViewById(R.id.et_v_code);
        etPsw = (EditText) findViewById(R.id.et_password);
        etConfirmPsw = (EditText) findViewById(R.id.et_confirm_psw);
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        btComplete = (Button) findViewById(R.id.bt_complete);
    }

    @Override
    public void initData() {


    }

    @Override
    public void addListener() {
        tvGetVCode.setOnClickListener(instance);
        btComplete.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_get_v_code:
                getVCode();
                break;
            case R.id.bt_complete:
                resetPsw();
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
        confirmPsw = etConfirmPsw.getText().toString().trim();
        if (phone == null || phone.equals("")) {
            showToast("请输入手机号码");
        } else if (vCode == null || vCode.equals("")) {
            showToast("请输入验证码");
        } else if (psw == null || psw.equals("")) {
            showToast("请输入密码");
        } else if (confirmPsw == null || confirmPsw.equals("")) {
            showToast("请输入确认密码");
        } else if (!psw.equals(confirmPsw)) {
            showToast("两次输入的密码不一致");
        } else {
            LogUtils.i(codeKey);
            if (codeKey == null || codeKey.equals("")) {
                showToast("验证码失效，请重新获取验证码");
                return;
            }
            LogUtils.i(HttpUrlUtils.getHttpUrl().getBackPswUrl());
            loadingDialog.show("正在修改");
            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getBackPswUrl())
                    .addParams("new_pwd", confirmPsw)
                    .addParams("code", vCode)
                    .addParams("codekey", codeKey)
                    .addParams("tel", phone)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            e.printStackTrace();
                            loadingDialog.dismiss();
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            try {
                                loadingDialog.dismiss();
                                LogUtils.i(s);
                                Map<String, Object> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                    showToast("找回密码成功");
                                    finish();
                                } else {
                                    showToast(map.get("mess").toString());
                                    return;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    public void getVCode() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        phone = etPhone.getText().toString().trim();
        if (phone == null || phone.equals("")) {
            showToast("请输入手机号码");
        } else {
            LogUtils.i(HttpUrlUtils.getHttpUrl().getVerCode() + "/" + phone);
            loadingDialog.show("正在获取验证码");
            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getVerCode() + "/" + phone)
//                    .addParams("tel", phone)
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
                                LogUtils.i(GsonUtil.GsonString(map.get("table")).toString());
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

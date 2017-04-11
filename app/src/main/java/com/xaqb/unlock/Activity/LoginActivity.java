package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.MyApplication;
import com.xaqb.unlock.Utils.SDCardUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by lenovo on 2016/11/22.
 * 登录页面
 */
public class LoginActivity extends BaseActivity {

    private TextView tvForgetPsw, tvRegister;
    private LoginActivity instance;
    private Button btLogin;
    private String username, psw;
    private EditText etUsername, etPsw;
    private CheckBox cbRememberPsw;


    @Override
    public void initTitleBar() {
        setTitle(R.string.login);
        showBackwardView(false);
    }

    @Override
    public void initViews() {

        setContentView(R.layout.login_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {

//        iv = (ImageView) findViewById(R.id.iv);
        tvForgetPsw = (TextView) findViewById(R.id.tv_forgetPsw);
//        tvRegister = (TextView) findViewById(R.id.tv_register);
        btLogin = (Button) findViewById(R.id.bt_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPsw = (EditText) findViewById(R.id.et_password);
        cbRememberPsw = (CheckBox) findViewById(R.id.cb_remember_psw);
    }

    @Override
    public void initData() {
        username = (String) SPUtils.get(instance, "userName", "");
        psw = (String) SPUtils.get(instance, "userPsw", "");
        boolean rememberPsw = (boolean) SPUtils.get(instance, "rememberPsw", false);
        if (rememberPsw) cbRememberPsw.setChecked(true);
        if (username != null && !username.isEmpty()) {
            etUsername.setText(username);
        }
        if (psw != null && !psw.isEmpty()) {
            etPsw.setText(psw);
        }
        SDCardUtils.copyDBToSD(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/unlock/tessdata", "number.traineddata");
    }

    @Override
    public void addListener() {
        tvForgetPsw.setOnClickListener(instance);
//        tvRegister.setOnClickListener(instance);
        btLogin.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgetPsw:
                startActivity(new Intent(instance, BackPswActivity.class));
                break;
//            case R.id.tv_register:
//                startActivity(new Intent(instance, RegisterActivity.class));
//                break;
            case R.id.bt_login:
                login();
//                finish();
//                startActivity(new Intent(instance, MainActivity.class));
                break;
        }
    }

    private void login() {
//        startActivity(new Intent(instance, MainActivity.class));
        username = etUsername.getText().toString().trim();
        psw = etPsw.getText().toString().trim();
        if (username == null || username.equals("")) {
            showToast("请输入账号");
        } else if (psw == null || psw.equals("")) {
            showToast("请输入密码");
        } else {
            LogUtils.i(HttpUrlUtils.getHttpUrl().getLoginUrl());
            loadingDialog.show("正在登陆");
            try {
                OkHttpUtils
                        .post()
                        .url(HttpUrlUtils.getHttpUrl().getLoginUrl())
                        .addParams("name", username)
                        .addParams("pwd", psw)
                        .addParams("deviceid", MyApplication.deviceId)
                        .build()
                        .execute(new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int i) {
                                loadingDialog.dismiss();
                                showToast("网络访问异常");
                            }

                            @Override
                            public void onResponse(String s, int i) {
                                LogUtils.i("s ==", s);
                                loadingDialog.dismiss();
                                Map<?, ?> map = GsonUtil.JsonToMap(s);
                                LogUtils.i("map== ", map.toString());
//                                TreeMap<String, Object> map11 =
//                                        GsonUtil.gson.fromJson(s, new TypeToken<TreeMap<String, Object>>(){}.getType());
//                                Map<?, ?> map11 = GsonUtil.GsonToMap(s);
//                                LogUtils.i("map11========== ", map11.toString());


                                if (map.get("state").toString().equals("0")) {
                                    SPUtils.put(instance, "userAccount", username);
                                    SPUtils.put(instance, "userid", map.get("id").toString());
                                    SPUtils.put(instance, "access_token", map.get("access_token"));
                                    SPUtils.put(instance, "staff_headpic", map.get("staff_headpic"));
                                    SPUtils.put(instance, "staff_nickname", map.get("staff_nickname"));
                                    SPUtils.put(instance, "staff_mp", map.get("staff_mp"));
                                    SPUtils.put(instance, "staff_is_real", map.get("staff_is_real"));
                                    SPUtils.put(instance, "staff_address", map.get("address"));
                                    SPUtils.put(instance, "staff_qq", map.get("staff_qq"));
                                    SPUtils.put(instance, "staff_company", map.get("staff_company"));
//                            SPUtils.put(instance, "userPicLocal", "storage/sdcard1/DongDong/" + SPUtils.get(instance, "userid", "").toString() + "userHead");
                                    showToast("登录成功");

                                    if (cbRememberPsw.isChecked()) {
                                        SPUtils.put(instance, "userName", username);
                                        SPUtils.put(instance, "userPsw", psw);
                                        SPUtils.put(instance, "rememberPsw", true);
                                    } else {
                                        SPUtils.put(instance, "userName", "");
                                        SPUtils.put(instance, "userPsw", "");
                                        SPUtils.put(instance, "rememberPsw", false);
                                    }
                                    finish();
                                    startActivity(new Intent(instance, MainActivity.class));
                                } else {
                                    showToast(map.get("mess").toString());
                                    return;
                                }
                            }
                        });
            } catch (Exception e) {
                showToast("网络访问异常，请稍后再试");
                e.printStackTrace();
            }
        }
    }
}

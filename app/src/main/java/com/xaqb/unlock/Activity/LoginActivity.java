package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.MyApplication;
import com.xaqb.unlock.Utils.PermissionUtils;
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
public class LoginActivity extends BaseActivityNew {

    private TextView tvForgetPsw;
    private LoginActivity instance;
    private Button btLogin;
    private String username, psw;
    private EditText etUsername, etPsw;
    private CheckBox cbRememberPsw;
    private ImageView ivDeUser,ivDePsw;

    @Override
    public void initViews() {


        StatusBarUtil.setTranslucent(this, 0);
        setContentView(R.layout.login_activity);
        instance = this;
        assignViews();
        setDeleteImgview(etUsername,ivDeUser);
        setDeleteImgview(etPsw,ivDePsw);
    }


    /**
     *
     */
    private void setDeleteImgview(EditText editText, final ImageView imageView){
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    private void assignViews() {
        tvForgetPsw = (TextView) findViewById(R.id.tv_forgetPsw);
        btLogin = (Button) findViewById(R.id.bt_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPsw = (EditText) findViewById(R.id.et_password);
        cbRememberPsw = (CheckBox) findViewById(R.id.cb_remember_psw);
        ivDeUser = (ImageView) findViewById(R.id.img_delete_user_login);
        ivDePsw = (ImageView) findViewById(R.id.img_delete_psw_login);
    }


    @Override
    public void initData() {
        username = (String) SPUtils.get(instance, "userName", "");
        psw = (String) SPUtils.get(instance, "userPsw", "");
        boolean rememberPsw = (boolean) SPUtils.get(instance, "rememberPsw", false);
        if (rememberPsw) {
            cbRememberPsw.setChecked(true);
            if (username != null && !username.isEmpty()) {
                etUsername.setText(username);
            }
            if (psw != null && !psw.isEmpty()) {
                etPsw.setText(psw);
            }
        }
        checkPer(PermissionUtils.CODE_WRITE_EXTERNAL_STORAGE);
    }

    @Override
    protected void requestPerPass(int requestCode) {
        super.requestPerPass(requestCode);
        SDCardUtils.copyDBToSD(this, Environment.getExternalStorageDirectory().getAbsolutePath() + "/unlock/tessdata", "number.traineddata");
    }

    @Override
    public void addListener() {
        tvForgetPsw.setOnClickListener(instance);
        btLogin.setOnClickListener(instance);
        ivDePsw.setOnClickListener(instance);
        ivDeUser.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_forgetPsw://忘记密码
                startActivity(new Intent(instance, BackPswActivity.class));
                break;
            case R.id.bt_login://登录
                login();
                break;
            case R.id.img_delete_user_login://删除用户名
                etUsername.setText("");
                ivDeUser.setVisibility(View.GONE);
                break;
            case R.id.img_delete_psw_login://删除密码
                etPsw.setText("");
                ivDePsw.setVisibility(View.GONE);
                break;
        }
    }

    private void login() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        username = etUsername.getText().toString().trim();
        psw = etPsw.getText().toString().trim();
        if (username == null || username.equals("")) {
            showToast("请输入账号");
        } else if (psw == null || psw.equals("")) {
            showToast("请输入密码");
        } else {
            loadingDialog.show("正在登陆");
            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getLoginUrl())
                    .addParams("name", username)
                    .addParams("pwd", psw)
                    .addParams("deviceid", MyApplication.deviceId)//阿里云设备标识
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            loadingDialog.dismiss();
                            showToast("网络访问异常");
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            try {
                                loadingDialog.dismiss();
                                Map<?, ?> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals("0")) {
                                    SPUtils.put(instance, "userAccount", username);
                                    SPUtils.put(instance, "userid", map.get("id").toString());
                                    SPUtils.put(instance, "access_token", map.get("access_token"));
                                    SPUtils.put(instance, "tokenTime", System.currentTimeMillis());
                                    SPUtils.put(instance, "refreshTokenTime", System.currentTimeMillis());
                                    SPUtils.put(instance, "refresh_token", map.get("refresh_token"));
                                    SPUtils.put(instance, "staff_headpic", map.get("staff_headpic"));
                                    SPUtils.put(instance, "staff_nickname", map.get("staff_nickname"));
                                    SPUtils.put(instance, "staff_mp", map.get("staff_mp"));
                                    SPUtils.put(instance, "staff_is_real", map.get("staff_is_real"));//认证状态
                                    SPUtils.put(instance, "staff_address", map.get("address"));
                                    SPUtils.put(instance, "staff_qq", map.get("staff_qq"));
                                    SPUtils.put(instance, "staff_company", map.get("staff_company"));
                                    SPUtils.put(instance, "userName", username);
                                    SPUtils.put(instance, "userPsw", psw);
                                    if (cbRememberPsw.isChecked()) {
                                        SPUtils.put(instance, "rememberPsw", true);
                                    } else {
                                        SPUtils.put(instance, "rememberPsw", false);
                                    }
                                    finish();
                                    startActivity(new Intent(instance, MainActivity.class));
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
}

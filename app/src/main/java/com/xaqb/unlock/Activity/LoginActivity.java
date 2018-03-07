package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.NullUtil;
import com.xaqb.unlock.Utils.PermissionUtils;
import com.xaqb.unlock.Utils.SDCardUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import org.litepal.util.LogUtil;

import java.util.Map;

import okhttp3.Call;

/**
 * Created by lenovo on 2016/11/22.
 * 登录页面
 */
public class LoginActivity extends BaseActivityNew {

    private TextView tvForgetPsw,mTst;
    private LoginActivity instance;
    private Button btLogin;
    private String username, psw;
    private EditText etUsername, etPsw;
    private CheckBox cbRememberPsw;
    private LinearLayout mLayStatus;
    private ImageView ivDeUser,ivDePsw;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.login_activity);
        instance = this;
        StatuBarUtil.translucentStatusBar(this,true);
        LogUtils.e(""+Build.VERSION.SDK_INT);
        LogUtils.e(""+Build.VERSION_CODES.LOLLIPOP);
        if (Build.VERSION.SDK_INT> Build.VERSION_CODES.LOLLIPOP){
        StatuBarUtil.translucentStatusBar(this,true);
        }
        assignViews();
        setDeleteImgview(etUsername,ivDeUser);
        setDeleteImgview(etPsw,ivDePsw);
        if (HttpUrlUtils.getHttpUrl().getBaseUrl().equals("http://kaisuo.qbchoice.cn")){
            mTst.setVisibility(View.VISIBLE);
        }

    }

    /**
     *
     * @param editText
     * @param imageView
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

    /**
     *
     */
    private void assignViews() {
        tvForgetPsw = (TextView) findViewById(R.id.tv_forgetPsw);
        mTst = (TextView) findViewById(R.id.test_version);
        btLogin = (Button) findViewById(R.id.bt_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPsw = (EditText) findViewById(R.id.et_password);
        cbRememberPsw = (CheckBox) findViewById(R.id.cb_remember_psw);
        ivDeUser = (ImageView) findViewById(R.id.img_delete_user_login);
        ivDePsw = (ImageView) findViewById(R.id.img_delete_psw_login);
        mLayStatus = (LinearLayout) findViewById(R.id.lay_status);
//        StatusBarUtil.setTranslucentForImageView(this, 0, mLayStatus);
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
                    .addParams("deviceid", "")
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            loadingDialog.dismiss();
                            showToast("网络访问异常");
                        }


                        @Override
                        public void onResponse(String s, int i) {
                            LogUtils.e(s);
                            try {
                                loadingDialog.dismiss();
                                Map<?, ?> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals("0")) {
                                    SPUtils.put(instance, "userAccount", username);
                                    SPUtils.put(instance, "userid", NullUtil.getString(map.get("id")));
                                    SPUtils.put(instance, "access_token", NullUtil.getString(map.get("access_token")));
                                    SPUtils.put(instance, "tokenTime", System.currentTimeMillis());
                                    SPUtils.put(instance, "refreshTokenTime", System.currentTimeMillis());
                                    SPUtils.put(instance, "refresh_token", NullUtil.getString(map.get("refresh_token")));
                                    SPUtils.put(instance, "staff_headpic", NullUtil.getString(map.get("staff_headpic")));
                                    SPUtils.put(instance, "staff_nickname", NullUtil.getString(map.get("staff_nickname")));
                                    SPUtils.put(instance, "staff_mp", NullUtil.getString(map.get("staff_mp")));
                                    SPUtils.put(instance, "staff_is_real", NullUtil.getString(map.get("staff_is_real")));//认证状态
                                    SPUtils.put(instance, "staff_address", NullUtil.getString(map.get("address")));
                                    SPUtils.put(instance, "staff_qq", NullUtil.getString(map.get("staff_qq")));
                                    SPUtils.put(instance, "staff_company", NullUtil.getString(map.get("staff_company")));
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
                                    showToast(NullUtil.getString(map.get("mess")));
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

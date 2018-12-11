package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
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

import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.PermissionUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SDCardUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
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
    private TextView version_login;
    private String username, psw;
    private EditText etUsername, etPsw;
    private CheckBox cbRememberPsw;
    private LinearLayout mLayStatus;
    private boolean isQuit = false;
    private ImageView ivDeUser, ivDePsw;
    SharedPreferences sprfMain;
    SharedPreferences.Editor editorMain;
//    private Handler mHandler = new Handler() {
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            isQuit = false;
//        }
//    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        sprfMain = PreferenceManager.getDefaultSharedPreferences(this);
        editorMain = sprfMain.edit();

//        if(sprfMain.getBoolean("main",false)){
//            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//            startActivity(intent);
//            LoginActivity.this.finish();
//        }

        setContentView(R.layout.login_activity);
        instance = this;
        StatuBarUtil.translucentStatusBar(this, true);
        assignViews();
        setDeleteImgview(etUsername, ivDeUser);
        setDeleteImgview(etPsw, ivDePsw);


    }

    /**
     * @param editText
     * @param imageView
     */
    private void setDeleteImgview(EditText editText, final ImageView imageView) {
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
        tvForgetPsw =  findViewById(R.id.tv_forgetPsw);
        btLogin =  findViewById(R.id.bt_login);
        etUsername =  findViewById(R.id.et_username);
        etPsw =  findViewById(R.id.et_password);
        cbRememberPsw =  findViewById(R.id.cb_remember_psw);
        ivDeUser =  findViewById(R.id.img_delete_user_login);
        ivDePsw =  findViewById(R.id.img_delete_psw_login);
        mLayStatus =  findViewById(R.id.lay_status);
        version_login =  findViewById(R.id.version_login);
        version_login.setText("v"+getVersionName());
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersionName() {
        try {
            PackageInfo info = instance.getPackageManager().getPackageInfo(instance.getPackageName(), 0);

            // 当前应用的版本名称
            return info.versionName;

        } catch (Exception e) {
            return "";
        }
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
        checkPer(PermissionUtils.CODE_CAMERA);
        if (sprfMain.getBoolean("auto", false)) {
//            login();
        }
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


//    @Override
//    public void onBackPressed() {
//        if (!isQuit) {
//            isQuit = true;
//            Toast.makeText(getApplicationContext(), "再按一次退出程序",
//                    Toast.LENGTH_SHORT).show();
//            // 利用handler延迟发送更改状态信息
//            mHandler.sendEmptyMessageDelayed(0, 2000);
//        } else {
//            finish();
//            System.exit(0);
//        }
//    }


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
            loadingDialog.show("");
            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getLoginUrl())
                    .addParams("name", username)
                    .addParams("pwd", psw)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            showToast(e.toString());
                        }
                        @Override
                        public void onResponse(String s, int i) {
                            LogUtils.e(s);
                            try {
                                loadingDialog.dismiss();
                                Map<?, ?> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals("0")) {
                                    SPUtils.put(instance, "ou_code", NullUtil.getString(map.get("ou_code")));//警员编号
                                    SPUtils.put(instance, "ou_nickname", NullUtil.getString(map.get("ou_nickname")));//昵称
                                    SPUtils.put(instance, "ou_headpic", HttpUrlUtils.getHttpUrl().getOuPic() +
                                            NullUtil.getString(map.get("ou_id")) + "/ou_headpic" +
                                            "?access_token=" + NullUtil.getString(map.get("access_token")));//用户头像
                                    SPUtils.put(instance, "ou_id", NullUtil.getString(map.get("ou_id")));//ou_id
                                    SPUtils.put(instance, "ho_count", NullUtil.getString(map.get("ho_count")));//旅馆数量
                                    SPUtils.put(instance, "so_name", NullUtil.getString(map.get("so_name")));//组织名称
                                    SPUtils.put(instance, "tourist_count", NullUtil.getString(map.get("tourist_count")));//旅客数量
                                    SPUtils.put(instance, "expire_in", NullUtil.getString(map.get("expire_in")));//expire_in
                                    SPUtils.put(instance, "access_token", NullUtil.getString(map.get("access_token")));//access_token
                                    SPUtils.put(instance, "refresh_token", NullUtil.getString(map.get("refresh_token")));//refresh_token
                                    SPUtils.put(instance, "ou_securityorg", NullUtil.getString(map.get("ou_securityorg")));//
                                    SPUtils.put(instance, "staff_count", NullUtil.getString(map.get("staff_count")));//
                                    SPUtils.put(instance, "so_level", NullUtil.getString(map.get("so_level")));//级别
                                    SPUtils.put(instance, "han", NullUtil.getString(map.get("han")));//汉族百分比
                                    SPUtils.put(instance, "zang", NullUtil.getString(map.get("zang")));//藏族百分比
                                    SPUtils.put(instance, "wei", NullUtil.getString(map.get("wei")));//维吾尔族百分比
                                    SPUtils.put(instance, "other", NullUtil.getString(map.get("other")));//其他族百分比
                                    SPUtils.put(instance, "fault", NullUtil.getString(map.get("fault")));//故障率

                                    LogUtils.e(NullUtil.getString(map.get("fault")) + "登录");

                                    SPUtils.put(instance, "ho_count", NullUtil.getString(map.get("ho_count")));//旅馆数
                                    SPUtils.put(instance, "table", NullUtil.getString(map.get("table")));//入住比例
                                    SPUtils.put(instance, "userName", username);
                                    SPUtils.put(instance, "userPsw", psw);
                                    if (cbRememberPsw.isChecked()) {
                                        SPUtils.put(instance, "rememberPsw", true);
                                    } else {
                                        SPUtils.put(instance, "rememberPsw", false);
                                    }
                                    editorMain.putBoolean("main", true);
                                    editorMain.putBoolean("auto", true);
                                    editorMain.commit();
                                    startActivity(new Intent(instance, MainActivity.class));
                                    instance.finish();
                                }else {
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

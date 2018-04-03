package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.QBCallback;
import com.xaqb.unlock.Utils.QBHttp;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by lenovo on 2016/11/22.
 */
public class ResetPswActivity extends BaseActivityNew {

    private Button btComplete;
    private TextView tvTilte;
    private ResetPswActivity instance;
    private LinearLayout mLayStatus;
    private EditText etOldPsw, etNewPsw, etConfirmPsw;
    private int requestCode = 0;
    private String oldPsw, newPsw, confirmPsw;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() {
        setContentView(R.layout.resetpsw_activity);
        instance = this;
        assignViews();
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        tvTilte.setText("修改密码");
    }

    private void assignViews() {
        etOldPsw = (EditText) findViewById(R.id.et_oldpsw);
        etNewPsw = (EditText) findViewById(R.id.et_newpsw);
        etConfirmPsw = (EditText) findViewById(R.id.et_confirm_psw);
        btComplete = (Button) findViewById(R.id.bt_complete);
        tvTilte = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    public void initData() {
    }

    @Override
    public void addListener() {
        btComplete.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_complete:
                try {
                    resetPsw();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    private void resetPsw() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        oldPsw = etOldPsw.getText().toString().trim();
        newPsw = etNewPsw.getText().toString().trim();
        confirmPsw = etConfirmPsw.getText().toString().trim();
        if (oldPsw == null || oldPsw.equals("")) {
            showToast("请输入旧密码");
        } else if (oldPsw.length()>20 || oldPsw.length()<6) {
            showToast("请输入长度为6-20的密码");
        } else if (newPsw == null || newPsw.equals("")) {
            showToast("请输入新密码");
        } else if (newPsw.length()>20 || newPsw.length()<6) {
            showToast("请输入长度为6-20的密码");
        } else if (confirmPsw == null || confirmPsw.equals("")) {
            showToast("请确认新密码");
        } else if (confirmPsw.length()>20 || confirmPsw.length()<6) {
            showToast("请输入长度为6-20的密码");
        } else if (!newPsw.equals(confirmPsw)) {
            showToast("两次输入的密码不一致");
        } else {
            loadingDialog.show("正在修改");

            OkHttpUtils
                    .post()
                    .url(HttpUrlUtils.getHttpUrl().getResetPswUrl() +
                            SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                    .addParams("old_pwd", oldPsw)
                    .addParams("new_pwd", confirmPsw)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int i) {
                            loadingDialog.dismiss();
                            showToast("网络连接失败");
                        }

                        @Override
                        public void onResponse(String s, int i) {
                            try{
                                loadingDialog.dismiss();
                                Map<String, Object> map = GsonUtil.JsonToMap(s);
                                if (map.get("state").toString().equals("1")) {
                                    showToast(map.get("mess").toString());
                                    return;
                                } else if (map.get("state").toString().equals("0")) {
                                    showToast("找回密码成功");
                                    instance.startActivity(new Intent(instance, LoginActivity.class));
                                    instance.finish();
                                } else if(map.get("state").toString().equals("202")){
                                    showToast("旧密码输入不正确");
                                }else {
                                    showToast("找回密码失败,请稍后再试");
                                }

                            }catch (Exception e){
                                showToast("网络连接异常");

                            }

                        }
                    });
        }
    }

}

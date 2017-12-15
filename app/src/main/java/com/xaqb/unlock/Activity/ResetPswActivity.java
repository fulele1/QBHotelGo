package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.QBCallback;
import com.xaqb.unlock.Utils.QBHttp;
import com.xaqb.unlock.Utils.SPUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lenovo on 2016/11/22.
 */
public class ResetPswActivity extends BaseActivityNew {

    private Button btComplete;
    private TextView tvTilte;
    private ResetPswActivity instance;
    private EditText etOldPsw, etNewPsw, etConfirmPsw;
    private int requestCode = 0;
    private String oldPsw, newPsw, confirmPsw;

    @Override
    public void initViews() {
        StatusBarUtil.setTranslucent(this,0);
        setContentView(R.layout.resetpsw_activity);
        instance = this;
        assignViews();
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
            Map<String, Object> params = new HashMap<>();
            params.put("old_pwd", oldPsw);
            params.put("new_pwd", confirmPsw);
            QBHttp.post(
                    instance
                    , HttpUrlUtils.getHttpUrl().getResetPswUrl() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", "")
                    , params
                    , new QBCallback() {
                        @Override
                        public void doWork(Map<?, ?> map) {
                            try {
                                loadingDialog.dismiss();
                                if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                    showToast("修改密码成功");
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

}

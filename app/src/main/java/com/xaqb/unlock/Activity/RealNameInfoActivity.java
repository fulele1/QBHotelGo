package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import com.xaqb.unlock.R;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import okhttp3.Call;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class RealNameInfoActivity extends BaseActivity {
    private RealNameInfoActivity instance;
    private TextView tvCertName, tvCertType, tvCertNum;

    @Override
    public void initTitleBar() {
        setTitle("实名认证信息");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.real_name_info_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {
        tvCertName = (TextView) findViewById(R.id.tv_cert_name);
        tvCertType = (TextView) findViewById(R.id.tv_cert_type);
        tvCertNum = (TextView) findViewById(R.id.tv_cert_num);
    }

    @Override
    public void initData() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        loadingDialog.show("加载中...");
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getRealNameInfo() + SPUtils.get(instance, "userid", "") + "?access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        e.printStackTrace();
                        showToast("网络访问异常");
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        loadingDialog.dismiss();
                        try {
                            Map<String, Object> map = GsonUtil.JsonToMap(s);
                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                                tvCertName.setText(map.get("ra_name").toString());
                                tvCertType.setText(map.get("ct_name").toString());
                                tvCertNum.setText(map.get("ra_certcode").toString());
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
                });

    }

    @Override
    public void addListener() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

}

package com.xaqb.unlock.Activity;

import android.view.View;

import com.xaqb.unlock.R;


/**
 * Created by chengeng on 2016/12/2.
 * 空activity，用于复制粘贴
 */
public class AboutActivity extends BaseActivity {
    private AboutActivity instance;

    @Override
    public void initTitleBar() {
        setTitle("关于我们");
        showBackwardView(true);
    }

    @Override
    public void initViews() {
        setContentView(R.layout.about_activity);
        instance = this;
        assignViews();
    }

    private void assignViews() {

    }

    @Override
    public void initData() {
//        if (!checkNetwork()) return;
//        LogUtils.i(HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "")+ "&p=0" + "&access_token=" + SPUtils.get(instance, "access_token", ""));
//        OkHttpUtils.get()
//                .url(HttpUrlUtils.getHttpUrl().getOrderList() + "?id=" + SPUtils.get(instance, "userid", "") + "&p=0" + "&access_token=" + SPUtils.get(instance, "access_token", ""))
//                .build()
//                .execute(new StringCallback() {
//                    @Override
//                    public void onError(Call call, Exception e, int i) {
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onResponse(String s, int i) {
//                        try {
//                            Map<String, Object> map = GsonUtil.JsonToMap(s);
//                            LogUtils.i(map.toString());
//                            if (map.get("state").toString().equals(Globals.httpSuccessState)) {
//                                LogUtils.i("senddata", "" + map.toString());
//                                List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
//                            } else {
//                                showToast(map.get("mess").toString());
//                                return;
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                });

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

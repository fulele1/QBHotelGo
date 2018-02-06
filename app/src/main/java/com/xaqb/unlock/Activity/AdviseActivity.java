package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
 * Created by chengeng on 2016/12/2.
 * 意见反馈界面
 */
public class AdviseActivity extends BaseActivityNew {
    private AdviseActivity instance;
    private EditText etTitle, etAdvise;
    private Button btComplete;
    private String title, advise, type;
    private Spinner spType;
    private TextView tvTitle;
    private LinearLayout mLayStatus;


    @Override
    public void initViews() {
//        StatusBarUtil.setTranslucent(this,0);
        setContentView(R.layout.advise_activity);
        instance = this;
        assignViews();
        tvTitle.setText("意见反馈");
    }

    private void assignViews() {
        etTitle = (EditText) findViewById(R.id.et_advise_title);
        etAdvise = (EditText) findViewById(R.id.et_advise_content);
        btComplete = (Button) findViewById(R.id.bt_complete);
        spType = (Spinner) findViewById(R.id.sp_advise_type);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        mLayStatus = (LinearLayout) findViewById(R.id.lay_status_Advice);
        StatusBarUtil.setTranslucentForImageView(this, 0, mLayStatus);
    }

    @Override
    public void initData() {

    }

    @Override
    public void addListener() {
        btComplete.setOnClickListener(instance);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    type = "0";
                } else {
                    type = "1";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = "0";
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_complete:
                uploadAdvise();
                break;
        }
    }

    private void uploadAdvise() {
        if (!checkNetwork()) {
            showToast(getResources().getString(R.string.network_not_alive));
            return;
        }
        title = etTitle.getText().toString().trim();
        advise = etAdvise.getText().toString().trim();
        if (title == null || title.equals("")) {
            showToast("请输入意见标题");
        } else if (advise == null || advise.equals("")) {
            showToast("请输入意见或建议后再提交");
        } else {
            loadingDialog.show("正在提交");
            try {
                Map<String, Object> map = new HashMap<>();
                map.put("title", title);
                map.put("content", advise);
                map.put("uid", SPUtils.get(instance, "userid", "").toString());
                map.put("utype", "2");
                map.put("type", type);

                QBHttp.post(instance,
                        HttpUrlUtils.getHttpUrl().getUploadAdvise() + "?access_token=" + SPUtils.get(instance, "access_token", "")
                        , map
                        , new QBCallback() {
                            @Override
                            public void doWork(Map<?, ?> map) {
                                loadingDialog.dismiss();
                                try {
                                    if (map.get("state").toString().equals(Globals.httpSuccessState)) {
                                        showToast("提交意见成功");
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
                                loadingDialog.dismiss();
                                e.printStackTrace();

                            }

                            @Override
                            public void reDoWork() {

                            }
                        });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

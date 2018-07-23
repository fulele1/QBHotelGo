package com.xaqb.hotel.Activity;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.DateUtil;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class LogDetActivity extends BaseActivityNew {
    private LogDetActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.edit_code_log_del)
    EditText edit_code;
    @BindView(R.id.edit_level_log_del)
    EditText edit_level;
    @BindView(R.id.edit_org_log_del)
    EditText edit_org;
    @BindView(R.id.edit_per_log_del)
    EditText edit_per;
    @BindView(R.id.edit_event_log_del)
    EditText edit_event;
    @BindView(R.id.edit_date_log_del)
    EditText edit_date;
    @BindView(R.id.edit_operate_log_del)
    EditText edit_operate;
    @BindView(R.id.edit_operate_date_log_del)
    EditText edit_operate_date;
    @BindView(R.id.edit_clue_log_del)
    EditText edit_clue;
    @BindView(R.id.edit_result_log_del)
    EditText edit_result;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_log_det);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("联查详情");
    }
    private String id;
    @Override
    public void initData() throws Exception {
        id = getIntent().getStringExtra("id");
        connecting();
    }

    @Override
    public void addListener() throws Exception {

    }

    private void connecting() {

        LogUtils.e("联合检查"+ HttpUrlUtils.getHttpUrl().LogDet()+id+"?access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().LogDet()+id+"?access_token="+ SPUtils.get(instance,"access_token",""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {

                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                edit_code.setText(NullUtil.getString(data.get("hname")));
                                edit_level.setText(NullUtil.getString(data.get("sl_name")));
                                edit_org.setText(NullUtil.getString(data.get("so_name")));
                                edit_per.setText(NullUtil.getString(data.get("ucorguser")));
                                edit_event.setText(NullUtil.getString(data.get("ucorgjob")));
                                edit_date.setText(DateUtil.getDate(NullUtil.getString(data.get("ucorgdate"))));
                                edit_operate.setText(NullUtil.getString(data.get("udcuser")));
                                edit_operate_date.setText(NullUtil.getString(data.get("udcdate")));
                                edit_clue.setText(NullUtil.getString(data.get("uccontent")));
                                edit_result.setText(NullUtil.getString(data.get("ucresult")));

                            } else if (data.get("state").toString().equals("0")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            }else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }


}

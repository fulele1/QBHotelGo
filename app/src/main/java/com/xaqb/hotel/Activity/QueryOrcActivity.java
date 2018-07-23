package com.xaqb.hotel.Activity;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.hotel.R;
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


public class QueryOrcActivity extends BaseActivityNew {
    QueryOrcActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.txt_monitoroutnum_query_orc)
    TextView txt_monitoroutnum;
    @BindView(R.id.txt_st_name_query_orc)
    TextView txt_st_name;
    @BindView(R.id.txt_monitornum_query_orc)
    TextView txt_monitornum;
    @BindView(R.id.txt_monitor_query_orc)
    TextView txt_monitor;
    @BindView(R.id.txt_so_name_query_orc)
    TextView txt_so_name;
    @BindView(R.id.txt_employee_query_orc)
    TextView txt_employee;
    @BindView(R.id.txt_ha_name_query_orc)
    TextView txt_ha_name;
    @BindView(R.id.txt_principaltel_query_orc)
    TextView txt_principaltel;
    @BindView(R.id.txt_principal_query_orc)
    TextView txt_principal;
    @BindView(R.id.txt_socialduty_query_orc)
    TextView txt_socialduty;
    @BindView(R.id.txt_monitorout_query_orc)
    TextView txt_monitorout;
    @BindView(R.id.txt_g_name_query_orc)
    TextView txt_g_name;
    @BindView(R.id.txt_hnohotel_query_orc)
    TextView txt_hnohotel;
    @BindView(R.id.txt_roomnum_query_orc)
    TextView txt_roomnum;
    @BindView(R.id.txt_sname_query_orc)
    TextView txt_sname;
    @BindView(R.id.txt_legalpersontel_query_orc)
    TextView txt_legalpersontel;
    @BindView(R.id.txt_telphone_query_orc)
    TextView txt_telphone;
    @BindView(R.id.txt_legalperson_query_orc)
    TextView txt_legalperson;
    @BindView(R.id.txt_fireno_query_orc)
    TextView txt_fireno;
    @BindView(R.id.txt_haddress_query_orc)
    TextView txt_haddress;
    @BindView(R.id.txt_hname_query_orc)
    TextView txt_hname;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_query_orc);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        title.setText("二维码查询");
        connecting();

    }

    @Override
    public void initData() throws Exception {


    }

    @Override
    public void addListener() throws Exception {

    }


    private String getIntentData() {
        Intent intent = getIntent();
        String code = intent.getStringExtra("code");
        return code;
    }

    private void connecting() {

        LogUtils.e(HttpUrlUtils.getHttpUrl().get_result() + getIntentData() + "&access_token=" + SPUtils.get(instance, "access_token", ""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().get_result() + getIntentData() + "&access_token=" + SPUtils.get(instance, "access_token", ""))
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
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                txt_monitoroutnum.setText(NullUtil.getString(data.get("monitoroutnum")));
                                txt_st_name.setText(NullUtil.getString(data.get("st_name")));
                                txt_monitornum.setText(NullUtil.getString(data.get("monitornum")));
                                txt_monitor.setText(NullUtil.getString(data.get("monitor")));
                                txt_so_name.setText(NullUtil.getString(data.get("so_name")));
                                txt_employee.setText(NullUtil.getString(data.get("employee")));
                                txt_ha_name.setText(NullUtil.getString(data.get("ha_name")));
                                txt_principaltel.setText(NullUtil.getString(data.get("principaltel")));
                                txt_principal.setText(NullUtil.getString(data.get("principal")));
                                txt_socialduty.setText(NullUtil.getString(data.get("socialduty")));
                                txt_monitorout.setText(NullUtil.getString(data.get("monitorout")));
                                txt_g_name.setText(NullUtil.getString(data.get("g_name")));
                                txt_hnohotel.setText(NullUtil.getString(data.get("hnohotel")));
                                txt_roomnum.setText(NullUtil.getString(data.get("roomnum")));
                                txt_sname.setText(NullUtil.getString(data.get("sname")));
                                txt_legalpersontel.setText(NullUtil.getString(data.get("legalpersontel")));
                                txt_telphone.setText(NullUtil.getString(data.get("telphone")));
                                txt_legalperson.setText(NullUtil.getString(data.get("legalperson")));
                                txt_fireno.setText(NullUtil.getString(data.get("fireno")));
                                txt_haddress.setText(NullUtil.getString(data.get("haddress")));
                                txt_hname.setText(NullUtil.getString(data.get("hname")));


                            } else if (data.get("state").toString().equals("0")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                        }


                    }
                });
    }

}

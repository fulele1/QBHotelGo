package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.CastTypeUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class PunishmentDelActivity extends BaseActivityNew {

    private PunishmentDelActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.edit_namehotel_delpud)
    EditText edit_namehotel_delpud;
    @BindView(R.id.edit_punishdate_delpud)
    EditText edit_punishdate_delpud;
    @BindView(R.id.edit_punishresult_delpud)
    EditText edit_punishresult_delpud;
    @BindView(R.id.edit_cflb_delpud)
    EditText edit_cflb_delpud;
    @BindView(R.id.edit_wgxq_delpud)
    EditText edit_wgxq_delpud;
    @BindView(R.id.edit_cfyj_delpud)
    EditText edit_cfyj_delpud;
    @BindView(R.id.edit_pzjg_delpud)
    EditText edit_pzjg_delpud;
    @BindView(R.id.edit_pzrxm_delpud)
    EditText edit_pzrxm_delpud;
    @BindView(R.id.edit_zxrxm_delpud)
    EditText edit_zxrxm_delpud;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_punishment_del);

        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("处罚详情");
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

        LogUtils.e( HttpUrlUtils.getHttpUrl().PunishmentList()+"/"+id+"?access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().PunishmentList()+"/"+id+"?access_token="+ SPUtils.get(instance,"access_token",""))
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
                                edit_namehotel_delpud.setText(NullUtil.getString(data.get("hname")));
                                edit_punishdate_delpud.setText(DateUtil.getDate(NullUtil.getString(data.get("punishdate"))));
                                        edit_punishresult_delpud.setText(CastTypeUtil.getTypeString(NullUtil.getString(data.get("cflb"))));
                                edit_cflb_delpud.setText(CastTypeUtil.getResultTypeString(NullUtil.getString(data.get("punishresult"))));
                                        edit_wgxq_delpud.setText(NullUtil.getString(data.get("cfyj")));
                                edit_cfyj_delpud.setText(NullUtil.getString(data.get("pzjg")));
                                        edit_pzjg_delpud.setText(NullUtil.getString(data.get("pzrxm")));
                                edit_pzrxm_delpud.setText(NullUtil.getString(data.get("zxrxm")));
                                        edit_zxrxm_delpud.setText(NullUtil.getString(data.get("wgxq")));

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
}}

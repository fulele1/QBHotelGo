package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqianbai.QBHotelSecurutyGovernor.R;
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

public class CrimeDelActivity extends BaseActivityNew {
    private CrimeDelActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.tv_forward)
    TextView tv_forward;

    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;

    @BindView(R.id.edit_hname_crdel)
    EditText edit_hname_crdel;
    @BindView(R.id.edit_level_log_del)
    EditText edit_level_log_del;
    @BindView(R.id.edit_org_log_del)
    EditText edit_org_log_del;
    @BindView(R.id.edit_per_log_del)
    EditText edit_per_log_del;
    @BindView(R.id.edit_event_log_del)
    EditText edit_event_log_del;
    @BindView(R.id.edit_remark_log_del)
    EditText edit_remark_log_del;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_crime_del);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("发案详情");
        tv_forward.setVisibility(View.VISIBLE);
        tv_forward.setOnClickListener(instance);
        loadingDialog.show("");

    }
    private String id;
    @Override
    public void initData() throws Exception {
        id = getIntent().getStringExtra("id");
        connecting();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_forward:
                Intent i = new Intent(instance, CrimeEditActivity.class);
                i.putExtra("id", id);
                startActivity(i);
                finish();
                break;
        }
    }

    @Override
    public void addListener() throws Exception {

    }

    private void connecting() {

        LogUtils.e( HttpUrlUtils.getHttpUrl().BothList()+"/"+id+"?access_token="+ SPUtils.get(instance,"access_token",""));
        OkHttpUtils
                .get()
                .url(HttpUrlUtils.getHttpUrl().BothList()+"/"+id+"?access_token="+ SPUtils.get(instance,"access_token",""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loadingDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                Toast.makeText(instance,data.get("mess").toString(),Toast.LENGTH_LONG).show();
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                edit_hname_crdel.setText(NullUtil.getString(data.get("hname")));
                                edit_level_log_del.setText(DateUtil.getDate(NullUtil.getString(data.get("crimedate"))));
                                        edit_org_log_del.setText(NullUtil.getString(data.get("cp_property")));
                                edit_per_log_del.setText(NullUtil.getString(data.get("ct_type")));
                                        edit_event_log_del.setText(NullUtil.getString(data.get("qkms")));
                                edit_remark_log_del.setText(NullUtil.getString(data.get("remark")));

                            }

//                            else if (data.get("state").toString().equals("0")) {
//                                //响应失败
//                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
//                            }

                            else if (data.get("state").toString().equals("10")) {
                                //响应失败
                                Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(instance,LoginActivity.class));
                                finish();
                            }
                        }catch (Exception e){
                            Toast.makeText(instance,e.toString(),Toast.LENGTH_SHORT).show();
                        }
                        loadingDialog.dismiss();
                    }
                });
    }

}

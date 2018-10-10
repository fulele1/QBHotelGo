package com.xaqianbai.QBHotelSecurutyGovernor.Activity;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.CastTypeUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

public class PunishmentAddActivity extends BaseActivityNew {

    private PunishmentAddActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.btn_finish_puad)
    Button btn_finish_puad;
    @BindView(R.id.edit_hname_pu)
    EditText edit_hname_pu;
    @BindView(R.id.edit_time_pu)
    EditText edit_time_pu;
    @BindView(R.id.edit_kind_pu)
    EditText edit_kind_pu;
    @BindView(R.id.edit_result_pu)
    EditText edit_result_pu;
    @BindView(R.id.edit_del_pu)
    EditText edit_del_pu;
    @BindView(R.id.edit_org_pu)
    EditText edit_org_pu;
    @BindView(R.id.edit_mane_pu)
    EditText edit_mane_pu;
    @BindView(R.id.edit_per_pu)
    EditText edit_per_pu;
    @BindView(R.id.edit_convendent_pu)
    EditText edit_convendent_pu;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_punish_add);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("处罚登记");

        pvTime = new TimePickerView(instance, TimePickerView.Type.YEAR_MONTH_DAY);
        pickerView = new OptionsPickerView(instance);
        pickerView1 = new OptionsPickerView(instance);
    }

    private void intternet() {

        if (mHotelCode.equals("")||mHotelCode ==null){
            Toast.makeText(instance, "请选择酒店", Toast.LENGTH_SHORT).show();
            return;
        }if (time.equals("")||time ==null){
            Toast.makeText(instance, "请选择处罚日期", Toast.LENGTH_SHORT).show();
            return;
        }if (kind.equals("")||kind ==null){
            Toast.makeText(instance, "请选择处罚类别", Toast.LENGTH_SHORT).show();
            return;
        }if (result.equals("")||result ==null){
            Toast.makeText(instance, "请选择处罚结果", Toast.LENGTH_SHORT).show();
            return;
        }


        LogUtils.e((HttpUrlUtils.getHttpUrl().PunishmentList() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString()));
        OkHttpUtils.post()
                .url(HttpUrlUtils.getHttpUrl().PunishmentList() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .addParams("nohotel",   mHotelCode)//
                .addParams("punishdate", DateUtil.data(time))//选
                .addParams("punishresult", result)//处罚结果 选
                .addParams("cflb", kind)//处罚类别 选
                .addParams("cfyj", edit_del_pu.getText().toString().trim())//处罚依据
                .addParams("pzjg", edit_org_pu.getText().toString().trim())//批准机构
                .addParams("pzrxm", edit_mane_pu.getText().toString().trim())//批准人姓名
                .addParams("zxrxm", edit_per_pu.getText().toString().trim())//执行人姓名
                .addParams("wgxq", edit_convendent_pu.getText().toString().trim())//违规详情
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        loadingDialog.dismiss();
                        showToast(e.toString());
                        LogUtils.e(e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e(s);
                        try{
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                showToast(data.get("mess").toString());
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                btn_finish_puad.setText("已发送成功");
                                btn_finish_puad.setEnabled(false);
//                                mIvShopDel.setVisibility(View.GONE);
//                                mIvTerDel.setVisibility(View.GONE);
                            } else {
                                showToast("发送失败");
                            }
                            loadingDialog.dismiss();
                        }catch (Exception e){
                            showToast("数据格式异常，无法解析");
                        }
                    }
                });


    }

    private TimePickerView pvTime;
    private OptionsPickerView pickerView;
    private OptionsPickerView pickerView1;
    private String time,kind,result;
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_finish_puad:
                time = edit_time_pu.getText().toString().trim();
                kind = CastTypeUtil.getTypeCode(edit_kind_pu.getText().toString().trim());
                result = CastTypeUtil.getResultType(edit_result_pu.getText().toString().trim());
                intternet();
                break;
                case R.id.edit_hname_pu:
                    Intent intent = new Intent(instance, SearchHotelActivity.class);
                    startActivityForResult(intent, 0);
                break;
                case R.id.edit_time_pu:
                    pvTime.show();
                break;
                case R.id.edit_kind_pu:
                    pickerView.show();
                break;
                case R.id.edit_result_pu:
                    pickerView1.show();
                break;
        }
    }


    String mHotelCode = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String name = bundle.getString("hname");
                String code = bundle.getString("hid");
                mHotelCode = code;
                edit_hname_pu.setText(name);
            }
        }
    }

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {
        btn_finish_puad.setOnClickListener(instance);
        edit_hname_pu.setOnClickListener(instance);
        edit_time_pu.setOnClickListener(instance);
        edit_kind_pu.setOnClickListener(instance);
        edit_result_pu.setOnClickListener(instance);
        checkTime();
        checkType();
        checkResult();
    }

    private void checkTime() {
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                edit_time_pu.setText(df.format(date));
            }
        });

    }
    final ArrayList<String> parent = new ArrayList<>();
    //案件选择
    private void checkType() {
        parent.add("警告");
        parent.add("罚款");
        parent.add("停业整顿");
        parent.add("吊销许可证");
        parent.add("限期整改");
        parent.add("其他");

        if (null != pickerView) {
            pickerView.setPicker(parent, null, true);
            pickerView.setCyclic(false);

            pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    edit_kind_pu.setText(parent.get(options1));
                }
            });
        }


    }
    final ArrayList<String> parent1 = new ArrayList<>();
    //案件选择
    private void checkResult() {
        parent1.add("未处罚");
        parent1.add("已处罚");
        if (null != pickerView) {
            pickerView1.setPicker(parent1, null, true);
            pickerView1.setCyclic(false);

            pickerView1.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    edit_result_pu.setText(parent1.get(options1));
                }
            });
        }


    }

        @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


}
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

public class CrimeAddActivity extends BaseActivityNew {

    private CrimeAddActivity instance;
    Unbinder unbinder;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.btn_finish_crad)
    Button btn_finish_crad;
    @BindView(R.id.edit_type_one_cr)
    EditText edit_type_one_cr;
    @BindView(R.id.edit_date_cr)
    EditText edit_date_cr;
    @BindView(R.id.edit_name_cr)
    EditText edit_name_cr;
    @BindView(R.id.edit_code_cr)
    EditText edit_code_cr;
    @BindView(R.id.edit_condition_cr)
    EditText edit_condition_cr;
    @BindView(R.id.edit_remark_cr)
    EditText edit_remark_cr;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_crime_add);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("发案登记");
        pickerView = new OptionsPickerView(instance);
        pvTime = new TimePickerView(instance, TimePickerView.Type.YEAR_MONTH_DAY);
    }

    private OptionsPickerView pickerView;
    private TimePickerView pvTime;


    private String time, remark, condition, code;
    private String ajxz = "";
    private String ajlb = "";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish_crad:

                time = edit_date_cr.getText().toString().trim();
                remark = edit_remark_cr.getText().toString().trim();
                condition = edit_condition_cr.getText().toString().trim();
                code = edit_code_cr.getText().toString().trim();
                LogUtils.e(edit_type_one_cr.getText().toString().trim());
                String one = CastTypeUtil.getTypeCode(edit_type_one_cr.getText().toString().trim());
                LogUtils.e(one);
                if (!one.equals("")) {
                    ajxz = one.substring(0, one.indexOf("-"));
                    ajlb = one.substring(one.indexOf("-") + 1, one.length());
                }

                intternet();
                break;
            case R.id.edit_type_one_cr:
                pickerView.show();
                break;
            case R.id.edit_date_cr:
                pvTime.show();
                break;
            case R.id.edit_name_cr:
                Intent intent = new Intent(instance, SearchHotelActivity.class);
                startActivityForResult(intent, 0);
                break;
        }
    }

    @Override
    public void onBackward(View backwardView) {
        super.onBackward(backwardView);
        if (addSuccess) {
            writeConfig("addSuccesscrime", "yes");
            LogUtils.e("--------------" + "yes");
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
                edit_name_cr.setText(name);
            }
        }
    }


    private void intternet() {
        if (mHotelCode.equals("") || mHotelCode == null) {
            Toast.makeText(instance, "请选择酒店", Toast.LENGTH_SHORT).show();
            return;
        }
        if (ajlb.equals("") || ajlb == null) {
            Toast.makeText(instance, "请选择案件类别", Toast.LENGTH_SHORT).show();
            return;
        }
        if (code.equals("") || code == null) {
            Toast.makeText(instance, "请输入案件编号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (time.equals("") || time == null) {
            Toast.makeText(instance, "请选择发案日期", Toast.LENGTH_SHORT).show();
            return;
        }
        LogUtils.e((HttpUrlUtils.getHttpUrl().BothList() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString()));
        OkHttpUtils.post()
                .url(HttpUrlUtils.getHttpUrl().BothList() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .addParams("nohotel", mHotelCode)//
                .addParams("crimedate", DateUtil.data(time))//
                .addParams("remark", remark)//
                .addParams("ajxz", ajxz)//案件性质
                .addParams("ajlb", ajlb)//案件类别
                .addParams("ajbh", code)//案件编号
                .addParams("qkms", condition)//情况描述
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
                        LogUtils.e(mHotelCode);
                        LogUtils.e(DateUtil.data(time));
                        LogUtils.e(remark);
                        LogUtils.e(ajxz);
                        LogUtils.e(ajlb);
                        LogUtils.e(code);
                        LogUtils.e(condition);
                        try {
                            Map<String, Object> data = GsonUtil.JsonToMap(s);
                            if (data.get("state").toString().equals("1")) {
                                showToast(data.get("mess").toString());
                                return;
                            } else if (data.get("state").toString().equals("0")) {
                                addSuccess = true;
                                btn_finish_crad.setText("已发送成功");
                                btn_finish_crad.setEnabled(false);
//                                mIvShopDel.setVisibility(View.GONE);
//                                mIvTerDel.setVisibility(View.GONE);
                            } else {
                                showToast("发送失败");
                            }
                            loadingDialog.dismiss();
                        } catch (Exception e) {
                            showToast("数据格式异常，无法解析");
                        }
                    }
                });


    }

    private boolean addSuccess;

    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {
        btn_finish_crad.setOnClickListener(instance);
        edit_type_one_cr.setOnClickListener(instance);
        edit_date_cr.setOnClickListener(instance);
        edit_name_cr.setOnClickListener(instance);
        checkType();
        checkTime();
    }

    private void checkTime() {
        pvTime.setOnTimeSelectListener(new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                edit_date_cr.setText(df.format(date));
            }
        });
    }

    final ArrayList<String> parent = new ArrayList<>();
    final ArrayList<String> caseType1 = new ArrayList<>();
    final ArrayList<String> caseType2 = new ArrayList<>();
    final ArrayList<ArrayList<String>> child = new ArrayList<>();

    //案件选择
    private void checkType() {
        parent.add("刑事案件类型");
        parent.add("治安案件类型");

        caseType1.add("故意杀人案");
        caseType1.add("抢劫案");
        caseType1.add("盗窃案");
        caseType1.add("诈骗案");
        caseType1.add("其他刑事案件");

        caseType2.add("卖淫嫖娼");
        caseType2.add("赌博");
        caseType2.add("吸毒");
        caseType2.add("其他治安案件");

        child.add(caseType1);
        child.add(caseType2);

        if (null != pickerView) {
            pickerView.setPicker(parent, child, true);
            pickerView.setCyclic(false);

            pickerView.setOnoptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    edit_type_one_cr.setText(parent.get(options1) + " - " + child.get(options1).get(option2));
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
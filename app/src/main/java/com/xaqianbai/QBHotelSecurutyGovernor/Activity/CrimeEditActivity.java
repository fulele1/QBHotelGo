package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.CastTypeUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.EditClearUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.HttpUrlUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.util.LogUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class CrimeEditActivity extends BaseActivityNew {
    private CrimeEditActivity instance;
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

    @BindView(R.id.img_name_clear_cr)
    ImageView img_name_clear_cr;
    @BindView(R.id.img_typeone_clear_cr)
    ImageView img_typeone_clear_cr;
    @BindView(R.id.img_code_clear_cr)
    ImageView img_code_clear_cr;
    @BindView(R.id.img_date_clear_cr)
    ImageView img_date_clear_cr;
    String id;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_crime_add);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("发案编辑");
        pickerView = new OptionsPickerView(instance);
        pvTime = new TimePickerView(instance, TimePickerView.Type.YEAR_MONTH_DAY);
        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        if (!id.equals("")) {
            LogUtils.e(HttpUrlUtils.getHttpUrl().BothList() + "/" + id + "?access_token=" + SPUtils.get(instance, "access_token", ""));
            OkHttpUtils
                    .get()
                    .url(HttpUrlUtils.getHttpUrl().BothList() + "/" + id + "?access_token=" + SPUtils.get(instance, "access_token", ""))
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
                                    Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_LONG).show();
                                    return;
                                } else if (data.get("state").toString().equals("0")) {
                                    edit_name_cr.setText(NullUtil.getString(data.get("hname")));
                                    edit_date_cr.setText(DateUtil.getDate(NullUtil.getString(data.get("crimedate"))));
                                    edit_type_one_cr.setText(NullUtil.getString(data.get("cp_property")) + "-" +
                                            NullUtil.getString(data.get("ct_type")));
                                    edit_code_cr.setText(NullUtil.getString(data.get("ajbh")));
                                    edit_condition_cr.setText(NullUtil.getString(data.get("qkms")));
                                    edit_remark_cr.setText(NullUtil.getString(data.get("remark")));
                                    mHotelCode = NullUtil.getString(data.get("nohotel"));
                                    ajxz = NullUtil.getString(data.get("ajxz"));
                                    ajlb = NullUtil.getString(data.get("ajlb"));
                                     is = true;
                                }
//                                else if (data.get("state").toString().equals("0")) {
//                                    //响应失败
//                                    Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
//                                }
                                else if (data.get("state").toString().equals("10")) {
                                    //响应失败
                                    Toast.makeText(instance, data.get("mess").toString(), Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(instance, LoginActivity.class));
                                    finish();
                                }
                            } catch (Exception e) {
                                Toast.makeText(instance, e.toString(), Toast.LENGTH_SHORT).show();
                            }
                            loadingDialog.dismiss();
                        }
                    });


        }
    }
    boolean is;
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
                String one1 = CastTypeUtil.getTypeCode(edit_type_one_cr.getText().toString().trim());
                LogUtils.e(one1);

                if (!is){
                    if (!one1.equals("")) {
                        ajxz = one1.substring(0, one1.indexOf("-"));
                        ajlb = one1.substring(one1.indexOf("-") + 1, one1.length());
                    }
                }


                loadingDialog.show("");
                Map<String, String> map = new HashMap<>();
                    map.put("nohotel", mHotelCode);
                    map.put("crimedate", DateUtil.data(time));
                    map.put("remark", remark);
                    map.put("ajxz", ajxz);
                    map.put("ajlb", ajlb);
                    map.put("ajbh", code);
                    map.put("qkms", condition);
                    RequestBody body = RequestBody.create(MediaType.parse("application/json"), GsonUtil.GsonString(map));


                    LogUtils.e(GsonUtil.GsonString(map));
                    LogUtils.e(HttpUrlUtils.getHttpUrl().BothList()+"/"+id + "?access_token=" + SPUtils.get(instance, "access_token", "").toString());
                    OkHttpUtils
                            .put()
                            .url(HttpUrlUtils.getHttpUrl().BothList()+"/"+id + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                            .requestBody(body)
                            .build()
                            .execute(new StringCallback() {
                                @Override
                                public void onError(Call call, Exception e, int i) {
                                    showToast(e.toString()+e.getMessage());
                                    LogUtils.e(e.getLocalizedMessage());
                                    LogUtils.e(e.getMessage());
                                    loadingDialog.dismiss();
                                }

                                @Override
                                public void onResponse(String s, int i) {

                                    try {
                                        Map<String, Object> data = GsonUtil.JsonToMap(s);
                                        if (data.get("state").toString().equals("1")) {
                                            showToast(data.get("mess").toString());

                                            return;
                                        } else if (data.get("state").toString().equals("0")) {
                                            addSuccess = true;
                                            btn_finish_crad.setText("已发送成功");
                                            btn_finish_crad.setEnabled(false);
                                        }else if (data.get("state").toString().equals("17")) {
                                            showToast("与原始数据一样，编辑失败");
                                        } else {
                                            showToast("发送失败");
                                        }
                                    } catch (Exception e) {
                                        showToast("数据格式异常，无法解析");
                                    }
                                    loadingDialog.dismiss();

                                }
                            });

//                }

                break;
            case R.id.edit_type_one_cr:
                pickerView.show();
                is = false;
                break;
            case R.id.edit_date_cr:
                pvTime.show();
                break;
            case R.id.edit_name_cr:
                Intent intent = new Intent(instance, SearchHotelActivity.class);
                startActivityForResult(intent, 0);
                break;

            case R.id.img_name_clear_cr:
                edit_name_cr.setText("");
                mHotelCode = "";
                img_name_clear_cr.setVisibility(View.GONE);
                break;

            case R.id.img_typeone_clear_cr:
                edit_type_one_cr.setText("");
                img_typeone_clear_cr.setVisibility(View.GONE);
                break;

            case R.id.img_code_clear_cr:
                edit_code_cr.setText("");
                img_code_clear_cr.setVisibility(View.GONE);
                break;

            case R.id.img_date_clear_cr:
                edit_date_cr.setText("");
                img_date_clear_cr.setVisibility(View.GONE);
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
        img_name_clear_cr.setOnClickListener(instance);
        img_typeone_clear_cr.setOnClickListener(instance);
        img_code_clear_cr.setOnClickListener(instance);
        img_date_clear_cr.setOnClickListener(instance);
        EditClearUtils.clearText(edit_name_cr, img_name_clear_cr);
        EditClearUtils.clearText(edit_type_one_cr, img_typeone_clear_cr);
        EditClearUtils.clearText(edit_code_cr, img_code_clear_cr);
        EditClearUtils.clearText(edit_date_cr, img_date_clear_cr);

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

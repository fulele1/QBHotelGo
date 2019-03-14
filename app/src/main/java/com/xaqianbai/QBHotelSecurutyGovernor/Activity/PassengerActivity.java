package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.CastTypeUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DialogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DoubleDateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.EditClearUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PassengerActivity extends AppCompatActivity implements View.OnClickListener {

    private Unbinder unbinder;
    private PassengerActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.edit_name_passenger)
    EditText edit_name;
    @BindView(R.id.edit_iden_passenger)
    EditText edit_iden;
    @BindView(R.id.edit_tel_passenger)
    EditText edit_tel;
    @BindView(R.id.edit_date_passenger)
    EditText edit_date;
    @BindView(R.id.img_clear_date_pass)
    ImageView img_clear_date;
    @BindView(R.id.img_clear_ptype_pass)
    ImageView img_clear_ptype;
    @BindView(R.id.img_clear_name_pass)
    ImageView img_clear_name_pass;
    @BindView(R.id.img_clear_iden_pass)
    ImageView img_clear_iden_pass;
    @BindView(R.id.img_clear_tel_pass)
    ImageView img_clear_tel_pass;
    @BindView(R.id.img_clear_identype_pass)
    ImageView img_clear_identype;
    @BindView(R.id.et_per_type_pass)
    EditText et_per_type;
    @BindView(R.id.et_iden_type_pass)
    EditText et_iden_type;
    @BindView(R.id.btn_quary_passenger)
    Button btn_quary;
    @BindView(R.id.edit_nation_passenger)
    EditText edit_nation_passenger;
    @BindView(R.id.img_clear_nation_pass)
    ImageView img_clear_nation_pass;
    @BindView(R.id.edit_area_passenger)
    EditText edit_area_passenger;
    @BindView(R.id.img_clear_area_pass)
    ImageView img_clear_area_pass;
    @BindView(R.id.edit_age_passenger)
    EditText edit_age_passenger;
    @BindView(R.id.img_clear_age_pass)
    ImageView img_clear_age_pass;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("旅客查询");
        event();
    }

    private void event() {
        edit_date.setOnClickListener(instance);
        img_clear_date.setOnClickListener(instance);
        btn_quary.setOnClickListener(instance);
        img_clear_ptype.setOnClickListener(instance);
        et_per_type.setOnClickListener(instance);
        img_clear_identype.setOnClickListener(instance);
        et_iden_type.setOnClickListener(instance);
        img_clear_name_pass.setOnClickListener(instance);
        img_clear_iden_pass.setOnClickListener(instance);
        img_clear_tel_pass.setOnClickListener(instance);
        edit_nation_passenger.setOnClickListener(instance);
        img_clear_nation_pass.setOnClickListener(instance);
        edit_area_passenger.setOnClickListener(instance);
        img_clear_area_pass.setOnClickListener(instance);
        edit_age_passenger.setOnClickListener(instance);
        img_clear_age_pass.setOnClickListener(instance);
        EditClearUtils.clearText(edit_date, img_clear_date);
        EditClearUtils.clearText(et_iden_type, img_clear_identype);
        EditClearUtils.clearText(et_per_type, img_clear_ptype);
        EditClearUtils.clearText(edit_name, img_clear_name_pass);
        EditClearUtils.clearText(edit_iden, img_clear_iden_pass);
        EditClearUtils.clearText(edit_tel, img_clear_tel_pass);
        EditClearUtils.clearText(edit_nation_passenger, img_clear_nation_pass);
        EditClearUtils.clearText(edit_area_passenger, img_clear_area_pass);
        EditClearUtils.clearText(edit_age_passenger, img_clear_age_pass);
    }

    public void onBackward(View view) {
        instance.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edit_date_passenger://查询时间
                DoubleDateUtil.show(instance, edit_date);
                break;
            case R.id.img_clear_date_pass://清除时间
                edit_date.setText("");
                mStart = "";
                mEnd = "";
                img_clear_date.setVisibility(View.GONE);
                break;

            case R.id.img_clear_name_pass:
                edit_name.setText("");
                img_clear_name_pass.setVisibility(View.GONE);
                break;

            case R.id.img_clear_iden_pass://
                edit_iden.setText("");
                img_clear_iden_pass.setVisibility(View.GONE);
                break;

            case R.id.img_clear_tel_pass://
                edit_tel.setText("");
                img_clear_tel_pass.setVisibility(View.GONE);
                break;
            case R.id.et_per_type_pass://查询旅客类型
                DialogUtils.showItemDialog(instance, "类型选择", new String[]{"中国大陆", "国外", "港澳"}, et_per_type);
                break;
            case R.id.edit_age_passenger://
                DialogUtils.showItemDialog(instance, "类型选择", new String[]{"0 - 18", "19 - 35",
                        "36 - 50", "51 - 100"}, edit_age_passenger);
                break;
            case R.id.edit_nation_passenger://
                if (et_per_type.getText().toString().trim().equals("中国大陆")) {
                    DialogUtils.showItemDialog(instance, "类型选择", new String[]{"汉族", "蒙古族", "回族", "藏族", "维吾尔族", "苗族", "彝族", "壮族", "布依族", "朝鲜族", "满族", "侗族", "瑶族", "白族", "土家族",
                            "哈尼族", "哈萨克族", "傣族", "黎族", "傈僳族", "佤族", "畲族", "高山族", "拉祜族", "水族", "东乡族", "纳西族", "景颇族", "柯尔克孜族",
                            "土族", "达斡尔族", "仫佬族", "羌族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "塔吉克族", "怒族", "乌孜别克族",
                            "俄罗斯族", "鄂温克族", "德昂族", "保安族", "裕固族", "京族", "塔塔尔族", "独龙族", "鄂伦春族", "赫哲族", "门巴族", "珞巴族", "基诺族"
                    }, edit_nation_passenger);
                } else if (et_per_type.getText().toString().trim().equals("国外")) {
                    DialogUtils.showItemDialog(instance, "类型选择", new String[]{"阿鲁巴", "阿富汗", "安哥拉", "安圭拉", "阿尔巴尼亚", "安道尔", "荷属安的列斯", "阿联酋",
                            "阿根廷", "亚美尼亚", "美属萨摩亚", "南极洲", "法属南部领土", "安提瓜和巴布达", "澳大利亚",
                            "奥地利", "阿塞拜疆", "布隆迪", "比利时", "贝宁", "布基纳法索", "孟加拉国", "保加利亚", "巴林", "巴哈马", "波黑", "白俄罗斯", "伯利兹", "百慕大",
                            "玻利维亚", "巴西", "巴巴多斯", "文莱", "不丹", "布维岛", "博茨瓦纳", "中非", "加拿大", "科科斯群岛", "瑞士", "智利", "中国", "科特迪瓦",
                            "喀麦隆", "刚果(金)", "刚果(布)", "库克群岛", "哥伦比亚", "科摩罗", "佛得角", "哥斯达黎加", "古巴", "圣诞岛", "开曼群岛", "塞浦路斯", "捷克",
                            "德国", "吉布提", "多米尼克", "丹麦", "多米尼加", "阿尔及利亚", "厄瓜多尔", "埃及", "厄立特里亚", "西撒哈拉", "西班牙", "爱沙尼亚", "埃塞俄比亚",
                            "芬兰", "斐济", "马尔维纳斯群岛", "法国", "法罗群岛", "密克罗尼西亚", "加蓬", "英国（独立领土公民，出国不用）", "英国（海外公民，出国不用）",
                            "英国（保护公民，出国不用）", "英国", "英国（隶属，出国不用）", "格鲁吉亚",
                            "加纳", "直布罗陀", "几内亚", "瓜德罗普", "冈比亚", "几内亚比绍", "赤道几内亚", "希腊", "格林纳达", "格陵兰", "危地马拉", "法属圭亚那",
                            "关岛", "圭亚那", "香港", "赫德岛和麦克唐纳岛", "洪都拉斯"
                    }, edit_nation_passenger);

                } else if (et_per_type.getText().toString().trim().equals("港澳")) {

                    DialogUtils.showItemDialog(instance, "类型选择", new String[]{"阿鲁巴", "阿富汗", "安哥拉", "安圭拉", "阿尔巴尼亚", "安道尔", "荷属安的列斯", "阿联酋",
                            "阿根廷", "亚美尼亚", "美属萨摩亚", "南极洲", "法属南部领土", "安提瓜和巴布达", "澳大利亚",
                            "奥地利", "阿塞拜疆", "布隆迪", "比利时", "贝宁", "布基纳法索", "孟加拉国", "保加利亚", "巴林", "巴哈马", "波黑", "白俄罗斯", "伯利兹", "百慕大",
                            "玻利维亚", "巴西", "巴巴多斯", "文莱", "不丹", "布维岛", "博茨瓦纳", "中非", "加拿大", "科科斯群岛", "瑞士", "智利", "中国", "科特迪瓦",
                            "喀麦隆", "刚果(金)", "刚果(布)", "库克群岛", "哥伦比亚", "科摩罗", "佛得角", "哥斯达黎加", "古巴", "圣诞岛", "开曼群岛", "塞浦路斯", "捷克",
                            "德国", "吉布提", "多米尼克", "丹麦", "多米尼加", "阿尔及利亚", "厄瓜多尔", "埃及", "厄立特里亚", "西撒哈拉", "西班牙", "爱沙尼亚", "埃塞俄比亚",
                            "芬兰", "斐济", "马尔维纳斯群岛", "法国", "法罗群岛", "密克罗尼西亚", "加蓬", "英国（独立领土公民，出国不用）", "英国（海外公民，出国不用）",
                            "英国（保护公民，出国不用）", "英国", "英国（隶属，出国不用）", "格鲁吉亚",
                            "加纳", "直布罗陀", "几内亚", "瓜德罗普", "冈比亚", "几内亚比绍", "赤道几内亚", "希腊", "格林纳达", "格陵兰", "危地马拉", "法属圭亚那",
                            "关岛", "圭亚那", "香港", "赫德岛和麦克唐纳岛", "洪都拉斯"
                    }, edit_nation_passenger);

                } else {
                    Toast.makeText(instance, "请选择旅客类型", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.img_clear_ptype_pass://清除旅客类型
                et_per_type.setText("");
                img_clear_ptype.setVisibility(View.GONE);
                break;
            case R.id.et_iden_type_pass://查询证件类型
                DialogUtils.showItemDialog(instance, "类型选择", new String[]{"身份证", "户口本", "中国香港居民居住证", "中国澳门居民居住证", "中国台湾居民居住证",
                        "军官证", "警官证", "士兵证", "国内护照", "驾照", "港澳通行证", "其他"}, et_iden_type);
                break;
            case R.id.img_clear_identype_pass://清除证件类型
                et_iden_type.setText("");
                img_clear_identype.setVisibility(View.GONE);
                break;
            case R.id.img_clear_nation_pass://
                mNationcCode = "";
                edit_nation_passenger.setText("");
                img_clear_nation_pass.setVisibility(View.GONE);
                break;
            case R.id.edit_area_passenger://
                Intent intent = new Intent(instance, SearchOrgActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.img_clear_area_pass://
                org = "";

                edit_area_passenger.setText("");
                img_clear_area_pass.setVisibility(View.GONE);
                break;
            case R.id.img_clear_age_pass://
                bdate = "";
                edit_age_passenger.setText("");
                img_clear_age_pass.setVisibility(View.GONE);
                break;
            case R.id.btn_quary_passenger://查询
                getIntentData();
                if (perType.equals("") && mName.equals("") && mIden.equals("") && mTel.equals("") && mDate.equals("")) {
                    Toast.makeText(instance, "请选择查询条件", Toast.LENGTH_SHORT).show();
                } else {
                    if (perType.equals("")) {
                        Toast.makeText(instance, "请选择旅客类型", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!idenType.equals("")) {
                        if (mIden.equals("")) {
                            Toast.makeText(instance, "请填写证件号码", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (!mIden.equals("")) {
                        if (idenType.equals("")) {
                            Toast.makeText(instance, "请选择证件类型", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    LogUtils.e("mStart111111111" + mStart);
                    LogUtils.e("mEnd1111111" + mEnd);
                    Intent i = new Intent(instance, PassengerListActivity.class);
                    i.putExtra("nameType", perType);
                    i.putExtra("name", mName);
                    i.putExtra("idenType", idenType);
                    i.putExtra("iden", mIden);
                    i.putExtra("tel", mTel);
                    i.putExtra("start", mStart);
                    i.putExtra("end", mEnd);
                    i.putExtra("psorgan", org);
                    i.putExtra("bdate", bdate);
                    LogUtils.e("lllllllllllllll" + edit_nation_passenger.getText().toString().trim());
                    LogUtils.e("lllllllllllllll" + edit_age_passenger.getText().toString().trim());
                    LogUtils.e("lllllllllllllll" + CastTypeUtil.getnationno(edit_nation_passenger.getText().toString().trim()));
                    i.putExtra("nation", mNationcCode);
                    startActivity(i);
                }
                break;
        }
    }

    private String mName, mIden, mTel, mDate;
    private String idenType;
    private String perType;
    private String mStart = "";
    private String mEnd = "";

    String org = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String name = bundle.getString("so_name");
                String code = bundle.getString("so_code");
                org = code;
                edit_area_passenger.setText(name);
            }
        }
    }

    String mNationcCode = "";
    String bdate = "";

    private void getIntentData() {
        mName = edit_name.getText().toString().trim();
        mIden = edit_iden.getText().toString().trim();
        mTel = edit_tel.getText().toString().trim();
        mDate = edit_date.getText().toString().trim();
        String inenty = et_iden_type.getText().toString().trim();
        String perty = et_per_type.getText().toString().trim();

        if (et_per_type.getText().toString().trim().equals("中国大陆")) {

            mNationcCode = CastTypeUtil.getnationno(edit_nation_passenger.getText().toString().trim());
        } else if (et_per_type.getText().toString().trim().equals("国外")) {
            mNationcCode = CastTypeUtil.getcontryno(edit_nation_passenger.getText().toString().trim());
        } else if (et_per_type.getText().toString().trim().equals("港澳")) {
            mNationcCode = CastTypeUtil.getcontryno(edit_nation_passenger.getText().toString().trim());
        } else {
            Toast.makeText(instance, "请选择证件类型", Toast.LENGTH_SHORT).show();
        }

        bdate = edit_age_passenger.getText().toString().trim();

        if (!mDate.equals("")) {
            mStart = NullUtil.getString(mDate.substring(0, mDate.indexOf("--->")));
            mEnd = NullUtil.getString(mDate.substring(mDate.indexOf("--->") + 4));
        }

        LogUtils.e("mStart" + mStart);
        LogUtils.e("mEnd" + mEnd);

        LogUtils.e("inenty" + inenty);
        LogUtils.e("perty" + perty);
        if (inenty.equals("身份证")) {
            idenType = "11";
        } else if (inenty.equals("户口本")) {
            idenType = "13";
        } else if (inenty.equals("中国香港居民居住证")) {
            idenType = "81";
        } else if (inenty.equals("中国澳门居民居住证")) {
            idenType = "82";
        } else if (inenty.equals("中国台湾居民居住证")) {
            idenType = "83";
        } else if (inenty.equals("军官证")) {
            idenType = "90";
        } else if (inenty.equals("警官证")) {
            idenType = "91";
        } else if (inenty.equals("士兵证")) {
            idenType = "92";
        } else if (inenty.equals("国内护照")) {
            idenType = "93";
        } else if (inenty.equals("驾照")) {
            idenType = "94";
        } else if (inenty.equals("港澳通行证")) {
            idenType = "95";
        } else if (inenty.equals("其他")) {
            idenType = "99";
        } else {
            idenType = "";
        }
        if (perty.equals("中国大陆")) {
            perType = "1";
        } else if (perty.equals("国外")) {
            perType = "2";
        } else if (perty.equals("港澳")) {
            perType = "3";
        } else {
            perType = "";
        }

    }

}
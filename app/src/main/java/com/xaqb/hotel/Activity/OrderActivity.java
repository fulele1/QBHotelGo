package com.xaqb.hotel.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompatSideChannelService;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.DateUtil;
import com.xaqb.hotel.Utils.DialogUtils;
import com.xaqb.hotel.Utils.EditClearUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.xaqb.hotel.Views.DoubleDatePickerDialog;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderActivity extends BaseActivityNew implements View.OnClickListener{

    private Unbinder unbinder;
    private OrderActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.edit_org_order)
    EditText edit_org;
    @BindView(R.id.edit_hname_order)
    EditText edit_hname;
    @BindView(R.id.edit_pername_order)
    EditText edit_pername;
    @BindView(R.id.edit_date_order)
    EditText edit_date;
    @BindView(R.id.img_clear_org_order)
    ImageView img_clear_org;
    @BindView(R.id.img_clear_date_order)
    ImageView img_clear_date;
    @BindView(R.id.img_per_type_order)
    ImageView img_per_type;
    @BindView(R.id.btn_quary_order)
    Button btn_quary;
    @BindView(R.id.et_per_type_order)
    EditText et_per_type;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void initViews() throws Exception {
        setContentView(R.layout.activity_order);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        title.setText("订单查询");
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        event();
    }

    private void event() {
        edit_org.setOnClickListener(instance);
        edit_date.setOnClickListener(instance);
        btn_quary.setOnClickListener(instance);
        et_per_type.setOnClickListener(instance);
        img_clear_org.setOnClickListener(instance);
        img_clear_date.setOnClickListener(instance);
        img_per_type.setOnClickListener(instance);
        EditClearUtils.clearText(edit_org,img_clear_org);
        EditClearUtils.clearText(edit_date,img_clear_date);
        EditClearUtils.clearText(et_per_type,img_per_type);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }


    @Override
    public void initData() throws Exception {

    }

    @Override
    public void addListener() throws Exception {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.et_per_type_order://订单类型
                DialogUtils.showItemDialog(instance,"类型选择",new String [] {"中国大陆","国外","港澳"},et_per_type);
                break;
            case R.id.img_per_type_order://清除订单类型
                et_per_type.setText("");
                img_per_type.setVisibility(View.GONE);
                break;
            case R.id.edit_org_order://管辖机构
                Intent intent = new Intent(instance, SearchOrgActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.img_clear_org_order://清除管辖机构
                edit_org.setText("");
                mOrg = "";
                img_clear_org.setVisibility(View.GONE);
                break;
            case R.id.edit_date_order://成立时间
                Calendar c = Calendar.getInstance();
                new DoubleDatePickerDialog(instance, 0, new DoubleDatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                          int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                          int endDayOfMonth) {
                        String textString = String.format("%d-%d-%d--->%d-%d-%d", startYear,
                                startMonthOfYear + 1, startDayOfMonth, endYear, endMonthOfYear + 1, endDayOfMonth);
                        edit_date.setText(textString);
                    }
                }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), false).show();

                break;
            case R.id.img_clear_date_order://清除成立时间
                edit_date.setText("");
                img_clear_date.setVisibility(View.GONE);
                break;
            case R.id.btn_quary_order://查询
                getIntentData();
                if (mType.equals("")){
                    Toast.makeText(instance, "请选择订单类型", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    Intent i = new Intent(instance, OrderListActivity.class);
                    i.putExtra("type", mType);
                    i.putExtra("org", mOrg);
                    i.putExtra("hName", mHName);
                    i.putExtra("perName", mPerName);
                    i.putExtra("start", mStart);
                    i.putExtra("end", mEnd);
                    startActivity(i);
                }
                break;
        }
    }

    private String mOrg = "";
    private String mHName,mPerName,mDate,mType,mStart,mEnd;
    private void getIntentData() {
        mHName = edit_hname.getText().toString().trim();
        mPerName = edit_pername.getText().toString().trim();
        mDate = edit_date.getText().toString().trim();
        if (!mDate.equals("")){
            mStart = NullUtil.getString(mDate.substring(0, mDate.indexOf("--->")));
            mEnd = NullUtil.getString(mDate.substring(mDate.indexOf("--->")+4));
        }
        String type = et_per_type.getText().toString().trim();
        if (type.equals("中国大陆")){
            mType = "1";
        }else if (type.equals("国外")){
            mType = "2";
        }else if (type.equals("港澳")){
            mType = "3";
        }else{
            mType = "";
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String name = bundle.getString("so_name");
                String code = bundle.getString("so_code");
                mOrg = code;
                edit_org.setText(name);
            }
        }
    }

}

package com.xaqb.hotel.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PassengerActivity extends AppCompatActivity implements View.OnClickListener{

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
    @BindView(R.id.img_clear_identype_pass)
    ImageView img_clear_identype;
    @BindView(R.id.et_per_type_pass)
    EditText et_per_type;
    @BindView(R.id.et_iden_type_pass)
    EditText et_iden_type;
    @BindView(R.id.btn_quary_passenger)
    Button btn_quary;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
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
        EditClearUtils.clearText(edit_date,img_clear_date);
        EditClearUtils.clearText(et_iden_type,img_clear_identype);
        EditClearUtils.clearText(et_per_type,img_clear_ptype);
    }

    public void onBackward(View view){
        instance.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_date_passenger://查询时间
                chooseDatePicker();
            break;
            case R.id.img_clear_date_pass://清除时间
                edit_date.setText("");
                img_clear_date.setVisibility(View.GONE);
            break;
            case R.id.et_per_type_pass://查询旅客类型
                DialogUtils.showItemDialog(instance,"类型选择",new String[]{"中国大陆","国外","港澳"},et_per_type);
            break;
            case R.id.img_clear_ptype_pass://清除旅客类型
                et_per_type.setText("");
                img_clear_ptype.setVisibility(View.GONE);
            break;
            case R.id.et_iden_type_pass://查询证件类型
                DialogUtils.showItemDialog(instance,"类型选择",new String[]{"身份证","户口本","军官证","警官证","士兵证","国内护照","驾照","港澳通行证","其他"},et_iden_type);
            break;
            case R.id.img_clear_identype_pass://清除证件类型
                et_iden_type.setText("");

                img_clear_identype.setVisibility(View.GONE);
            break;
            case R.id.btn_quary_passenger://查询
                getIntentData();
                if (perType.equals("")&&mName.equals("") && mIden.equals("") && mTel.equals("") && mDate.equals("")) {
                    Toast.makeText(instance, "请选择查询条件", Toast.LENGTH_SHORT).show();
                } else {
                    if (perType.equals("")){
                        Toast.makeText(instance,"请选择旅客类型",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (!idenType.equals("")){
                        if (mIden.equals("")){
                        Toast.makeText(instance,"请填写证件号码",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    if (!mIden.equals("")){
                        if (idenType.equals("")){
                        Toast.makeText(instance,"请选择证件类型",Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    Intent i = new Intent(instance, PassengerListActivity.class);
                    i.putExtra("nameType", perType);
                    i.putExtra("name", mName);
                    i.putExtra("idenType", idenType);
                    i.putExtra("iden", mIden);
                    i.putExtra("tel", mTel);
                    i.putExtra("date", mDate);
                    startActivity(i);
                }
            break;
        }
    }
    private String mName,mIden,mTel,mDate;
    private String idenType;
    private String perType;
    private void getIntentData(){
        mName = edit_name.getText().toString().trim();
        mIden = edit_iden.getText().toString().trim();
        mTel = edit_tel.getText().toString().trim();
        mDate = NullUtil.getString(DateUtil.data(edit_date.getText().toString().trim()));
        String inenty = et_iden_type.getText().toString().trim();
        String perty = et_per_type.getText().toString().trim();
        LogUtils.e("inenty"+inenty);
        LogUtils.e("perty"+perty);
        if (inenty.equals("身份证")){
            idenType = "11";
        }else if (inenty.equals("户口本")){
            idenType = "13";
        }else if (inenty.equals("军官证")){
            idenType = "90";
        }else if (inenty.equals("警官证")){
            idenType = "91";
        }else if (inenty.equals("士兵证")){
            idenType = "92";
        }else if (inenty.equals("国内护照")){
            idenType = "93";
        }else if (inenty.equals("驾照")){
            idenType = "94";
        }else if (inenty.equals("港澳通行证")){
            idenType = "95";
        }else if (inenty.equals("其他")){
            idenType = "99";
        }else{
            idenType = "";
        }


        if (perty.equals("中国大陆")){
            perType = "1";
        }else if (perty.equals("国外")){
            perType = "2";
        }else if (perty.equals("港澳")){
            perType = "3";
        }else {
            perType = "";
        }

    }


    public void chooseDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(instance, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1 + 1;
                String datainner = i + "-" + i1 + "-" + i2;
                edit_date.setText(datainner);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
}
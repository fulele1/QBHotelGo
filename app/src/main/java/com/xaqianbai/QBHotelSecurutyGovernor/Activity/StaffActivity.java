package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.EditClearUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class StaffActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.btn_quary_staff)
    Button btn_quary;
    @BindView(R.id.edit_hotel_staff)//酒店名称
            EditText edit_hotel;
    @BindView(R.id.edit_name_staff)//姓名
            EditText edit_name;
    @BindView(R.id.edit_tel_staff)//电话号码
            EditText edit_tel;
    @BindView(R.id.edit_iden_staff)//证件号码
            EditText edit_iden;
    @BindView(R.id.edit_org_staff)//管辖机构
            EditText edit_org;
    @BindView(R.id.img_clear_staff)//清空管辖机构
            ImageView img_clear;
    @BindView(R.id.img_hotel_clear_staff)
            ImageView img_hotel_clear_staff;
    @BindView(R.id.img_name_clear_staff)
            ImageView img_name_clear_staff;
    @BindView(R.id.img_tel_clear_staff)
            ImageView img_tel_clear_staff;
    @BindView(R.id.img_iden_clear_staff)
            ImageView img_iden_clear_staff;
    private Unbinder unbinder;
    private StaffActivity instance;
    private String hotel;
    private String name;
    private String tel;
    private String iden;
    private String org = "";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        StatuBarUtil.setStatuBarLightMode(instance, getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("从业人员查询");
        event();
    }

    private void event() {
        btn_quary.setOnClickListener(instance);
        edit_org.setOnClickListener(instance);
        img_clear.setOnClickListener(instance);
        img_hotel_clear_staff.setOnClickListener(instance);
        img_name_clear_staff.setOnClickListener(instance);
        img_tel_clear_staff.setOnClickListener(instance);
        img_iden_clear_staff.setOnClickListener(instance);
        EditClearUtils.clearText(edit_org,img_clear);
        EditClearUtils.clearText(edit_hotel,img_hotel_clear_staff);
        EditClearUtils.clearText(edit_name,img_name_clear_staff);
        EditClearUtils.clearText(edit_tel,img_tel_clear_staff);
        EditClearUtils.clearText(edit_iden,img_iden_clear_staff);
    }

    public void onBackward(View view) {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_quary_staff://查询
                getIntentData();
                if (hotel.equals("") && name.equals("") && tel.equals("") && iden.equals("") && org.equals("")) {
                    Toast.makeText(instance, "请选择查询条件", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(instance, StaffListActivity.class);
                    i.putExtra("hotel", hotel);
                    i.putExtra("name", name);
                    i.putExtra("tel", tel);
                    i.putExtra("iden", iden);
                    i.putExtra("org", org);
                    startActivity(i);
                }
                break;
            case R.id.edit_org_staff://管辖机构
                Intent intent = new Intent(instance, SearchOrgActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.img_clear_staff://清空管辖机构
                edit_org.setText("");
                org="";
                img_clear.setVisibility(View.GONE);
                break;
                case R.id.img_hotel_clear_staff:
                    edit_hotel.setText("");
                    img_hotel_clear_staff.setVisibility(View.GONE);
                break;
                case R.id.img_name_clear_staff://
                    edit_name.setText("");
                    img_name_clear_staff.setVisibility(View.GONE);
                break;
                case R.id.img_tel_clear_staff://
                    edit_tel.setText("");
                    img_tel_clear_staff.setVisibility(View.GONE);
                break;
                case R.id.img_iden_clear_staff://
                    edit_iden.setText("");
                    img_iden_clear_staff.setVisibility(View.GONE);
                break;
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
                org = code;
                edit_org.setText(name);
            }
        }
    }

    public void getIntentData() {
        hotel = edit_hotel.getText().toString().trim();
        name = edit_name.getText().toString().trim();
        tel = edit_tel.getText().toString().trim();
        iden = edit_iden.getText().toString().trim();
    }
}

package com.xaqb.hotel.Activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.DateUtil;
import com.xaqb.hotel.Utils.EditClearUtils;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.StatuBarUtil;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class HotelActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.edit_org_hotel)
    EditText edit_org;
    @BindView(R.id.edit_name_hotel)
    EditText edit_name;
    @BindView(R.id.edit_start_hotel)
    EditText edit_start;
    @BindView(R.id.edit_end_hotel)
    EditText edit_end;
    @BindView(R.id.img_clear_org_hotel)
    ImageView img_clear_org;
    @BindView(R.id.img_clear_start_hotel)
    ImageView img_clear_start;
    @BindView(R.id.img_clear_end_hotel)
    ImageView img_clear_end;
    @BindView(R.id.btn_quary_hotel)
    Button btn_quary;
    private Unbinder unbinder;
    private HotelActivity instance;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel);
        instance = this;
        unbinder = ButterKnife.bind(instance);
        title.setText("酒店查询");
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        event();
    }

    private void event() {
        btn_quary.setOnClickListener(instance);
        edit_org.setOnClickListener(instance);
        edit_start.setOnClickListener(instance);
        edit_end.setOnClickListener(instance);
        img_clear_org.setOnClickListener(instance);
        img_clear_start.setOnClickListener(instance);
        img_clear_end.setOnClickListener(instance);
        EditClearUtils.clearText(edit_org,img_clear_org);
        EditClearUtils.clearText(edit_start,img_clear_start);
        EditClearUtils.clearText(edit_end,img_clear_end);
    }

    public void onBackward(View view){
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.edit_org_hotel://管辖机构
                Intent intent = new Intent(instance, SearchAreaActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.edit_start_hotel://成立时间
                chooseDatePicker(edit_start);
                break;
            case R.id.edit_end_hotel://截止时间
                chooseDatePicker(edit_end);

                break;
            case R.id.img_clear_org_hotel://清除管辖机构
                edit_org.setText("");
                mOrg = "";
                img_clear_org.setVisibility(View.GONE);
                break;
            case R.id.img_clear_start_hotel://清除成立时间
                edit_start.setText("");
                img_clear_start.setVisibility(View.GONE);
                break;
            case R.id.img_clear_end_hotel://清除截止时间
                edit_end.setText("");
                img_clear_end.setVisibility(View.GONE);
                break;
            case R.id.btn_quary_hotel://查询
                getIntentData();
                if (mOrg.equals("") && mName.equals("") && mStart.equals("") && mEnd.equals("")) {
                    Toast.makeText(instance, "请选择查询条件", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(instance, HotelListActivity.class);
                    i.putExtra("org", mOrg);
                    i.putExtra("name", mName);
                    i.putExtra("start", mStart);
                    i.putExtra("end", mEnd);
                    startActivity(i);
                }

                break;
        }
    }

    public void chooseDatePicker(final EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(instance, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                i1 = i1 + 1;
                String datainner = i + "-" + i1 + "-" + i2;
                editText.setText(datainner);
            }
        }, year, month, day);
        datePickerDialog.show();
    }
    private String mOrg = "";
    private String mName,mStart,mEnd;
    private void getIntentData() {
        mName = edit_name.getText().toString().trim();
        mStart = NullUtil.getString(DateUtil.data(edit_start.getText().toString().trim()));
        mEnd = NullUtil.getString(DateUtil.data(edit_end.getText().toString().trim()));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String name = bundle.getString("ha_name");
                String code = bundle.getString("ha_code");
                mOrg = code;
                edit_org.setText(name);
            }
        }
    }

}

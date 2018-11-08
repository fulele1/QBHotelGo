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
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.DoubleDateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.EditClearUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.NullUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class OrgActivity extends AppCompatActivity implements View.OnClickListener{

    private Unbinder unbinder;
    private OrgActivity instance;
    @BindView(R.id.tv_title)
    TextView title;
    @BindView(R.id.layout_titlebar)
    FrameLayout titlebar;
    @BindView(R.id.edit_org_org)
    EditText edit_org;
    @BindView(R.id.edit_time_org)
    EditText edit_time;
    @BindView(R.id.img_clear_org_org)
    ImageView img_clear_org;
    @BindView(R.id.img_clear_start_org)
    ImageView img_clear_start;
    @BindView(R.id.btn_quary_org)
    Button btn_quary;



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        instance = this;
        unbinder = ButterKnife.bind(this);
        StatuBarUtil.setStatuBarLightMode(this,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        titlebar.setBackgroundColor(getResources().getColor(R.color.white));
        title.setText("辖区统计");
        event();
    }

    private void event() {
        edit_org.setOnClickListener(instance);
        edit_time.setOnClickListener(instance);
        img_clear_org.setOnClickListener(instance);
        img_clear_start.setOnClickListener(instance);
        btn_quary.setOnClickListener(instance);
        EditClearUtils.clearText(edit_org,img_clear_org);
        EditClearUtils.clearText(edit_time,img_clear_start);
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
            case R.id.edit_org_org://管辖机构
                Intent intent = new Intent(instance, SearchOrgActivity.class);
                startActivityForResult(intent, 0);
                break;
            case R.id.edit_time_org://成立时间
                DoubleDateUtil.show(instance,edit_time);
                break;
            case R.id.img_clear_org_org://清除管辖机构
                edit_org.setText("");
                mOrg = "";
                img_clear_org.setVisibility(View.GONE);
                break;
            case R.id.img_clear_start_org://清除成立时间
                edit_time.setText("");
                mEnd = "";
                mStart = "";
                img_clear_start.setVisibility(View.GONE);
                break;
            case R.id.btn_quary_org://查询
                getIntentData();
                if (mOrg.equals("") && mTime.equals("") ) {
                    Toast.makeText(instance, "请选择查询条件", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(instance, OrgDetileActivity.class);
                    i.putExtra("org", mOrg);
                    i.putExtra("start", mStart);
                    i.putExtra("end", mEnd);
                    startActivity(i);
                }
                break;
        }
    }

    private String mOrg = "";
    private String mTime;
    private String mEnd = "";
    private String mStart = "";
    private void getIntentData() {
        mTime = NullUtil.getString(edit_time.getText().toString().trim());

        if (!mTime.equals("")){
            mStart = NullUtil.getString(mTime.substring(0, mTime.indexOf("--->")));
            mEnd = NullUtil.getString(mTime.substring(mTime.indexOf("--->")+4));
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
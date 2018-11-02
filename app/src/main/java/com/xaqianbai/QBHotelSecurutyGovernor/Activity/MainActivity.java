package com.xaqianbai.QBHotelSecurutyGovernor.Activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;
import com.xaqianbai.QBHotelSecurutyGovernor.Fragment.LeftFragment;
import com.xaqianbai.QBHotelSecurutyGovernor.R;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.ActivityController;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.ChartUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GlideRoundTransform;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.GsonUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.LogUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.SPUtils;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.StatuBarUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.Utils.UpdateUtil;
import com.xaqianbai.QBHotelSecurutyGovernor.zxing.activity.CaptureActivity;


import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 主页面
 */
public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener {
    private MainActivity instance;
    private String au_version;
    private boolean isQuit = false;
    private Fragment mContent;
    private SlidingMenu sm;


    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isQuit = false;
        }
    };

    private Unbinder mUnbinder;
    @BindView(R.id.iv_user)
    ImageView mIvUser;
    @BindView(R.id.chart_line_main)
    LineChart mLine;
    @BindView(R.id.pie_main)
    PieChart mPie;
    @BindView(R.id.name_main)
    TextView mName;
    @BindView(R.id.soname_main)
    TextView mSoName;
    @BindView(R.id.hotel_count_main)
    TextView mHotelCount;
    @BindView(R.id.staff_count_main)
    TextView mStaffCount;
    @BindView(R.id.hotel_tourist_main)
    TextView mTouristCount;
    @BindView(R.id.ou_headpic_main)
    ImageView mHeadPic;
    @BindView(R.id.txt_passenger_main)
    TextView mTxtPass;
    @BindView(R.id.txt_staff_main)
    TextView mTxtStaff;
    @BindView(R.id.txt_hotel_main)
    TextView mTxtHotel;
    @BindView(R.id.txt_data_main)
    TextView mTxtData;
    @BindView(R.id.txt_order_main)
    TextView txt_order;
    @BindView(R.id.progress_text_main)
    TextView progress_text;
    @BindView(R.id.iv_zxing)
    ImageView iv_zxing;
    @BindView(R.id.progress_main)
    PieChart mProgress;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        instance = this;
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.bag));//修改状态栏字体颜色为黑色
        mUnbinder = ButterKnife.bind(instance);
        ActivityController.addActivity(instance);
        new UpdateUtil(this,"20").getVersion();//检查时候有新版本
        addListener();
        initSlidingMenu(savedInstanceState);

        setLine();
        setPie();
        setPieProgress();
    }


    private void setSPData() {
        mName.setText(SPUtils.get(instance, "ou_nickname", "").toString());
        mSoName.setText(SPUtils.get(instance, "so_name", "").toString());
        mHotelCount.setText(SPUtils.get(instance, "ho_count", "").toString());
        mStaffCount.setText(SPUtils.get(instance, "staff_count", "").toString());
        mTouristCount.setText(SPUtils.get(instance, "tourist_count", "").toString());
        String url = SPUtils.get(instance, "ou_headpic", "http").toString();
         if (url != null && !url.equals("")){
            Glide.with(instance)
                    .load(url)
                    .skipMemoryCache(false)//防止大图因为内存问题无法加载
                    .transform(new GlideRoundTransform(instance, 10))
                    .error(R.mipmap.per)
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mHeadPic);
        }


    }


    /**
     * 初始化侧边栏
     */
    private void initSlidingMenu(Bundle savedInstanceState) {
        // 如果保存的状态不为空则得到之前保存的Fragment，否则实例化MyFragment
        if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(
                    savedInstanceState, "mContent");
        }

        if (mContent == null) {
            mContent = new LeftFragment();
        }

        // 设置左侧滑动菜单
        setBehindContentView(R.layout.menu_frame_left);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.menu_frame, new LeftFragment()).commit();

        // 实例化滑动菜单对象
        sm = getSlidingMenu();
        // 设置可以左右滑动的菜单
        sm.setMode(SlidingMenu.LEFT);
        // 设置滑动阴影的宽度
        sm.setShadowWidthRes(R.dimen.shadow_width);
        // 设置滑动菜单阴影的图像资源
        sm.setShadowDrawable(R.mipmap.left_shadow);
        // 设置滑动菜单视图的宽度

        WindowManager manager = (WindowManager) instance.getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        int width = display.getWidth();

        sm.setBehindOffsetRes(R.dimen.slidingmenu_width);
        // 设置渐入渐出效果的值
        sm.setFadeDegree(0.35f);
        sm.setFadeEnabled(true);
        // 设置触摸屏幕的模式,这里设置为全屏
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置下方视图的在滚动时的缩放比例
        sm.setBehindScrollScale(0.2f);
//        sm.addIgnoredView(sib);
    }



    public void addListener() {
        iv_zxing.setOnClickListener(instance);
        mIvUser.setOnClickListener(instance);
        mTxtPass.setOnClickListener(instance);
        mTxtStaff.setOnClickListener(instance);
        mTxtHotel.setOnClickListener(instance);
        mTxtData.setOnClickListener(instance);
        txt_order.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_user:
                sm.toggle();
                break;
            case R.id.iv_zxing:
                Intent intent2 = new Intent(instance, CaptureActivity.class);
                startActivityForResult(intent2, 0);
                break;
            case R.id.txt_passenger_main://旅客查询
                startActivity(new Intent(instance,PassengerActivity.class));
                break;
            case R.id.txt_staff_main://从业人员查询
                startActivity(new Intent(instance,StaffActivity.class));
                break;
            case R.id.txt_hotel_main://酒店查询
                startActivity(new Intent(instance,HotelActivity.class));
                break;
            case R.id.txt_data_main://数据查询
                startActivity(new Intent(instance,OrgActivity.class));
                break;
            case R.id.txt_order_main://订单查询
                startActivity(new Intent(instance,OrderActivity.class));
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //扫描结果
        if (resultCode == RESULT_OK && requestCode == 0) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                String scanResult = bundle.getString("result");
                String code = scanResult.substring(scanResult.indexOf("=")+1,scanResult.length());
                LogUtils.e(code);
                //跳转到查询结果结果界面
                Intent intent = new Intent(instance, QueryOrcActivity.class);
                intent.putExtra("code", code);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (!isQuit) {
            isQuit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 2000);
        } else {
            finish();
            System.exit(0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        setSPData();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityController.removeActivity(instance);
        mUnbinder.unbind();
    }



    /**
     * 折线图
     */
    public void setLine(){
//        List<Map<String, Object>> table = GsonUtil.GsonToListMaps(SPUtils.get(instance, "table", "").toString());
//            LogUtils.e(table.toString());

        ArrayList<String> x = new ArrayList<String>();
        ArrayList<Double> y = new ArrayList<Double>();
        try
        {
            JSONArray jsonArray = new JSONArray(SPUtils.get(instance, "table", "").toString());
            for (int i=0; i < jsonArray.length(); i++)    {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int date = jsonObject.getInt("date");
                Double count = jsonObject.getDouble("count");
                x.add(date+"");
                y.add(count);
            }

            LineData mLineData = ChartUtil.makeLineData(instance,7, y, x);
            ChartUtil.setChartStyle(mLine, mLineData, Color.WHITE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }

    /**
     * 饼状图
     */
    public void setPie(){
        // /名字
        ArrayList<String> names=new ArrayList<String>();
        names.add("汉族");
        names.add("藏族");
        names.add("维吾尔族");
        names.add("其他");
        //大小

        ArrayList<Entry> sizes=new ArrayList<Entry>();
        float han = Float.parseFloat(SPUtils.get(instance, "han", "").toString());
        float zang = Float.parseFloat(SPUtils.get(instance, "zang", "").toString());
        float wei = Float.parseFloat(SPUtils.get(instance, "wei", "").toString());
        float other = Float.parseFloat(SPUtils.get(instance, "other", "").toString());

        sizes.add(new Entry(han,0));
        sizes.add(new Entry(zang,1));
        sizes.add(new Entry(wei,2));
        sizes.add(new Entry(other,3));
        //颜色
        ArrayList<Integer> colors=new ArrayList<Integer>();
        colors.add(Color.parseColor("#aa01fe"));
        colors.add(Color.parseColor("#fd00a8"));
        colors.add(Color.parseColor("#04a5ff"));
        colors.add(Color.parseColor("#02dee1"));

        PieDataSet pieDataSet=new PieDataSet(sizes,"");//参数：颜色栏显示颜色目录、
        //pieDataSet.setDrawValues(false);//是否在块上面显示值以及百分百
        pieDataSet.setSliceSpace(3f);           //设置饼状Item之间的间隙
        pieDataSet.setSelectionShift(10f);      //设置饼状Item被选中时变化的距离
        pieDataSet.setColors(colors);

//        DisplayMetrics metrics=this.getResources().getDisplayMetrics();
        PieData pieData=new PieData(names,pieDataSet);



        mPie.setTransparentCircleRadius(0f);//设置大圆里面透明小圆半径，和洞不是一个圆

        mPie.setDrawHoleEnabled(true);
        //pieChart.setHoleColorTransparent(true);//设置中心洞是否透明：true为黑，false为白
        mPie.setHoleRadius(0f);//设置大圆里面的无色圆的半径（洞...）


        mPie.setDescription("单位：%");//参数：右下角显示图形描述

        mPie.setDrawCenterText(false);//不显示图中心文字
        mPie.setCenterText("traffic graph");//图中心文字
        mPie.setRotationEnabled(false);//不能手动旋转

//        mPie.setDrawMarkerViews(false);
        mPie.setDrawSliceText(false);//块的文本是否显示
        mPie.setData(pieData);

        Legend legend=mPie.getLegend();
        legend.setEnabled(false);//是否显示图形说明，必须要放在setData后,否则出错
//        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        //两个参数有不同的意思：
        //durationMillisX：每个块运行到固定初始位置的时间
        //durationMillisY: 每个块到绘制结束时间
        mPie.animateXY(1000, 1000);//设置动画（参数为时间）

    }

    public void setPieProgress(){
        // /名字
        ArrayList<String> names=new ArrayList<String>();
        names.add("故障率");
        names.add("未故障");
        //大小

        ArrayList<Entry> sizes=new ArrayList<Entry>();
        LogUtils.e(SPUtils.get(instance, "fault", "").toString()+"主界面");
        LogUtils.e(SPUtils.get(instance, "ho_count", "").toString()+"主界面");
        float fault = Float.parseFloat(SPUtils.get(instance, "fault", "").toString());
        float ho_count = Float.parseFloat(SPUtils.get(instance, "ho_count", "").toString());
        float faultp = fault/ho_count;
        LogUtils.e(faultp+"主界面");
        DecimalFormat decimalFormat=new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.
        String p=decimalFormat.format(faultp);//format 返回的是字符串

        progress_text.setText(p+"");
        float no_fault = 100.0f - faultp;

        if (faultp<5){
            faultp = 5;
        }
        sizes.add(new Entry(faultp,0));
        sizes.add(new Entry(no_fault,1));
        //颜色
        ArrayList<Integer> colors=new ArrayList<Integer>();
        colors.add(Color.parseColor("#2b76fd"));
        colors.add(Color.parseColor("#bbbbbb"));

        PieDataSet pieDataSet=new PieDataSet(sizes,"");//参数：颜色栏显示颜色目录、
        pieDataSet.setDrawValues(false);//是否在块上面显示值以及百分百
        pieDataSet.setSliceSpace(3f);           //设置饼状Item之间的间隙
        pieDataSet.setSelectionShift(10f);      //设置饼状Item被选中时变化的距离
        pieDataSet.setColors(colors);

        //DisplayMetrics metrics=this.getResources().getDisplayMetrics();
        PieData pieData=new PieData(names,pieDataSet);


        mProgress.setTransparentCircleRadius(0f);//设置大圆里面透明小圆半径，和洞不是一个圆

        mProgress.setDrawHoleEnabled(true);
        //pieChart.setHoleColorTransparent(true);//设置中心洞是否透明：true为黑，false为白
        mProgress.setHoleRadius(85f);//设置大圆里面的无色圆的半径（洞...）


        mProgress.setDescription("单位：%");//参数：右下角显示图形描述

        mProgress.setDrawCenterText(false);//不显示图中心文字
        mProgress.setCenterText("traffic graph");//图中心文字
        mProgress.setRotationEnabled(false);//不能手动旋转

//        mPie.setDrawMarkerViews(false);
        mProgress.setDrawSliceText(false);//块的文本是否显示
        mProgress.setData(pieData);

        Legend legend=mProgress.getLegend();
        legend.setEnabled(false);//是否显示图形说明，必须要放在setData后,否则出错
//        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_RIGHT);

        //两个参数有不同的意思：
        //durationMillisX：每个块运行到固定初始位置的时间
        //durationMillisY: 每个块到绘制结束时间
        mProgress.animateXY(1000, 1000);//设置动画（参数为时间）
    }

}
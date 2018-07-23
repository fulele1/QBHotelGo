package com.xaqb.hotel.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.xaqb.hotel.Fragment.LeftFragment;
import com.xaqb.hotel.R;
import com.xaqb.hotel.Utils.ActivityController;
import com.xaqb.hotel.Utils.ApkTotalUtill;
import com.xaqb.hotel.Utils.ChartUtil;
import com.xaqb.hotel.Utils.CheckNetwork;
import com.xaqb.hotel.Utils.GlideRoundTransform;
import com.xaqb.hotel.Utils.GsonUtil;
import com.xaqb.hotel.Utils.HttpUrlUtils;
import com.xaqb.hotel.Utils.LogUtils;
import com.xaqb.hotel.Utils.MyApplication;
import com.xaqb.hotel.Utils.NullUtil;
import com.xaqb.hotel.Utils.ProcUnit;
import com.xaqb.hotel.Utils.SPUtils;
import com.xaqb.hotel.Utils.StatuBarUtil;
import com.xaqb.hotel.zxing.activity.CaptureActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;

/**
 * 主页面
 */
public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener {
    /**
     * 检查用户验证状态
     */
    static boolean FbForceRight = false;
    public String late;
    protected String FsUrl = "";
    protected String FsUser = "";
    protected String FsRet = "";
    protected AlertDialog FoWait = null;
    protected String FsFile = "";
    protected ProgressBar FoBar = null;
    protected String FsVersion = "";
    protected boolean FbUpdate = false;
    protected boolean FbForceUpdate = false;
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


    /**
     * 检查是否要进行更新
     */
    private String au_last_version;
    private String au_filePath;
    private String au_info;
    private String au_id;
    private List<Integer> mImageList;


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


    /**
     * 下载新版本
     */
    private File f;
    private boolean isExists;
    Handler FoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //获取版本号
                    String sVersion = getVersionName();
                    String[] aData = FsRet.split(",");
                    int newVersion = Integer.parseInt(au_version.replace(".", ""));
                    int nowVersion = Integer.parseInt(getVersionName().replace(".", ""));

                    if (newVersion == nowVersion) {
                        late = "yes";
                        writeConfig("late", late);
                        if (FbUpdate) {
                            showDialog("提示", "已经是最新版本", "确定", "", 0);
                        }
                    } else if (newVersion > nowVersion) {
                        late = "no";
                        writeConfig("late", late);
                        f = new File(SPUtils.get(instance,"au_save_path","")+"");
                        isExists = f.exists();
                        if (isExists
                                && ApkTotalUtill.getUninatllApkInfo(instance,SPUtils.get(instance,"au_save_path","")+"")
                                ) {
                            showDialog( "提示", "新版本已下载成功是否直接安装", "立刻安装", "以后再说",0);

                        } else {
                            aData[0] = aData[0].trim();
                            if (sVersion.compareTo(aData[0]) < 0) {
                                FsVersion = aData[0];
                                if (aData.length > 1) {
                                    aData[1] = aData[1].trim();
                                    if (aData[1].compareTo("1") == 0) FbForceUpdate = true;
                                }
                            }

                            showDialog("发现新版本", "本次更新的内容有\n" + au_info, "立刻更新", "以后再说", 0);
                        }
                    }
                    break;

                case 1://下载完成
                    if (FoWait != null) FoWait.dismiss();
                    File oFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "/" + FsFile);
                    if (!oFile.exists()) {
                        showDialog("错误", "下载文件不存在", "关闭", "", 0);
                        return;
                    }
                    Intent oInt = new Intent(Intent.ACTION_VIEW);
                    oInt.setDataAndType(Uri.fromFile(oFile), "application/vnd.android.package-archive");
                    MainActivity.this.startActivity(oInt);
                    break;
                case 3://显示进度
                    if (FoBar != null)
                        FoBar.setProgress(msg.arg1);
                    break;

                case 10:
                    if (FoWait != null) FoWait.dismiss();
                    showDialog("错误", FsRet, "确定", "", 0);
                    showMess(FsRet, true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    //获取版本信息
    public void getVersion() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    String sRet = ProcUnit.httpGetMore(FsUrl);
                    if (!au_version.equals(null)) {
                        FsRet = sRet.substring(1);
                        FoHandler.sendMessage(M(0));
                    } else {
                        //FsRet=sRet.substring(1);
                        FsRet = "获取版本信息错误，请与管理员联系！";
                        FoHandler.sendMessage(M(10));
                    }
                } catch (Exception E) {
                    FsRet = E.getMessage();
                    FoHandler.sendMessage(M(10));
                }
            }

            protected Message M(int iWhat) {
                Message oMess = new Message();
                oMess.what = iWhat;
                return oMess;
            }
        }).start();


        //网络访问看是否需要有
        if (FsUrl.length() < 6) {
            showMess("地址错误，请在系统设置中设置上传地址。", true);
            return;
        }
        if (!CheckNetwork.isNetworkAvailable(MyApplication.instance)) {
//            showMess("网络未连接", false);
            return;
        }
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public String getVersionName() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);

            // 当前应用的版本名称
            return info.versionName;

        } catch (Exception e) {
            return "";
        }
    }

    protected void downVersion() {
        Intent oInt = new Intent();
        oInt.setClass(this, UpdateActivityNew.class);
        oInt.putExtra("url", HttpUrlUtils.getHttpUrl().get_updata() + au_filePath);
        oInt.putExtra("file", au_filePath);
        startActivity(oInt);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        instance = this;
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.bag));//修改状态栏字体颜色为黑色
        mUnbinder = ButterKnife.bind(instance);
        ActivityController.addActivity(instance);
        addListener();
        initSlidingMenu(savedInstanceState);
        checkVerdion();//检查更新

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
    private void checkVerdion() {
        //  请求连接网络 解析后 拿到版本号和版本名
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().get_updata() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        LogUtils.e(s);
                        Map<String, Object> map = GsonUtil.GsonToMaps(s);
                        if (map.get("state").toString().equals("1.0")) {
                            showMess(NullUtil.getString(map.get("mess")), true);
                            return;
                        } else if (map.get("state").toString().equals("0.0")) {
                            Map<String, Object> data = GsonUtil.JsonToMap(GsonUtil.GsonString(map.get("table")));
                            au_version = NullUtil.getString(data.get("au_version"));
                            au_last_version = NullUtil.getString(data.get("au_last_version"));
                            au_filePath = NullUtil.getString(data.get("au_file_path"));//下载链接

                            SPUtils.put(instance, "au_file_path", HttpUrlUtils.getHttpUrl().getBaseUrl()+"/"+au_filePath);//下载地址
                            SPUtils.put(instance, "au_save_path", Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+au_version+".apk");//保存地址
                            au_info = NullUtil.getString(data.get("au_info"));
                            SPUtils.put(instance, "au_info", au_info);
                            au_id = NullUtil.getString(data.get("au_id"));
                            FsUrl = readConfig("url");
                            if (FsUrl.length() == 0) {
                                FsUrl = HttpUrlUtils.getHttpUrl().get_updata() + "?access_token=" +
                                        SPUtils.get(instance, "access_token", "").toString();
                                writeConfig("url", FsUrl);
                            }
                            FsUrl = readConfig("url");
                            FsUser = readConfig("user");
                            FsFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + au_version+".apk";
                            FbUpdate = false;
                            getVersion();
                            checkRight();
                        }
                    }
                });

    }

    protected String readConfig(String sName) {

        SharedPreferences oConfig = getSharedPreferences("config", Activity.MODE_PRIVATE);
        return oConfig.getString(sName, "");
    }

    protected void writeConfig(String sName, String sValue) {
        SharedPreferences oConfig = getSharedPreferences("config", Activity.MODE_PRIVATE);
        SharedPreferences.Editor oEdit = oConfig.edit();//获得编辑器
        oEdit.putString(sName, sValue);
        oEdit.commit();//提交内容

    }

    private void checkRight() {
        if (!CheckNetwork
                .isNetworkAvailable(MyApplication.instance)) {
            return;
        }
        new Thread(new Runnable() {
            public void run() {
                try {
                    String sRet = ProcUnit.httpCheckRight(FsUrl, FsUser, readConfig("right"));
                    if (sRet.equals("0ok")) {
                        FbForceRight = false;
                    } else if (sRet.startsWith("2")) {
                        FbForceRight = false;
                    } else {
                        FbForceRight = true;
                    }
                } catch (Exception E) {
                    FbForceRight = false;
                }
            }
        }).start();
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
     * 检查更新
     * @param sCaption
     * @param sText
     * @param sOk
     * @param sCancel
     * @param iLayout
     * @return
     */
    protected AlertDialog showDialog(String sCaption,
                                     String sText,
                                     String sOk,
                                     String sCancel,
                                     int iLayout) {
        AlertDialog.Builder oBuilder = new AlertDialog.Builder(this);
        if (iLayout > 0) {
            LayoutInflater oInflater = getLayoutInflater();
            View oLayout = oInflater.inflate(iLayout, null, false);
            oBuilder.setView(oLayout);

        } else
            oBuilder.setMessage(sText);
        oBuilder.setTitle(sCaption);
        if (sOk.length() > 0) {
            oBuilder.setPositiveButton(sOk, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    instance.dialogOk();
                    dialog.dismiss();
                }
            });
        }
        if (sCancel.length() > 0) {
            oBuilder.setNegativeButton(sCancel, new DialogInterface.OnClickListener() {
                @Override


                public void onClick(DialogInterface dialog, int which) {
                    instance.dialogCancel();
                    dialog.dismiss();
                }
            });

        }
        AlertDialog oDialog = oBuilder.create();
        oDialog.show();
        return oDialog;
    }

    /**
     * 对话框单击确定按钮处理
     */
    protected void dialogOk() {
        if(isExists
                && ApkTotalUtill.getUninatllApkInfo(instance,SPUtils.get(instance,"au_save_path","")+"")
                ){
            //安装app
            Intent oInt1 = new Intent(Intent.ACTION_VIEW);
            oInt1.setDataAndType(Uri.fromFile(f), "application/vnd.android.package-archive");

            //关键点：
            //安装完成后执行打开
            oInt1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(oInt1);
        }else{
            downVersion();
        }
    }


    /**
     * 对话框单击取消按钮处理
     */
    protected void dialogCancel() {
    }


    /**
     * 吐司
     *
     * @param sMess
     * @param bLong
     */
    protected void showMess(String sMess, boolean bLong) {
        Toast.makeText(this, sMess, bLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    /**
     * 折线图
     */
    public void setLine(){
        List<Map<String, Object>> table = GsonUtil.GsonToListMaps(SPUtils.get(instance, "table", "").toString());
            LogUtils.e(table.toString());
        ArrayList<String> x = new ArrayList<String>();
        ArrayList<Double> y = new ArrayList<Double>();
        for (int i = 0; i <table.size(); i++) {
            // x轴显示的数据
            x.add(table.get(i).get("date").toString());
            y.add(Double.parseDouble(table.get(i).get("count").toString()));
            LogUtils.e(table.get(i).get("date").toString());
        }

        LineData mLineData = ChartUtil.makeLineData(instance,7, y, x);
        ChartUtil.setChartStyle(mLine, mLineData, Color.WHITE);
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

//        float han = 70.8f;
//        float zang = 0f;
//        float wei = 0f;
//        float other = 29.2f;
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
        pieDataSet.setSliceSpace(0f);//块间距
        pieDataSet.setColors(colors);

        //DisplayMetrics metrics=this.getResources().getDisplayMetrics();
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
        names.add("已入住");
        names.add("未入住");
        //大小

        ArrayList<Entry> sizes=new ArrayList<Entry>();
        float used = Float.parseFloat(SPUtils.get(instance, "checkin", "").toString());
        progress_text.setText(used+"");
        float to_use = 100.0f - used;

        sizes.add(new Entry(used,0));
        sizes.add(new Entry(to_use,1));
        //颜色
        ArrayList<Integer> colors=new ArrayList<Integer>();
        colors.add(Color.parseColor("#2b76fd"));
        colors.add(Color.parseColor("#bbbbbb"));

        PieDataSet pieDataSet=new PieDataSet(sizes,"");//参数：颜色栏显示颜色目录、
        pieDataSet.setDrawValues(false);//是否在块上面显示值以及百分百
        pieDataSet.setSliceSpace(0f);//块间距
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
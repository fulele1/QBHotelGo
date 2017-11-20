package com.xaqb.unlock.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;
import com.xaqb.unlock.Fragment.LeftFragment;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Service.FileService;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.CheckNetwork;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.MyApplication;
import com.xaqb.unlock.Utils.ProcUnit;
import com.xaqb.unlock.Utils.SPUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

import static com.amap.api.maps.MapsInitializer.getVersion;

/**
 * 主页面
 */
public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener {
    private MainActivity instance;
    private ConvenientBanner mCb;
    private String au_version;
    protected String FsUrl = "";
    protected String FsUser = "";
    protected String FsRet = "";
    protected AlertDialog FoWait = null;
    protected String FsFile = "";
    protected ProgressBar FoBar = null;
    protected String FsVersion = "";
    protected boolean FbUpdate = false;
    protected boolean FbForceUpdate = false;
public String late;

    Handler FoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: //获取版本号
                    String sVersion = getVersionName();
                    String[] aData = FsRet.split(",");

                    LogUtils.e(au_version.equals(getVersionName()) + "");

                    if (au_version.equals(getVersionName())) {
                        late ="yes";
                        writeConfig("late",late);
                        if (FbUpdate){
                            showDialog("提示", "已经是最新版本", "确定", "", 0);
                        }
                    } else {
                        aData[0] = aData[0].trim();
                        if (sVersion.compareTo(aData[0]) < 0) {
                            FsVersion = aData[0];
                            if (aData.length > 1) {
                                aData[1] = aData[1].trim();
                                if (aData[1].compareTo("1") == 0) FbForceUpdate = true;
                            }
                        }
                        late ="no";
                        writeConfig("late",late);
                        showDialog("更新提示", "检测到新版本，是否更新", "立刻更新", "以后再说", 0);
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
                    LogUtils.e("fsurl==", FsUrl);
                    String sRet = ProcUnit.httpGetMore(FsUrl);
                    if (!au_version.equals(null)) {
                        FsRet = sRet.substring(1);
                        FoHandler.sendMessage(M(0));
                    }
//                    if (sRet.substring(0, 1).equals("0")) {
//                        FsRet = sRet.substring(1);
//                        FoHandler.sendMessage(M(0));
//                    }
                    else {
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
        LogUtils.e(FsUrl.length() + "getVersion");
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

    /**
     *下载新版本
     */
    protected void downVersion() {
            Intent oInt = new Intent();
            oInt.setClass(this, UpdateActivity.class);
            oInt.putExtra("url", HttpUrlUtils.getHttpUrl().get_updata() + au_filename);
            oInt.putExtra("file", au_filename);
            LogUtils.e(au_filename);
            startActivity(oInt);
    }


    //    private SimpleImageBanner sib;
//    private String[] imgUrl = {
//            "http://pic39.nipic.com/20140312/10606030_144828215306_2.jpg",
//            "http://p0.so.qhmsg.com/bdr/_240_/t017eb457951f1a17e0.jpg",
//            "http://img.sootuu.com/Exchange/2009-11/20091120233536281.jpg",
//            "http://pic4.nipic.com/20090919/3372381_123043464790_2.jpg"
//    };
//    private String[] titles = {
//            "广告测试1", "广告测试2", "广告测试3", "广告测试4"
//    };
    private boolean isQuit = false;

    private ImageView ivUser,ivSend, ivWillSend, ivNearby, ivUserInfo, ivSetting, ivRealName,ivMessage;
    private LinearLayout llMainMenu, llQuery, llPickUp, llTransport, llSign, llCustomer, llFriends;
    //    private Button btOrder;
    private Fragment mContent;
    private SlidingMenu sm;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isQuit = false;
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //取消状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.main_menu);
        instance = this;
        ActivityController.addActivity(instance);
        assignViews();
        initData();
        addListener();
//        List<UserInfo> da = DataSupport.findAll(UserInfo.class);
//        LogUtils.i(da.toString());
        initSlidingMenu(savedInstanceState);
        //阿里云推送绑定手机账号
        CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.bindAccount(SPUtils.get(instance, "userAccount", "").toString(), new CommonCallback() {
            @Override
            public void onSuccess(String s) {
//                LogUtils.i("bindAccount------", "onSuccess");
            }

            @Override
            public void onFailed(String s, String s1) {
//                LogUtils.i("bindAccount------", "onFailed");
            }
        });
        checkVerdion();//检查更新
    }

    /**
     * 检查是否要进行更新
     */
    private String au_last_version;
    private String au_filename;
    private String au_info;
    private String au_id;
    private void checkVerdion() {
        //  请求连接网络 解析后 拿到版本号和版本名
        OkHttpUtils.get()
//                .url(HttpUrlUtils.getHttpUrl().get_updata() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .url(HttpUrlUtils.getHttpUrl().getPayDetail() + "?p=" + 1 + "&access_token=" + SPUtils.get(instance, "access_token", ""))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                        LogUtils.e("sssss" + e.toString());
                    }

                    @Override
                    public void onResponse(String s, int i) {

                        s = "{\"state\":0,\"mess\":\"\",\"table\":{\"au_id\":8,\"au_version\":\"1.4\"," +
                                "\"au_last_version\":\"1.2\",\"au_is_constraint\":1,\"au_createtime\":1509958607," +
                                "\"au_info\":\"优化数据，增减检查日志界面\",\"au_type\":2,\"au_filename\":\"police.apk\"}}\n";
                        LogUtils.e("sss" + s);
                        Map<String, Object> map = GsonUtil.GsonToMaps(s);
                        if (map.get("state").toString().equals("1.0")) {
                            showMess(map.get("mess").toString(),true);
                            return;
                        } else if (map.get("state").toString().equals("0.0")) {
                            Map<String, Object> data = GsonUtil.JsonToMap(GsonUtil.GsonString(map.get("table")));
                            au_version = data.get("au_version").toString();
                            au_last_version = data.get("au_last_version").toString();
                            au_filename = data.get("au_filename").toString();
                            au_info = data.get("au_info").toString();
                            au_id = data.get("au_id").toString();
                            FsUrl = readConfig("url");
                            if (FsUrl.length() == 0) {
                                FsUrl = HttpUrlUtils.getHttpUrl().get_updata() + "?access_token=" +
                                        SPUtils.get(instance, "access_token", "").toString();
                                writeConfig("url", FsUrl);
                            }
                            FsUrl = readConfig("url");
                            FsUser = readConfig("user");
                            FsFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + au_filename;
                            FbUpdate = false;
                            getVersion();
                            checkRight();

                        }}
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


    /**
     * 检查用户验证状态
     */
    static boolean FbForceRight = false;
    private void checkRight() {
        if (!CheckNetwork
                .isNetworkAvailable(MyApplication.instance)) {
//            showMess("网络未连接", false);
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


    private void assignViews() {
//        sib = (SimpleImageBanner) findViewById(R.id.sib_the_most_comlex_usage);
//        sib.setOnItemClickL(new BaseBanner.OnItemClickL() {
//            @Override
//            public void onItemClick(int position) {
//                Log.i("ccc", position + "");
//            }
//        });
//        setNewsTabData();
        ivUser = (ImageView) findViewById(R.id.iv_user);
        ivMessage = (ImageView) findViewById(R.id.iv_message);
        ivSend = (ImageView) findViewById(R.id.iv_send_data);
        ivWillSend = (ImageView) findViewById(R.id.iv_will_send);
        ivNearby = (ImageView) findViewById(R.id.iv_nearby_order);
        ivUserInfo = (ImageView) findViewById(R.id.iv_user_info);
        ivSetting = (ImageView) findViewById(R.id.iv_setting);
        ivRealName = (ImageView) findViewById(R.id.iv_real_name);
        llMainMenu = (LinearLayout) findViewById(R.id.ll_main_menu);
//        btOrder = (Button) findViewById(R.id.bt_order);
        mCb = (ConvenientBanner) findViewById(R.id.cb_main);

    }
private List <Integer> mImageList;
    public void initData() {
        mImageList = new ArrayList();
        mImageList.add(R.mipmap.main_pic1);
        mImageList.add(R.mipmap.main_pic2);
        mImageList.add(R.mipmap.main_pic3);
        cbSetPage();
        mCb.startTurning(2000);
    }


    /**
     * 轮播图holder
     */
    public class CbHolder implements Holder<Integer> {

        private ImageView pImg;
        @Override
        public View createView(Context context) {
            pImg = new ImageView(context);
            pImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return pImg;
        }

        @Override
        public void UpdateUI(Context context, int position, Integer data) {
            pImg.setImageResource(data);
        }
    }

    //设置状态栏和导航栏沉浸式
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }


    /**
     * 轮播图设置图片
     */
    public void cbSetPage(){
        mCb.setPages(new CBViewHolderCreator<CbHolder>() {
            @Override
            public CbHolder createHolder() {
                return new CbHolder();
            }
        },mImageList)
//                .setPageIndicator(new int[] {R.mipmap.pointn,R.mipmap.pointc})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_LEFT);
    }

    public void addListener() {
        ivUser.setOnClickListener(instance);
        ivMessage.setOnClickListener(instance);
        ivSend.setOnClickListener(instance);
        ivWillSend.setOnClickListener(instance);
        ivNearby.setOnClickListener(instance);
        ivUserInfo.setOnClickListener(instance);
        ivSetting.setOnClickListener(instance);
        ivRealName.setOnClickListener(instance);
    }

    @Override
    public void onClick(View v) {
        Intent i = null;
        switch (v.getId()) {
            case R.id.iv_user:
                sm.toggle();
                break;
            case R.id.iv_message:
                Toast.makeText(instance, "正在研发中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_send_data:
                i = new Intent(instance, MyOrderActivity.class);
                startActivity(i);
                break;
            case R.id.iv_will_send:
                i = new Intent(instance, WillSendActivity.class);
                startActivity(i);
                break;
            case R.id.iv_nearby_order:
//                AnimationUtil.playButtonAnimation(ivNearby);
                Toast.makeText(instance, "正在研发中...", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_user_info://个人信息
                i = new Intent(instance, UserInfoActivity.class);
                startActivity(i);
                break;
            case R.id.iv_setting:
                stopService(new Intent(instance, FileService.class));
                i = new Intent(instance, CollectionInfoActivity.class);
                startActivity(i);
                break;
            case R.id.iv_real_name:
                String status = SPUtils.get(instance, "staff_is_real", "").toString();
                if (status.equals(Globals.staffIsRealNo) || status.equals(Globals.staffIsRealFaild)) {
                    Toast.makeText(instance, status, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(instance, RealNameActivity.class));
//                    startActivity(new Intent(instance, RealNameActivityNew.class));
                } else if (status.equals(Globals.staffIsRealSuc)) {
                    Toast.makeText(instance, status, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(instance, RealNameInfoActivity.class));
                } else if (status.equals(Globals.staffIsRealIng)) {
                    Toast.makeText(instance, status, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(instance, RealNameActivity.class));

                }
                break;
//            case R.id.bt_order:
//                startActivity(new Intent(instance, OrderActivity.class));
//                break;
        }
    }

//    /**
//     * 设置新闻数据
//     */
//    private void setNewsTabData() {
//        sib
//                /** methods in BaseIndicatorBanner */
////              .setIndicatorStyle(BaseIndicaorBanner.STYLE_CORNER_RECTANGLE)//set indicator style
////              .setIndicatorWidth(6)                               //set indicator width
////              .setIndicatorHeight(6)                              //set indicator height
////              .setIndicatorGap(8)                                 //set gap btween two indicators
////              .setIndicatorCornerRadius(3)                        //set indicator corner raduis
//                .setSelectAnimClass(ZoomInEnter.class)              //se//t indicator select anim
//                /** methods in BaseBanner */
////              .setBarColor(Color.parseColor("#88000000"))         //set bootom bar color
////              .barPadding(5, 2, 5, 2)                             //set bottom bar padding
////              .setBarShowWhenLast(true)                           //set bottom bar show or not when the position is the last
////              .setTextColor(Color.parseColor("#ffffff"))          //set title text color
////              .setTextSize(12.5f)                                 //set title text size
////              .setTitleShow(true)                                 //set title show or not
////              .setIndicatorShow(true)                             //set indicator show or not
////              .setDelay(2)                                        //setDelay before start scroll
//                .setPeriod(5)                                      //scroll setPeriod
////                .setSource(DataProvider.getList())                  //data source list
//                .setSource(getList())                  //data source list
//                .setTransformerClass(ZoomOutSlideTransformer.class) //set page transformer
//                .startScroll();                                     //start scroll,the last method to call
//    }
//
//    //设置数据
//    private ArrayList<BannerItem> getList() {
//        ArrayList<BannerItem> list = new ArrayList<>();
//        for (int i = 0; i < imgUrl.length; i++) {
//            BannerItem item = new BannerItem();
//            item.imgUrl = imgUrl[i];
//            item.title = titles[i];
//            list.add(item);
//        }
//        return list;
//    }

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
//        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(instance, FileService.class));
        MobclickAgent.onResume(this);
        checkVerdion();//检查更新
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
    }

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
        downVersion();
    }

    /**
     * 对话框单击取消按钮处理
     */
    protected void dialogCancel() {
    }

    /**
     * 吐司
     * @param sMess
     * @param bLong
     */
    protected void showMess(String sMess, boolean bLong) {
        Toast.makeText(this, sMess, bLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

}

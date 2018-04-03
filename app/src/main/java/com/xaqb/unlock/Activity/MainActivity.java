package com.xaqb.unlock.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityManagerCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.xaqb.unlock.Fragment.LeftFragment;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Service.FileService;
import com.xaqb.unlock.Utils.ActivityController;
import com.xaqb.unlock.Utils.ApkTotalUtill;
import com.xaqb.unlock.Utils.CheckNetwork;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.GsonUtil;
import com.xaqb.unlock.Utils.HttpUrlUtils;
import com.xaqb.unlock.Utils.LogUtils;
import com.xaqb.unlock.Utils.MyApplication;
import com.xaqb.unlock.Utils.NullUtil;
import com.xaqb.unlock.Utils.ProcUnit;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.Utils.StatuBarUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.util.LogUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.HttpUrl;

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
    private ConvenientBanner mCb;
    private String au_version;
    private TextView mTxtKeyboard;
    private String status;
    private boolean isQuit = false;
    private ImageView ivUser, ivSend, ivWillSend, ivNearby, ivUserInfo, ivSetting, ivRealName, ivMessage;
    private LinearLayout llMainMenu, llQuery, llPickUp, llTransport, llSign, llCustomer, llFriends;
//        private Button btOrder;
    private Fragment mContent;
    private LinearLayout mLayStatus;
    private SlidingMenu sm;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isQuit = false;
        }
    };
    //轮播下面的小点（小圆点是本地的，自己导入的图片）
    private int[] indicator = {R.mipmap.point_gary, R.mipmap.point_red};
    private ConvenientBanner convenientBanner;
    //图片加载地址的集合
    private List<String> bean;
    private String[] images;
    private String[] url;
    /**
     * 检查是否要进行更新
     */
    private String au_last_version;
    private String au_filePath;
    private String au_info;
    private String au_id;
    private List<Integer> mImageList;
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
                    LogUtils.e(au_version+"-----------"+getVersionName());
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
        StatuBarUtil.setStatuBarLightMode(instance,getResources().getColor(R.color.white));//修改状态栏字体颜色为黑色
        ActivityController.addActivity(instance);
        assignViews();
//        initData();
        checkPic();
        addListener();
        initSlidingMenu(savedInstanceState);
        checkVerdion();//检查更新
        startService(new Intent(instance, FileService.class));
    }

    private void checkPic() {
        //  请求连接网络 解析后 拿到版本号和版本名
        OkHttpUtils.get()
                .url(HttpUrlUtils.getHttpUrl().getPic() + "?access_token=" + SPUtils.get(instance, "access_token", "").toString())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int i) {
                    }

                    @Override
                    public void onResponse(String s, int i) {
                        Map<String, Object> map = GsonUtil.GsonToMaps(s);
                        if (map.get("state").toString().equals("1.0")) {
                            return;
                        } else if (map.get("state").toString().equals("0.0")) {

                            List<Map<String, Object>> data = GsonUtil.GsonToListMaps(GsonUtil.GsonString(map.get("table")));
                            images = new String [data.size()] ;
                            url = new String [data.size()] ;

                            for (int j = 0; j < data.size(); j++) {
                                images[j] = NullUtil.getString(data.get(j).get("art_img"));
                                url[j] = NullUtil.getString(data.get(j).get("url"));
                            }

                            convenientBanner = (ConvenientBanner) findViewById(R.id.cb_main);

                            bean = Arrays.asList(images);
                            convenientBanner.setPointViewVisible(true)
                                    //设置小点
                                    .setPageIndicator(indicator);
                            //允许手动轮播
                            convenientBanner.setManualPageable(true);
                            //设置自动轮播的时间
                            convenientBanner.startTurning(2000);
                            //设置点击事件
                            //泛型为具体实现类ImageLoaderHolder
                            convenientBanner.setPages(new CBViewHolderCreator<MainActivity.NetImageLoadHolder>() {
                                @Override
                                public MainActivity.NetImageLoadHolder createHolder() {
                                    return new MainActivity.NetImageLoadHolder();
                                }
                            }, bean);

                            //设置每个pager的点击事件
                            convenientBanner.setOnItemClickListener(new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    if (url.length>0){
                                        Uri uri = Uri.parse(url[convenientBanner.getCurrentItem()]);
                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        startActivity(intent);
                                    }
                                }
                            });

                        }
                    }
                });
    }



    public class NetImageLoadHolder implements Holder<String> {
        private ImageView image_lv;

        //可以是一个布局也可以是一个Imageview
        @Override
        public ImageView createView(Context context) {
            image_lv = new ImageView(context);
            image_lv.setScaleType(ImageView.ScaleType.FIT_XY);

            return image_lv;
        }

        @Override
        public void UpdateUI(Context context, int position, String data) {
            //Picasso
            Picasso.with(context)
                    .load(data)
                    .placeholder(R.mipmap.main_pic1)
                    .error(R.mipmap.main_pic1)
                    .into(image_lv);

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

    private void assignViews() {
        ivUser = (ImageView) findViewById(R.id.iv_user);
        ivMessage = (ImageView) findViewById(R.id.iv_message);
        ivSend = (ImageView) findViewById(R.id.iv_send_data);
        ivWillSend = (ImageView) findViewById(R.id.iv_will_send);
        ivNearby = (ImageView) findViewById(R.id.iv_nearby_order);
        ivUserInfo = (ImageView) findViewById(R.id.iv_user_info);
        ivSetting = (ImageView) findViewById(R.id.iv_setting);
        ivRealName = (ImageView) findViewById(R.id.iv_real_name);
        llMainMenu = (LinearLayout) findViewById(R.id.ll_main_menu);
        mCb = (ConvenientBanner) findViewById(R.id.cb_main);
        mTxtKeyboard = (TextView) findViewById(R.id.txt_keyboard);
        if (StatuBarUtil.checkDeviceHasNavigationBar(instance)){
            mTxtKeyboard.setVisibility(View.VISIBLE);
        }
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
            case R.id.iv_will_send://待发数据
                i = new Intent(instance, WillSendActivity.class);
                startActivity(i);
                break;
            case R.id.iv_nearby_order://附近订单
                i = new Intent(instance, NearbyOrderActivity.class);
                startActivity(i);
                break;
            case R.id.iv_user_info://个人信息
                i = new Intent(instance, UserInfoActivity.class);
                startActivity(i);
                break;
            case R.id.iv_setting://信息采集
                stopService(new Intent(instance, FileService.class));
                i = new Intent(instance, CollectionInfoActivity.class);
                startActivity(i);
                break;
            case R.id.iv_real_name://实名认证
                status = SPUtils.get(instance, "staff_is_real", "").toString();
                if (status.equals(Globals.staffIsRealNo) || status.equals(Globals.staffIsRealFaild)) {
                    Toast.makeText(instance, "认证失败或未认证，请认证", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(instance, ApproveActivity.class));
                } else if (status.equals(Globals.staffIsRealSuc)) {
                    Toast.makeText(instance, "已经认证成功！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(instance, RealNameInfoActivity.class));
                } else if (status.equals(Globals.staffIsRealIng)) {
                    Toast.makeText(instance, "正在认证中！请耐心等待", Toast.LENGTH_SHORT).show();
//                    startActivity(new Intent(instance, ApproveActivity.class));
                    startActivity(new Intent(instance, RealNameInfoActivity.class));
                }
                break;
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
        startService(new Intent(instance, FileService.class));
        MobclickAgent.onResume(this);
        status = SPUtils.get(instance, "staff_is_real", "").toString();
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


    /**
     * 检查更新
     *
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


//    /**
//     * 轮播图holder
//     */
//    public class CbHolder implements Holder<Integer> {
//
//        private ImageView pImg;
//
//        @Override
//        public View createView(Context context) {
//            pImg = new ImageView(context);
//            pImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            return pImg;
//        }
//
//        @Override
//        public void UpdateUI(Context context, int position, Integer data) {
//            pImg.setImageResource(data);
//        }
//    }

}
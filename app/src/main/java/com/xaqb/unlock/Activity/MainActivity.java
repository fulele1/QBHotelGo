package com.xaqb.unlock.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主页面
 */
public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener {
    private MainActivity instance;
    private ConvenientBanner mCb;

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

    private ImageView ivUser, ivMessage, ivSend, ivWillSend, ivNearby, ivUserInfo, ivSetting, ivRealName;
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

    private List<Integer> mImageList;

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
    public void cbSetPage() {
        mCb.setPages(new CBViewHolderCreator<CbHolder>() {
            @Override
            public CbHolder createHolder() {
                return new CbHolder();
            }
        }, mImageList)
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
//        btOrder.setOnClickListener(instance);
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
//                //测试更新
////                /**
////                 * 2016-12-02 add register and service
////                 */
////                ProgressDialog
////                        progBar = new ProgressDialog(instance);
////                progBar.setTitle("下载");
////                progBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
////                progBar.setIcon(R.mipmap.app_logo);
////                progBar.setIndeterminate(false);
////                progBar.setCanceledOnTouchOutside(false);
////                progBar.setButton("取消", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialogInterface, int i) {
////                        LogUtils.i("点击取消下载");
////                    }
////                });
////                progBar.show();
//                startActivity(new Intent(instance, MessageActivity.class));
//                break;
            case R.id.iv_send_data:
                i = new Intent(instance, SendDataActivity.class);
                startActivity(i);
                break;
            case R.id.iv_will_send:
                i = new Intent(instance, WillSendActivity.class);
                startActivity(i);
                break;
            case R.id.iv_nearby_order:
//                AnimationUtil.playButtonAnimation(ivNearby);
                Toast.makeText(instance, "正在研发中...", Toast.LENGTH_SHORT).show();
//                i = new Intent(instance, NearbyOrderActivity.class);
//                startActivity(i);
                break;
            case R.id.iv_user_info:
                i = new Intent(instance, UserInfoActivity.class);
                startActivity(i);
                break;
            case R.id.iv_setting:
                stopService(new Intent(instance, FileService.class));
                i = new Intent(instance, OrderNewActivity.class);
                startActivity(i);
                break;
            case R.id.iv_real_name:
                String status = SPUtils.get(instance, "staff_is_real", "").toString();
                if (status.equals(Globals.staffIsRealNo) || status.equals(Globals.staffIsRealFaild)) {
                    startActivity(new Intent(instance, RealNameActivity.class));
//                    startActivity(new Intent(instance, RealNameActivityNew.class));
                } else if (status.equals(Globals.staffIsRealSuc)) {
//                    Toast.makeText(instance, "已经认证成功！", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(instance, RealNameInfoActivity.class));
//                    startActivity(new Intent(instance, RealNameActivityNew.class));
                } else if (status.equals(Globals.staffIsRealIng)) {
                    Toast.makeText(instance, "正在认证中！", Toast.LENGTH_SHORT).show();
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
}

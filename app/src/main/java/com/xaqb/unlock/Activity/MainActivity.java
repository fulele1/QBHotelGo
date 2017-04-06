package com.xaqb.unlock.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.flyco.banner.anim.select.ZoomInEnter;
import com.flyco.banner.transform.ZoomOutSlideTransformer;
import com.flyco.banner.widget.Banner.base.BaseBanner;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;
import com.xaqb.unlock.Fragment.LeftFragment;
import com.xaqb.unlock.R;
import com.xaqb.unlock.Service.FileService;
import com.xaqb.unlock.Utils.Globals;
import com.xaqb.unlock.Utils.SPUtils;
import com.xaqb.unlock.banner.BannerItem;
import com.xaqb.unlock.banner.SimpleImageBanner;

import java.util.ArrayList;

/**
 * 主页面
 */
public class MainActivity extends SlidingFragmentActivity implements View.OnClickListener {
    private MainActivity instance;
    private SimpleImageBanner sib;
    private String[] imgUrl = {
            "http://pic39.nipic.com/20140312/10606030_144828215306_2.jpg",
            "http://p0.so.qhmsg.com/bdr/_240_/t017eb457951f1a17e0.jpg",
            "http://img.sootuu.com/Exchange/2009-11/20091120233536281.jpg",
            "http://pic4.nipic.com/20090919/3372381_123043464790_2.jpg"
    };
    private String[] titles = {
            "广告测试1", "广告测试2", "广告测试3", "广告测试4"
    };

    private ImageView ivUser, ivMessage, ivSend, ivWillSend, ivNearby, ivUserInfo, ivSetting, ivRealName;
    private LinearLayout llMainMenu, llQuery, llPickUp, llTransport, llSign, llCustomer, llFriends;
    //    private Button btOrder;
    private Fragment mContent;
    private SlidingMenu sm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu);
        instance = this;
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
        sm.setShadowDrawable(R.drawable.ll_pressed);
        // 设置滑动菜单视图的宽度
        sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        // 设置渐入渐出效果的值
        sm.setFadeDegree(0.35f);
        sm.setFadeEnabled(true);
        // 设置触摸屏幕的模式,这里设置为全屏
        sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        // 设置下方视图的在滚动时的缩放比例
        sm.setBehindScrollScale(0.2f);
        sm.addIgnoredView(sib);
    }


    private void assignViews() {
        sib = (SimpleImageBanner) findViewById(R.id.sib_the_most_comlex_usage);
        sib.setOnItemClickL(new BaseBanner.OnItemClickL() {
            @Override
            public void onItemClick(int position) {
                Log.i("ccc", position + "");
            }
        });
        setNewsTabData();
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
    }

    public void initData() {
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
//            case R.id.iv_message:
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
                i = new Intent(instance, NearbyOrderActivity.class);
                startActivity(i);
                break;
            case R.id.iv_user_info:
                i = new Intent(instance, UserInfoActivity.class);
                startActivity(i);
                break;
            case R.id.iv_setting:
                stopService(new Intent(instance, FileService.class));
                i = new Intent(instance, OrderActivity.class);
                startActivity(i);
                break;
            case R.id.iv_real_name:
                String status = SPUtils.get(instance, "staff_is_real", "").toString();
                if (status.equals(Globals.staffIsRealNo) || status.equals(Globals.staffIsRealFaild)) {
                    startActivity(new Intent(instance, RealNameActivity.class));
                } else if (status.equals(Globals.staffIsRealSuc)) {
                    Toast.makeText(instance, "已经认证成功！", Toast.LENGTH_SHORT).show();
                } else if (status.equals(Globals.staffIsRealIng)) {
                    Toast.makeText(instance, "正在认证中！", Toast.LENGTH_SHORT).show();
                }
                break;
//            case R.id.bt_order:
//                startActivity(new Intent(instance, OrderActivity.class));
//                break;
        }
    }

    //设置新闻数据
    private void setNewsTabData() {
        sib
                /** methods in BaseIndicatorBanner */
//              .setIndicatorStyle(BaseIndicaorBanner.STYLE_CORNER_RECTANGLE)//set indicator style
//              .setIndicatorWidth(6)                               //set indicator width
//              .setIndicatorHeight(6)                              //set indicator height
//              .setIndicatorGap(8)                                 //set gap btween two indicators
//              .setIndicatorCornerRadius(3)                        //set indicator corner raduis
                .setSelectAnimClass(ZoomInEnter.class)              //se//t indicator select anim
                /** methods in BaseBanner */
//              .setBarColor(Color.parseColor("#88000000"))         //set bootom bar color
//              .barPadding(5, 2, 5, 2)                             //set bottom bar padding
//              .setBarShowWhenLast(true)                           //set bottom bar show or not when the position is the last
//              .setTextColor(Color.parseColor("#ffffff"))          //set title text color
//              .setTextSize(12.5f)                                 //set title text size
//              .setTitleShow(true)                                 //set title show or not
//              .setIndicatorShow(true)                             //set indicator show or not
//              .setDelay(2)                                        //setDelay before start scroll
                .setPeriod(5)                                      //scroll setPeriod
//                .setSource(DataProvider.getList())                  //data source list
                .setSource(getList())                  //data source list
                .setTransformerClass(ZoomOutSlideTransformer.class) //set page transformer
                .startScroll();                                     //start scroll,the last method to call
    }

    //设置数据
    private ArrayList<BannerItem> getList() {
        ArrayList<BannerItem> list = new ArrayList<>();
        for (int i = 0; i < imgUrl.length; i++) {
            BannerItem item = new BannerItem();
            item.imgUrl = imgUrl[i];
            item.title = titles[i];
            list.add(item);
        }
        return list;
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
}

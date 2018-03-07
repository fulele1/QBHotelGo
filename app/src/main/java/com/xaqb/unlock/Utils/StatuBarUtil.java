package com.xaqb.unlock.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 * 状态栏修改工具类
 * Created by fl on 2018/3/6.
 */

public class StatuBarUtil {
    private static final String TAG_FAKE_STATUS_BAR_VIEW = "statusBarView";
    private static final String TAG_MARGIN_ADDED = "marginAdded";

    /**
     * 5.0以上版本修改状态栏的颜色
     *
     * @param activity
     * @param statusColor
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColor(Activity activity, int statusColor){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP) {//5.0
            Window window = activity.getWindow();
            //取消状态栏透明
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //设置状态栏颜色
            window.setStatusBarColor(statusColor);
            //设置系统状态栏处于可见状态
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            //让View不根据系统窗口来调整自己的布局
            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null) {
                ViewCompat.setFitsSystemWindows(mChildView, true);
                ViewCompat.requestApplyInsets(mChildView);
            }
        }
    }


    /**
     * 设置状态栏透明
     * @param activity
     * @param hideStatusBarBackGround
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void translucentStatusBar(Activity activity, boolean hideStatusBarBackGround){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){//5.0
            Window window  = activity.getWindow();
            //添加Flag把状态栏设为可绘制模式
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (hideStatusBarBackGround){
                //如果为全透明模式，取消设置Window半透明的Flag
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //设置状态栏为透明
                window.setStatusBarColor(Color.TRANSPARENT);
                //设置window的状态栏为不可见
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE|View.SYSTEM_UI_LAYOUT_FLAGS);
            }else{
                //如果为半透明模式，添加设置Windows半透明的Flag
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                //设置系统状态栏的处于可见状态
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
            //view不根据系统窗口来调整自己的布局
            ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
            View mChildView = mContentView.getChildAt(0);
            if (mChildView != null){
                ViewCompat.setFitsSystemWindows(mChildView,true);//true在界面上会留出状态栏的位置 false在界面上不会留出状态栏的位置,并且会使华为下方的虚拟导航键遮挡住屏幕上的文字，不建议使用false
                ViewCompat.requestApplyInsets(mChildView);
            }
        }else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){//4.4-5.0
            Window window = activity.getWindow();
            //设置Window为透明
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
            View mContentChild = mContentView.getChildAt(0);
            //移除已经存在假状态栏，并取消它的margin间距
            removeFakeStatusBarViewIfExist(activity);
            removeMarginTopOfContentChild(mContentChild,getStatusBarHeight(activity));
            if (mContentChild !=null){
                //fitSystemWindow为false,不预留系统栏位置  true会留出状态栏的位置
                ViewCompat.setFitsSystemWindows(mContentChild,true);
            }
        }
    }

    /**
     * 修改状态栏字体颜色(一般在状态栏背景为白色时修改为黑色字体或者在状态栏透明的情况下背景图片的上面部分为渐变的颜色比较淡)
     * @param activity
     * @param color
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatuBarLightMode(Activity activity, int color){
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.KITKAT){

            if (MIUISetStatusBarLightMode(activity,true)||FlymeSetStatusBarLightMode(activity,true)){//判断是否为小米或魅族手机，如果是则将状态栏文字改为黑色
                //设置状态栏为指定颜色
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){//5.0
                    activity.getWindow().setStatusBarColor(color);
                }else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){//4.4
                    //调用修改状态栏颜色的方法
                    setStatusBarColor(activity,color);
                }
            }else if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){//如果是一般手机6.0 以上将状态栏文字改为黑色，并设置状态栏颜色

                activity.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                activity.getWindow().setStatusBarColor(color);
                //view不根据系统窗口来调整自己的布局
                ViewGroup mContentView = (ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT);
                View mChildView = mContentView.getChildAt(0);
                if (mChildView != null){
                    ViewCompat.setFitsSystemWindows(mChildView,true);//true在界面上会留出状态栏的位置 false在界面上不会留出状态栏的位置
                    ViewCompat.requestApplyInsets(mChildView);
                }
            }else if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){//如果是6.0以下的版本不修改颜色
                activity.getWindow().setStatusBarColor(color);

            }
        }
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }




    /**
     * 小米手机修改字体颜色
     * @param activity
     * @param darkmode
     * @return
     */
    private static boolean MIUISetStatusBarLightMode(Activity activity,boolean darkmode){
        boolean result = false;
        Class<? extends  Window> clazz = activity.getWindow().getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags",int.class,int.class);
            extraFlagField.invoke(activity.getWindow(),darkmode?darkModeFlag:0,darkModeFlag);
            result = true;
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    private static boolean FlymeSetStatusBarLightMode(Activity activity,boolean darkmode){
        boolean result = false;
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class.getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkmode){
                value |= bit;
            }else{
                value &= ~bit;
            }
            meizuFlags.setInt(lp,value);
            activity.getWindow().setAttributes(lp);
            result = true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 移除存在的假的状态栏view
     * @param activity
     */
    private static void removeFakeStatusBarViewIfExist(Activity activity) {
        Window window = activity.getWindow();
        ViewGroup mDecorView = (ViewGroup) window.getDecorView();

        View fakeView = mDecorView.findViewWithTag(TAG_FAKE_STATUS_BAR_VIEW);
        if (fakeView != null) {
            mDecorView.removeView(fakeView);
        }
    }

    private static void removeMarginTopOfContentChild(View mContentChild, int statusBarHeight) {
        if (mContentChild == null) {
            return;
        }
        if (TAG_MARGIN_ADDED.equals(mContentChild.getTag())) {
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mContentChild.getLayoutParams();
            lp.topMargin -= statusBarHeight;
            mContentChild.setLayoutParams(lp);
            mContentChild.setTag(null);
        }
    }

    /**
     * 获取状态栏的高度
     * @param activity
     * @return
     */
    private static int getStatusBarHeight(Activity activity) {
        Resources resources = activity.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen","android");
        int height = resources.getDimensionPixelSize(resourceId);
        return height;
    }


}

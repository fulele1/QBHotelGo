package com.xaqb.unlock.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;

import com.xaqb.unlock.Activity.SplashActivity;
import com.xaqb.unlock.R;

import static com.google.gson.internal.UnsafeAllocator.create;


public class CheckNetwork {


    /**
     * 判断无网络的情况下跳转到设置界面
     * @param context
     */
    public static void checkNetwork(final SplashActivity context){

        if (!isNetworkAvailable(context)){
            new AlertDialog.Builder(context)
                    .setIcon(context.getResources().getDrawable(R.mipmap.side))
                    .setTitle("提示")
                    .setMessage("无网络 是否开启网络")
                    .setPositiveButton("前往设置",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            context.startActivity(new Intent(Settings.ACTION_SETTINGS));//跳转至设置界面
                            context.finish();
                        }
                    }).create().show();

        }
    }


    /**
     *
     * @param context 上下文环境
     * @return true 表示有网络  false表示无网络
     */
    public static boolean isNetworkAvailable(Context context) {

        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null){
                return false;
            }else{
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info !=null){
                    for (NetworkInfo network : info){
                        if (network.getState() == NetworkInfo.State.CONNECTED){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * 判断是否为wifi网络链接
     *
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    /**
     * 判断当前网络是否是3G网络
     *
     * @param context
     * @return boolean
     */
    public static boolean is3G(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetInfo != null
                && activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE;
    }

}

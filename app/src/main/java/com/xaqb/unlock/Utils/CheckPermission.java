package com.xaqb.unlock.Utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.xaqb.unlock.Activity.LoginActivity;

/**
 * Created by sjw on 2017/10/6.
 * 将需要申请的权限传入,返回布尔值判断是否申请成功
 *
 * 处理权限请求结果
 *
 *  requestCode
 *          请求权限时传入的请求码，用于区别是哪一次请求的
 *
 *  permissions
 *          所请求的所有权限的数组
 *
 * grantResults
 *          权限授予结果，和 permissions 数组参数中的权限一一对应，元素值为两种情况，如下:
 *          授予: PackageManager.PERMISSION_GRANTED
 *          拒绝: PackageManager.PERMISSION_DENIED
 */

public class CheckPermission {

    private  int MY_PERMISSION_REQUEST_CODE = 10000;
    boolean check = false;
    private Context mContext;

    public boolean checkPermission(Context context , String[] permissions,int requestCode){
        mContext = context;
        MY_PERMISSION_REQUEST_CODE = requestCode;
        /**
         * 第 1 步: 检查是否有相应的权限
         */
            boolean isAllGranted = checkPermissionAllGranted(context,
                    permissions
            );
            // 如果这3个权限全都拥有, 则直接执行备份代码
            if (isAllGranted) {
               // doBackup();
                return true;
            }

        /**
         * 第 2 步: 请求权限
         */
        // 一次请求多个权限, 如果其他有权限是已经授予的将会自动忽略掉
        ActivityCompat.requestPermissions(
                (LoginActivity) mContext,
                permissions,
                MY_PERMISSION_REQUEST_CODE
        );
        return false;
    }

    /**
     * 检查是否拥有指定的所有权限
     */
    private boolean checkPermissionAllGranted(Context context ,String[]permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                // 只要有一个权限没有被授予, 则直接返回 false
                return false;
            }
        }
        return true;
    }
    /**
     * 打开 APP 的详情设置
     */
    public void openAppDetails() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("拍照需要访问 “摄像头” 和 “外部存储器”，请到 “应用信息 -> 权限” 中授予！");
        builder.setPositiveButton("去手动授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.setData(Uri.parse("package:" + mContext.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                mContext.startActivity(intent);
            }
        });
        builder.setNegativeButton("取消", null);
        builder.show();
    }
}

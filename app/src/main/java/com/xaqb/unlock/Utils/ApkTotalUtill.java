package com.xaqb.unlock.Utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;


/**
 * 验证apk是否完整
 * Created by fl on 2017/12/21.
 */

public class ApkTotalUtill {

    public static boolean getUninatllApkInfo(Context context, String filePath) {



        boolean result = false;
        try {
            PackageManager pm = context.getPackageManager();
            Log.e("archiveFilePath", filePath);
            PackageInfo info = pm.getPackageArchiveInfo(filePath, PackageManager.GET_ACTIVITIES);
            if (info != null) {
                result = true;
            }
        } catch (Exception e) {
            result = false;
            Log.e("archiveFilePath", "解析未安装的 apk 出现异常");
        }
        return result;
    }


}

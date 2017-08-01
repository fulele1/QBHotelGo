package com.xaqb.unlock.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by lenovo on 2017/4/1.
 */
public class ToolsUtils {
    /**/
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static String getStrTime(String cc_time) {
        String re_StrTime = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd a HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        long lcc_time = Long.valueOf(cc_time);
        re_StrTime = sdf.format(new Date(lcc_time * 1000L));
        return re_StrTime;
    }

    /**
     * 图片加文字水印
     *
     * @param oBack 图片
     * @param sText 文字
     * @param size  文字大小
     * @return
     */
    public static Bitmap drawText(Bitmap oBack, String sText, int size) {
        if (sText.length() == 0) return oBack;
        Bitmap oBmp = Bitmap.createBitmap(oBack.getWidth(), oBack.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas oCanvas = new Canvas();
        oCanvas.setBitmap(oBmp);
        oCanvas.drawBitmap(oBack, 0, 0, null);//画背景
        //-----计算板的大小----------------------------

        Paint oPaint = new Paint();
        oPaint.setColor(Color.rgb(246, 134, 68));
        oPaint.setAntiAlias(true);//去除锯齿
        oPaint.setFilterBitmap(true);//对位图进行滤波处理
        String familyName = "宋体";
        Typeface oFont = Typeface.create(familyName, Typeface.BOLD);
        oPaint.setTypeface(oFont);
//        int iSize = oBack.getHeight() / (sText.length()) - 10;
        oPaint.setTextSize(size);
        Rect oRect = new Rect();
        oPaint.getTextBounds(sText, 0, sText.length(), oRect);
        int iX = (oBmp.getWidth() - oRect.width()) / 2;
        int iY = (oBmp.getHeight() - oRect.height()) / 2;
        oCanvas.drawText(sText, iX, iY, oPaint);//写项目名称
        return oBmp;
    }

    /**
     * 身份证号码加密
     *
     * @param certNum 身份证号码
     * @return 加密后的身份证号码
     */
    public static String certNumEncryption(String certNum) {
        if (certNum != null && !certNum.isEmpty()) {
            if (certNum.length() == 18 || certNum.length() == 15) {
                // 用于显示的加*身份证
                certNum = certNum.substring(0, 6) + "**********" + certNum.substring(16);
            }
        }
        return certNum;
    }

    /**
     * 回收bitmap资源占用的内存空间
     *
     * @param bitmap 图片文件
     */
    public static void recycleBitmap(Bitmap bitmap) {
        // 先判断是否已经回收
        if (bitmap != null && !bitmap.isRecycled()) {
            // 回收并且置为null
            bitmap.recycle();
            bitmap = null;
        }
        System.gc();
    }


    /**
     * 获取锁具类型
     */
    public static String getLockType(String type) {
        switch (type) {
            case "01":
                type = "门锁";
                break;
            case "02":
                type = "保险柜锁";
                break;
            case "03":
                type = "汽车锁";
                break;
            case "04":
                type = "电子锁";
                break;
            case "05":
                type = "汽车芯片";
                break;
        }
        return type;
    }


}

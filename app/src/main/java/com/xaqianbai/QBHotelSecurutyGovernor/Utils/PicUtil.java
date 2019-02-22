package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;

/**
 * 绘制圆形图像
 * 流程：
 *     绘制白格子
 *     绘制圆形
 *     取其交集
 *     绘制头像
 */
public class PicUtil {

    /**
     * 剪切为圆形头像
     * @param resource
     * @return
     */
    public static Bitmap getCircleBit(Bitmap resource){
        int width =resource.getWidth();
        int height = resource.getHeight();
        int min = Math.min(width, height);
        Bitmap bitmap = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);//绘制白的格子
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        canvas.drawCircle(min/2,min/2,min/2,paint);//绘制圆形
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));//取其交集
        canvas.drawBitmap(resource,0,0,paint);//绘制图片
        return bitmap;
    }


    /**
     * 图片的缩放
     * @param oSrc
     * @param fRate
     * @return
     */
    public static Bitmap scalePhoto(Bitmap oSrc, float fRate) {
        Matrix oMatrix = new Matrix();
        oMatrix.postScale(fRate, fRate); //长和宽放大缩小的比例
        return Bitmap.createBitmap(oSrc, 0, 0, oSrc.getWidth(), oSrc.getHeight(), oMatrix, true);
    }

    /**
     * 按最大边长缩放
     * @param oSrc
     * @param iMax
     * @return
     */
    public static Bitmap scalePhoto(Bitmap oSrc, int iMax) {
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        float fRate = 0;
        if (iWidth > iHeight) fRate = (float) iMax / (float) iWidth;
        else fRate = (float) iMax / (float) iHeight;
        return scalePhoto(oSrc, fRate);
    }

    /**
     * 图片的旋转（自定义旋转角度）
     * @param oSrc
     * @param iAngle
     * @return
     */
    public static Bitmap rotatePhoto(Bitmap oSrc, int iAngle) {
        try {
            Matrix oMatrix = new Matrix();
            oMatrix.postRotate(iAngle); //旋转角度
            return Bitmap.createBitmap(oSrc, 0, 0, oSrc.getWidth(), oSrc.getHeight(), oMatrix, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return oSrc;
    }

    /**
     * 旋转
     * @param oSrc
     * @return
     */
    public static Bitmap rotateRect(Bitmap oSrc) {
        if (oSrc.getHeight() > oSrc.getWidth()) return rotatePhoto(oSrc, -90);
        else return oSrc;
    }

    /**
     * 图片裁剪
     * @param oSrc
     * @param iX
     * @param iY
     * @param iWidth
     * @param iHeight
     * @return
     */
    public static Bitmap cropPhoto(Bitmap oSrc, int iX, int iY, int iWidth, int iHeight) {
        return Bitmap.createBitmap(oSrc, iX, iY, iWidth, iHeight, null, false);
    }
}

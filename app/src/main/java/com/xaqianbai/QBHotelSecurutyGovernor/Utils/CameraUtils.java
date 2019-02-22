package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Environment;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lenovo on 2017/3/27.
 */
public class CameraUtils {

    public static Rect getCertRect(int iWidth, int iHeight) {
        Rect oRC = new Rect();
        oRC.left = 0;
        oRC.right = iWidth;
        int iH = iWidth * 540 / (2 * 856);
        oRC.top = iHeight / 2 - iH;
        oRC.bottom = iHeight / 2 + iH;
        return oRC;
    }

    public static Rect getCertNoRect(int iWidth, int iHeight) {
        Rect oRC = new Rect();
        int iTop = iHeight / 2 - iWidth * 540 / (2 * 856);
        oRC.top = iTop + iWidth * 420 / 856;
        oRC.bottom = iTop + iWidth * 500 / 856;
        oRC.left = iWidth * 280 / 856;
        oRC.right = iWidth * 800 / 856;
        return oRC;

    }

    //图片的旋转
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

    public static Bitmap rotateRect(Bitmap oSrc) {
        if (oSrc.getHeight() > oSrc.getWidth()) return rotatePhoto(oSrc, -90);
        else return oSrc;
    }

    //图片裁剪
    public static Bitmap cropPhoto(Bitmap oSrc, int iX, int iY, int iWidth, int iHeight) {

        return Bitmap.createBitmap(oSrc, iX, iY, iWidth, iHeight, null, false);
    }

    //0302新增身份证号码读取方法
    static final String TESSBASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/unlock/";
    //识别数字
    static final String DEFAULT_LANGUAGE = "number";

    public static String ReadCert(Bitmap bmp) {
        final TessBaseAPI baseApi = new TessBaseAPI();
        //初始化OCR的训练数据路径与语言
        try {
            baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
            //设置识别模式
            baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
            //设置要识别的图片
            baseApi.setImage(bmp);
//        english.setImageBitmap(bmp);
            String result = baseApi.getUTF8Text();
            baseApi.clear();
            baseApi.end();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static boolean checkCertNo(String sNo) {
        if (sNo == null) return false;
        if (sNo.length() != 18) return false;
        int i;
        String sTmp = "";
        for (i = 0; i < 18; i++) {
            sTmp = sNo.substring(i, i + 1);
            if (sTmp.compareTo("0") < 0 || sTmp.compareTo("9") > 0)
                if (i != 17 || sTmp.compareTo("X") != 0) return false;
        }
        sTmp = sNo.substring(6, 14);
        SimpleDateFormat oFormat = new SimpleDateFormat("yyyyMMdd");
        try {
            Date oDate = oFormat.parse(sTmp);
        } catch (Exception e) {
            return false;
        }
        int[] aRight = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};//权值数组
        String[] aCheck = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        int iAll = 0;
        for (i = 0; i < 17; i++)
            iAll += (Integer.parseInt(sNo.substring(i, i + 1)) * aRight[i]);
        iAll = iAll % 11;
        if (sNo.substring(17, 18).compareTo(aCheck[iAll]) != 0) return false;
        return true;
    }

    //按最大边长缩放
    public static Bitmap scalePhoto(Bitmap oSrc, int iMax) {
        int iWidth = oSrc.getWidth();
        int iHeight = oSrc.getHeight();
        float fRate = 0;
        if (iWidth > iHeight) fRate = (float) iMax / (float) iWidth;
        else fRate = (float) iMax / (float) iHeight;
        return scalePhoto(oSrc, fRate);
    }

    //图片的缩放
    public static Bitmap scalePhoto(Bitmap oSrc, float fRate) {
        Matrix oMatrix = new Matrix();
        oMatrix.postScale(fRate, fRate); //长和宽放大缩小的比例
        return Bitmap.createBitmap(oSrc, 0, 0, oSrc.getWidth(), oSrc.getHeight(), oMatrix, true);
    }

}

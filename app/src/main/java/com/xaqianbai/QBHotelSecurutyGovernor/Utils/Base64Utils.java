package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.graphics.Bitmap;
import android.util.Base64;

import java.io.ByteArrayOutputStream;


/**
 * Created by fl on 2017/3/15.
 */
public class Base64Utils {

    //照片转换成base64
    public static String photoToBase64(Bitmap oSrc, int iQuality) {

        ByteArrayOutputStream oStream = null;
        if (iQuality > 100) iQuality = 100;
        if (iQuality < 0) iQuality = 0;
        try {
            if (oSrc != null) {
                oStream = new ByteArrayOutputStream();
                oSrc.compress(Bitmap.CompressFormat.JPEG, iQuality, oStream);

                oStream.flush();
                oStream.close();

                byte[] aBmp = oStream.toByteArray();

                return Base64.encodeToString(aBmp, Base64.DEFAULT);
            }
        } catch (Exception e) {
            return "";
        } finally {
            try {
                if (oStream != null) {
                    oStream.flush();
                    oStream.close();
                }
            } catch (Exception e) {
                return "";
            }
        }
        return "";
    }

    public static byte[] Base64ToString(String str) {


            byte[] decodeWord = Base64.decode(str, Base64.NO_WRAP);
            return decodeWord;

    }
}
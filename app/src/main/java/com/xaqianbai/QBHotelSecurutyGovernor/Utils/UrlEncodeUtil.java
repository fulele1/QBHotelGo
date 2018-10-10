package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import java.net.URLEncoder;

/**
 * Created by fule on 2018/4/18.
 */

public class UrlEncodeUtil {


    public static String toURLEncoded(String paramString) {
        if (paramString == null || paramString.equals("")) {
            return "";
        }

        try
        {
            String str = new String(paramString.getBytes(), "UTF-8");
            str = URLEncoder.encode(str, "UTF-8");
            return str;
        }
        catch (Exception localException)
        {
        }

        return "";
    }


}

package com.xaqb.hotel.Utils;

import android.util.Base64;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by fule on 2018/4/17.
 */

public class ConditionUtil {

    public static String getConditionString(HashMap map){
        HashMap<String, String> map2 = new HashMap();
        ArrayList<String> list = new ArrayList();

        LogUtils.e("map------"+map.toString());
        Iterator map1it = map.entrySet().iterator();
        while (map1it.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) map1it.next();
            if (!"".equals(entry.getValue().toString()) && entry.getValue() != null) {
                map2.put(entry.getKey(), entry.getValue());
                list.add(entry.getKey());
            }
        }

        String condition = "";
        switch (list.size()) {
            case 1:
                condition = "{" + list.get(0) +":\""+ map2.get(list.get(0)) + "\"}";
                break;
            case 2:
                condition = "{" + list.get(0) +":\""+ map2.get(list.get(0)) + "\","
                        + list.get(1) +":\""+ map2.get(list.get(1)) + "\"}";
                break;
            case 3:
                condition = "{" + list.get(0) +":\""+ map2.get(list.get(0)) + "\"," +
                        list.get(1) +":\""+ map2.get(list.get(1)) + "\"," +
                        list.get(2) +":\""+ map2.get(list.get(2)) + "\"}";
                break;
            case 4:
                condition = "{" + list.get(0) +":\""+ map2.get(list.get(0)) + "\"," +
                        list.get(1) +":\""+ map2.get(list.get(1)) + "\"," +
                        list.get(2) +":\""+ map2.get(list.get(2)) + "\"," +
                        list.get(3) +":\""+ map2.get(list.get(3)) + "\"}";
                break;
        }


        LogUtils.e("String---"+condition);
        LogUtils.e("64---"+ Base64.encodeToString(condition.getBytes(), Base64.DEFAULT));
        LogUtils.e("encuded------"+getUrlEncoded(condition));
        return getUrlEncoded(condition);
    }


    public static String getUrlEncoded(String list) {
        return  UrlEncodeUtil.toURLEncoded(Base64.encodeToString(list.getBytes(), Base64.DEFAULT));
    }

}
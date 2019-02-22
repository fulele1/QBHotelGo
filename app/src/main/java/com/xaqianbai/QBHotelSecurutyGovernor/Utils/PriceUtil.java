package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by lenovo on 2018/1/18.
 */

public class PriceUtil {
    //金额验证
    public static boolean isNumber(String str){
        // 判断小数点后2位的数字的正则表达式
        Pattern pattern= Pattern.compile("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){0,2})?$");
        Matcher match=pattern.matcher(str);
        if(match.matches()==false){
            return false;
        }else{
            return true;
        }



    }

}

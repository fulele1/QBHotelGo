package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

/**
 * Created by lenovo on 2018/5/17.
 */

public class IdenTypeUtils {
    static String mType;
    public static String getIdenType(String type){
        if (type.equals("11")){
            mType = "身份证";
        }else if (type.equals("13")){
            mType = "户口本";
        }else if (type.equals("90")){
            mType = "军官证";
        }else if (type.equals("91")){
            mType = "警官证";
        }else if (type.equals("92")){
            mType = "士兵证";
        }else if (type.equals("93")){
            mType = "国内护照";
        }else if (type.equals("94")){
            mType = "驾照";
        }else if (type.equals("95")){
            mType = "港澳通行证";
        }else if (type.equals("99")){
            mType = "其他";
        }

        return mType;
    }
}

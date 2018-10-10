package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

/**
 * Created by fule on 2018/9/12.
 */

public class CastTypeUtil {

    private static String code = "";
    private static String type = "";

    public static String getTypeCode(String string) {
        if (string.equals("刑事案件类型 - 故意杀人案")){
            code = "1-040101";
        }else if (string.equals("刑事案件类型 - 抢劫案")){
            code = "1-050100";
        }else if (string.equals("刑事案件类型 - 盗窃案")){
            code = "1-050200";
        }else if (string.equals("刑事案件类型 - 诈骗案")){
            code = "1-050300";
        }else if (string.equals("刑事案件类型 - 其他刑事案件")){
            code = "1-990000";
        }else if (string.equals("治安案件类型 - 卖淫嫖娼")){
            code = "2-010000";
        }else if (string.equals("治安案件类型 - 赌博")){
            code = "2-020000";
        }else if (string.equals("治安案件类型 - 吸毒")){
            code = "2-030000";
        }else if (string.equals("治安案件类型 - 其他治安案件")){
            code = "2-990000";
        }else if (string.equals("治安案件类型 - 其他治安案件")){
            code = "2-990000";
        }else if (string.equals("治安案件类型 - 其他治安案件")){
            code = "2-990000";
        }else if (string.equals("警告")){
            code = "1";
        }else if (string.equals("罚款")){
            code = "2";
        }else if (string.equals("停业整顿")){
            code = "3";
        }else if (string.equals("吊销许可证")){
            code = "4";
        }else if (string.equals("限期整改")){
            code = "5";
        }else if (string.equals("其他")){
            code = "9";
        }
        return code;
    }

    public static String getResultType(String string) {
        if (string.equals("未处罚")) {
            type = "0";
        } else if (string.equals("已处罚")) {
            type = "1";
        }
        return type;
    }


}

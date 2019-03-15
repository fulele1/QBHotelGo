package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

/**
 * Created by fule on 2018/9/12.
 */

public class CastTypeUtil {

    private static String code = "";
    private static String type = "";

    public static String getTypeCode(String string) {
        if (string.equals("刑事案件类型 - 故意杀人案")) code = "1-040101";
        else if (string.equals("刑事案件类型 - 抢劫案")) code = "1-050100";
        else if (string.equals("刑事案件类型 - 盗窃案")) code = "1-050200";
        else if (string.equals("刑事案件类型 - 诈骗案")) code = "1-050300";
        else if (string.equals("刑事案件类型 - 其他刑事案件")) code = "1-990000";
        else if (string.equals("治安案件类型 - 卖淫嫖娼")) code = "2-010000";
        else if (string.equals("治安案件类型 - 赌博")) code = "2-020000";
        else if (string.equals("治安案件类型 - 吸毒")) code = "2-030000";
        else if (string.equals("治安案件类型 - 其他治安案件")) code = "2-990000";
        else if (string.equals("治安案件类型 - 其他治安案件")) code = "2-990000";
        else if (string.equals("治安案件类型 - 其他治安案件")) code = "2-990000";
        else if (string.equals("警告")) code = "1";
        else if (string.equals("罚款")) code = "2";
        else if (string.equals("停业整顿")) code = "3";
        else if (string.equals("吊销许可证")) code = "4";
        else if (string.equals("限期整改")) code = "5";
        else if (string.equals("其他"))
            code = "9";
        return code;
    }

    public static String getTypeString(String code) {
        if (code.equals("1")) code = "警告";
        else if (code.equals("2")) code = "罚款";
        else if (code.equals("3")) code = "停业整顿";
        else if (code.equals("4")) code = "吊销许可证";
        else if (code.equals("5")) code = "限期整改";
        else if (code.equals("9")) code = "其他";
        return code;
    }

    public static String getResultType(String string) {
        if (string.equals("未处罚")) type = "0";
        else if (string.equals("已处罚")) type = "1";
        return type;
    }

    public static String getResultTypeString(String string) {
        if (string.equals("0")) type = "未处罚";
        else if (string.equals("1")) type = "已处罚";
        return type;
    }

    public static String getyesno(String code) {
        if (code.equals("0")) type = "否";
        else if (code.equals("1")) type = "是";
        return type;
    }


    /**
     * 获取民族代码
     *
     * @param nation
     * @return
     */
    public static String getnationno(String nation) {
        if (nation.equals("汉族")) type = "01";
        if (nation.equals("蒙古族")) type = "02";
        if (nation.equals("回族")) type = "03";
        if (nation.equals("藏族")) type = "04";
        if (nation.equals("维吾尔族")) type = "05";
        if (nation.equals("苗族")) type = "06";
        if (nation.equals("彝族")) type = "07";
        if (nation.equals("壮族")) type = "08";
        if (nation.equals("布依族")) type = "09";
        if (nation.equals("朝鲜族")) type = "10";
        if (nation.equals("满族")) type = "11";
        if (nation.equals("侗族")) type = "12";
        if (nation.equals("瑶族")) type = "13";
        if (nation.equals("白族")) type = "14";
        if (nation.equals("土家族")) type = "15";
        if (nation.equals("哈尼族")) type = "16";
        if (nation.equals("哈萨克族")) type = "17";
        if (nation.equals("傣族")) type = "18";
        if (nation.equals("黎族")) type = "19";
        if (nation.equals("傈僳族")) type = "20";
        if (nation.equals("佤族")) type = "21";
        if (nation.equals("畲族")) type = "22";
        if (nation.equals("高山族")) type = "23";
        if (nation.equals("拉祜族")) type = "24";
        if (nation.equals("水族")) type = "25";
        if (nation.equals("东乡族")) type = "26";
        if (nation.equals("纳西族")) type = "27";
        if (nation.equals("景颇族")) type = "28";
        if (nation.equals("柯尔克孜族")) type = "29";
        if (nation.equals("土族")) type = "30";
        if (nation.equals("达斡尔族")) type = "31";
        if (nation.equals("仫佬族")) type = "32";
        if (nation.equals("羌族")) type = "33";
        if (nation.equals("布朗族")) type = "34";
        if (nation.equals("撒拉族")) type = "35";
        if (nation.equals("毛难族")) type = "36";
        if (nation.equals("仡佬族")) type = "37";
        if (nation.equals("锡伯族")) type = "38";
        if (nation.equals("阿昌族")) type = "39";
        if (nation.equals("普米族")) type = "40";
        if (nation.equals("塔吉克族")) type = "41";
        if (nation.equals("怒族")) type = "42";
        if (nation.equals("乌孜别克族")) type = "43";
        if (nation.equals("俄罗斯族")) type = "44";
        if (nation.equals("鄂温克族")) type = "45";
        if (nation.equals("崩龙族")) type = "46";
        if (nation.equals("保安族")) type = "47";
        if (nation.equals("裕固族")) type = "48";
        if (nation.equals("京族")) type = "49";
        if (nation.equals("塔塔尔族")) type = "50";
        if (nation.equals("独龙族")) type = "51";
        if (nation.equals("鄂伦春族")) type = "52";
        if (nation.equals("赫哲族")) type = "53";
        if (nation.equals("门巴族")) type = "54";
        if (nation.equals("珞巴族")) type = "55";
        if (nation.equals("基诺族")) type = "56";
        return type;
    }


    /**
     * @param contry
     * @return
     */
    public static String getcontryno(String contry) {
        if (contry.equals("阿鲁巴")) type = "ABW";
        if (contry.equals("阿富汗")) type = "AFG";
        if (contry.equals("安哥拉")) type = "AGO";
        if (contry.equals("安圭拉")) type = "AIA";
        if (contry.equals("阿尔巴尼亚")) type = "ALB";
        if (contry.equals("安道尔")) type = "AND";
        if (contry.equals("荷属安的列斯")) type = "ANT";
        if (contry.equals("阿联酋")) type = "ARE";
        if (contry.equals("阿根廷")) type = "ARG";
        if (contry.equals("亚美尼亚")) type = "ARM";
        if (contry.equals("美属萨摩亚")) type = "ASM";
        if (contry.equals("南极洲")) type = "ATA";
        if (contry.equals("法属南部领土")) type = "ATF";
        if (contry.equals("安提瓜和巴布达")) type = "ATG";
        if (contry.equals("澳大利亚")) type = "AUS";
        if (contry.equals("奥地利")) type = "AUT";
        if (contry.equals("阿塞拜疆")) type = "AZE";
        if (contry.equals("布隆迪")) type = "BDI";
        if (contry.equals("比利时")) type = "BEL";
        if (contry.equals("贝宁")) type = "BEN";
        if (contry.equals("布基纳法索")) type = "BFA";
        if (contry.equals("孟加拉国")) type = "BGD";
        if (contry.equals("保加利亚")) type = "BGR";
        if (contry.equals("巴林")) type = "BHR";
        if (contry.equals("巴哈马")) type = "BHS";
        if (contry.equals("波黑")) type = "BIH";
        if (contry.equals("白俄罗斯")) type = "BLR";
        if (contry.equals("伯利兹")) type = "BLZ";
        if (contry.equals("百慕大")) type = "BMU";
        if (contry.equals("玻利维亚")) type = "BOL";
        if (contry.equals("巴西")) type = "BRA";
        if (contry.equals("巴巴多斯")) type = "BRB";
        if (contry.equals("文莱")) type = "BRN";
        if (contry.equals("不丹")) type = "BTN";
        if (contry.equals("布维岛")) type = "BVT";
        if (contry.equals("博茨瓦纳")) type = "BWA";
        if (contry.equals("中非")) type = "CAF";
        if (contry.equals("加拿大")) type = "CAN";
        if (contry.equals("科科斯群岛")) type = "CCK";
        if (contry.equals("瑞士")) type = "CHE";
        if (contry.equals("智利")) type = "CHL";
        if (contry.equals("中国")) type = "CHN";
        if (contry.equals("科特迪瓦")) type = "CIV";
        if (contry.equals("喀麦隆")) type = "CMR";
        if (contry.equals("刚果(金)")) type = "COD";
        if (contry.equals("刚果(布)")) type = "COG";
        if (contry.equals("库克群岛")) type = "COK";
        if (contry.equals("哥伦比亚")) type = "COL";
        if (contry.equals("科摩罗")) type = "COM";
        if (contry.equals("佛得角")) type = "CPV";
        if (contry.equals("哥斯达黎加")) type = "CRI";
        if (contry.equals("古巴")) type = "CUB";
        if (contry.equals("圣诞岛")) type = "CXR";
        if (contry.equals("开曼群岛")) type = "CYM";
        if (contry.equals("塞浦路斯")) type = "CYP";
        if (contry.equals("捷克")) type = "CZE";
        if (contry.equals("德国")) type = "DEU";
        if (contry.equals("吉布提")) type = "DJI";
        if (contry.equals("多米尼克")) type = "DMA";
        if (contry.equals("丹麦")) type = "DNK";
        if (contry.equals("多米尼加")) type = "DOM";
        if (contry.equals("阿尔及利亚")) type = "DZA";
        if (contry.equals("厄瓜多尔")) type = "ECU";
        if (contry.equals("埃及")) type = "EGY";
        if (contry.equals("厄立特里亚")) type = "ERI";
        if (contry.equals("西撒哈拉")) type = "ESH";
        if (contry.equals("西班牙")) type = "ESP";
        if (contry.equals("爱沙尼亚")) type = "EST";
        if (contry.equals("埃塞俄比亚")) type = "ETH";
        if (contry.equals("芬兰")) type = "FIN";
        if (contry.equals("斐济")) type = "FJI";
        if (contry.equals("马尔维纳斯群岛")) type = "FLK";
        if (contry.equals("法国")) type = "FRA";
        if (contry.equals("法罗群岛")) type = "FRO";
        if (contry.equals("密克罗尼西亚")) type = "FSM";
        if (contry.equals("加蓬")) type = "GAB";
        if (contry.equals("英国（独立领土公民，出国不用）")) type = "GBD";
        if (contry.equals("英国（海外公民，出国不用）")) type = "GBO";
        if (contry.equals("英国（保护公民，出国不用）")) type = "GBP";
        if (contry.equals("英国")) type = "GBR";
        if (contry.equals("英国（隶属，出国不用）")) type = "GBS";
        if (contry.equals("格鲁吉亚")) type = "GEO";
        if (contry.equals("加纳")) type = "GHA";
        if (contry.equals("直布罗陀")) type = "GIB";
        if (contry.equals("几内亚")) type = "GIN";
        if (contry.equals("瓜德罗普")) type = "GLP";
        if (contry.equals("冈比亚")) type = "GMB";
        if (contry.equals("几内亚比绍")) type = "GNB";
        if (contry.equals("赤道几内亚")) type = "GNQ";
        if (contry.equals("希腊")) type = "GRC";
        if (contry.equals("格林纳达")) type = "GRD";
        if (contry.equals("格陵兰")) type = "GRL";
        if (contry.equals("危地马拉")) type = "GTM";
        if (contry.equals("法属圭亚那")) type = "GUF";
        if (contry.equals("关岛")) type = "GUM";
        if (contry.equals("圭亚那")) type = "GUY";
        if (contry.equals("香港")) type = "HKG";
        if (contry.equals("赫德岛和麦克唐纳岛")) type = "HMD";
        if (contry.equals("洪都拉斯")) type = "HND";
        return type;
    }

}

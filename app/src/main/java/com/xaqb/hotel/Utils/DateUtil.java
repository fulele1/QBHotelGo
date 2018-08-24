package com.xaqb.hotel.Utils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by fl on 2017/7/12.
 */

public class DateUtil {

    /**
     * 把时间戳转换为时间
     *
     * @param date
     * @return
     */
    public static String getDate(String date) {

        BigDecimal bd = new BigDecimal(date);
        Long dateLong = Long.parseLong(bd.toPlainString());
        return formatData("yyyy-MM-dd", dateLong);
    }

    /**
     * 把时间转化为时间戳
     *
     * @param dataFormat
     * @param timeStamp
     * @return
     */
    public static String formatData(String dataFormat, long timeStamp) {
        if (timeStamp == 0) {
            return "";
        }
        timeStamp = timeStamp * 1000;
        String result = "";
        SimpleDateFormat format = new SimpleDateFormat(dataFormat);
        result = format.format(new Date(timeStamp));
        return result;
    }


    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }


    public static String getNowTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }



    /**
     * 把时间转换为时间戳
     * @param time
     * @return
     */
    public static String data(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String times = null;
        try {
            date = sdr.parse(time);
            long l = date.getTime();
            String stf = String.valueOf(l);
            times = stf.substring(0, 10);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return times;
    }

}

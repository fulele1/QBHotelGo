package com.xaqianbai.QBHotelSecurutyGovernor.Utils;


import com.xaqianbai.QBHotelSecurutyGovernor.BuildConfig;

/**
 * Created by fule on 2017/3/15.
 */
public class HttpUrlUtils {
    private static HttpUrlUtils httpUrl = new HttpUrlUtils();

    public static HttpUrlUtils getHttpUrl() {
        return httpUrl;
    }

    public String getBaseUrl() {
        return BuildConfig.DEBUG ?"http://hotel.qbchoice.cn":"http://hotel.qbchoice.cn";
//        return BuildConfig.DEBUG ?"http://hotel.qbchoice.com":"http://hotel.qbchoice.com";
    }

    //  用户登录接口/governor/login
    public String getLoginUrl() {
        return getBaseUrl() + "/governor/login";
    }

    //  用户头像接口 /privite/image/118/:id/:field
    public String getOuPic() {
        return getBaseUrl() + "/privite/image/118/";
    }
    //  从业人员接口 /governor/staff/search
    public String getStaffList() {
        return getBaseUrl() + "/v1/governor/employee";
    }
    //  从业人员接口 /governor/staff/:id
    public String getStaffDetail() {
        return getBaseUrl() + "/governor/employee/";
    }

    //  /v1/governor/security_organization?nopage  治安机构
    public String getOrgList() {
        return getBaseUrl() + "/v1/governor/security_organization?nopage";
    }

    //  行政区划  /v1/governor/house_area?nopage
    public String getHouseList() {
        return getBaseUrl() + "/v1/governor/house_area?nopage";
    }
    //  旅客接口    /v1/governor/touristsearch
    public String passengerList() {
        return getBaseUrl() + "/v1/governor/tourist";
    }
    //  订单列表  v1/governor/ordersearch
    public String OrderList() {
        return getBaseUrl() + "/v1/governor/order";
    }
    //  旅客订单详情   /v1/governor/touristinfo
    public String OrderDetil() {
        return getBaseUrl() + "/v1/governor/tourist_history";
    }

    //  国内旅客的图片 /privite/image/113/:id/:field
    public String picInDel() {
        return getBaseUrl() + "/privite/image";
    }

    // 线索的图片 /privite/image/116/:id/:field
    public String picInclue() {
        return getBaseUrl() + "/privite/image/116/";
    }

    // 线索详情的图片 /privite/multi_img/116/

    public String picInclueDel() {
        return getBaseUrl() + "/privite/multi_img/116/";
    }

    // 人的图片 /privite/image/112/:id/:field
    public String picInPer() {
        return getBaseUrl() + "/privite/image/1006/";
    }
    // 酒店的图片 /privite/image/111/:id/:field
    public String picInHotel() {
        return getBaseUrl() + "/privite/image/1000/";
    }

    //  酒店列表   /governor/hotel/search
    public String HotelList() {
        return getBaseUrl() + "/governor/hotel";
    }
    //  酒店详情    /governor/hotel/:id
    public String HotelDel() {
        return getBaseUrl() + "/governor/hotel";
    }

    public String HotelDelnew() {
        return getBaseUrl() + "/v1/governor/hotel_condition";
    }
    //  管辖机构详情     /governor/statistics/search
    public String orgDel() {
        return getBaseUrl() + "/v1/governor/statistics";
    }

    //  线索列表      /governor/security_clue
    public String clueList() {
        return getBaseUrl() + "/governor/security_clue";
    }

    //  线索详情       /governor/security_clue/:id
    public String clueDetil() {
        return getBaseUrl() + "/governor/security_clue/";
    }

    //  联合检查        /governor/dailycheck     /v1/governor/unionchecks/
    public String LogList() {
        return getBaseUrl() + "/v1/governor/unionchecks";
    }
    //  联合检查详情        /governor/dailycheck/:id    /v1/governor/unionchecks/:id
    public String LogDet() {
        return getBaseUrl() + "/v1/governor/unioncheck/";
    }

    //  发案列表
    public String BothList() {
        return getBaseUrl() + "/v1/governor/crime";
    }


//    //  删除发案列表   /v1/governor/crime/:id
//    public String BothdeleteList() {
//        return getBaseUrl() + "/v1/governor/crime";
//    }


    //  处罚列表
    public String PunishmentList() {
        return getBaseUrl() + "/v1/governor/punish";
    }


    //  /v1/governor/refresh_token/:refresh_token
    public String getToken() {
        return getBaseUrl() + "/v1/governor/refresh_token";
    }

    //  生成图形验证码/v1/governor/captchar
    public String getCaptchar() {
        return getBaseUrl() + "/v1/governor/captchar";
    }

    //  找回密码    v1/governor/backpwd
    public String getBackPswUrl() {
        return getBaseUrl() + "/v1/governor/backpwd";
    }

    //  获取手机验证码接口  /v1/governor/sendsms
    public String getVerCode() {
        return getBaseUrl() + "/v1/governor/sendsms";
    }

    //  返回密码数据校验   /v1/governor/checktmp
    public String getcheckmpCode() {
        return getBaseUrl() + "/v1/governor/checktmp";
    }

    //  轮播图接口 http://api.ddks.comv1/staff/advertisement
    public String getPic() {
        return getBaseUrl() + "/v1/staff/advertisement";
    }

    //  修改个人信息接口 /v1/governor/user/:id
    public String getUpdataUserinfoUrl() {
        return getBaseUrl() + "/v1/governor/user/";
    }


    //  修改密码接口 /v1/governor/password
    public String getModifyPswUrl() {
        return getBaseUrl() + "/v1/governor/password";
    }

    //扫码查询
    //  /v1/governor/qrcode/hotel/:code
    public String get_result() {
        return getBaseUrl() +"/v1/governor/qrcode/hotel/";
    }
    //  获取刷新token接口   /v1/governor/refresh_token/:refresh_token
    public String getrefreshToken() {
        return getBaseUrl() + "/v1/governor/refresh_token/refresh_token/";
    }

}

package com.xaqb.unlock.Utils;

/**
 * Created by lenovo on 2017/3/15.
 */
public class HttpUrlUtils {
    private static HttpUrlUtils httpUrl = new HttpUrlUtils();

    public static HttpUrlUtils getHttpUrl() {
        return httpUrl;
    }

    //  基础url
//    public String getBaseUrl() {
//        return "http://ksapi.qbdongdong.com/";
//    }
    public String getBaseUrl() {
        return "http://api.ddkaisuo.net";
    }

    //  用户登录接口
    public String getLoginUrl() {
        return getBaseUrl() + "/v1/staff/login";
    }

    //  修改个人信息接口
    public String getUpdataUserinfoUrl() {
        return getBaseUrl() + "/v1/staff/info/";
    }

    //  实名认证接口
    public String getRealNameUrl() {
        return getBaseUrl() + "/v1/staff/realyapply";
    }

    //  修改密码接口
    public String getResetPswUrl() {
        return getBaseUrl() + "/v1/staff/revisepwd/";
    }

    //  重新设置密码接口
    public String getBackPswUrl() {
        return getBaseUrl() + "/v1/staff/forgetpwd/";
    }

    //  获取手机验证码接口
    public String getVerCode() {
        return getBaseUrl() + "/v1/common/sms/";
    }

    //  采集信息上传接口
    public String getOrderUrl() {
        return getBaseUrl() + "/v1/staff/collection/";
    }

    //  查看个人信息接口
    public String getUserInfo() {
        return getBaseUrl() + "/v1/staff/info/";
    }

    //  提交意见接口
    public String getUploadAdvise() {
        return getBaseUrl() + "/v1/staff/feedback";
    }

    //  获得锁具类型接口
    public String getLockType() {
        return getBaseUrl() + "/dict_data/101";
    }

    //  获得订单列表接口
    public String getOrderList() {
        return getBaseUrl() + "/v1/staff/order";
    }

    //  获得订单详情接口
    public String getOrderDetail() {
        return getBaseUrl() + "/v1/staff/order";
    }

    //  线下现金支付接口
    public String getPayCash() {
        return getBaseUrl() + "/v1/staff/pay/offlinepay/";
    }

    //  线上在线支付接口
    public String getPayOnline() {
        return getBaseUrl() + "/v1/staff/pay/onlinepay/";
    }

    //  获取支付状态接口
    public String getPayResult() {
        return getBaseUrl() + "/v1/staff/pay/orderstatus/";
    }

    //  获取实名认证信息接口
    public String getRealNameInfo() {
        return getBaseUrl() + "v1/staff/realyapply/";
    }

    //  获取收入明细接口
    public String getPayDetail() {
        return getBaseUrl() + "/v1/staff/pay/paydetail";
    }
    //  获取刷新token接口
    public String getrefreshToken() {
        return getBaseUrl() + "/v1/index/token/refresh_token/";
    }

    //App更新
    public String get_updata() {
        return getBaseUrl()+"/v1/staff/appupdate";
    }

    //http://www.ddkaisuo.net/dict_data/104?access_token=2b4c8d06616844d51836b8fb69429e09
    //获取民族信息
    public String get_nation() {
        return "/dict_data/104?";
    }

}

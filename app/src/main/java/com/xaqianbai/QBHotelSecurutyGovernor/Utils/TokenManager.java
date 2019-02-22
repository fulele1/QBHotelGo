package com.xaqianbai.QBHotelSecurutyGovernor.Utils;

import android.content.Context;

/**
 * Created by lenovo on 2017/4/21.
 * token管理者
 */
public class TokenManager {

    //    private String accessToken;
//    private String refreshToken;
    private Context context;//上下文
    //    private String tokenTime = "";
//    private String refreshTokenTime = "";
    private String ACCESSTOKEN = "access_token";//正常token
    private String REFRESHTOKEN = "refresh_token";//刷新token
    private String TOKENTIME = "tokenTime";//正常token有效时间
    private String REFRESHTOKENTIME = "refreshTokenTime";//刷新token有效时间


    public TokenManager(Context context) {
        this.context = context;
    }

    public String getAccessToken() {
        return SPUtils.get(context, ACCESSTOKEN, "").toString();
    }

    public void setAccessToken(String accessToken) {
        SPUtils.put(context, ACCESSTOKEN, accessToken);
    }

    public String getRefreshToken() {
        return SPUtils.get(context, REFRESHTOKEN, "").toString();
    }

    public void setRefreshToken(String refreshToken) {
        SPUtils.put(context, REFRESHTOKEN, refreshToken);
    }

    public String getTokenTime() {
        return SPUtils.get(context, TOKENTIME, 0L).toString();
    }

    public void setTokenTime(long tokenTime) {
        SPUtils.put(context, TOKENTIME, tokenTime);
    }

    public String getRefreshTokenTime() {
        return SPUtils.get(context, REFRESHTOKENTIME, 0L).toString();
    }

    public void setRefreshTokenTime(long refreshTokenTime) {
        SPUtils.put(context, REFRESHTOKENTIME, refreshTokenTime);
    }

    /**
     * 检查token是否过时
     *
     * @return 是否过时
     */
    public boolean checkToken() {
        if (System.currentTimeMillis() - Long.parseLong(getTokenTime()) > 7000) {
            return false;
        }
        return true;
    }

    /**
     * 检查刷新token是否过时
     *
     * @return 是否过时
     */
    public boolean checkRefreshToken() {
        if (System.currentTimeMillis() - Long.parseLong(getRefreshTokenTime()) < 7200) {
            return true;
        }
        return false;
    }


}

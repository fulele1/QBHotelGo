package com.xaqb.hotel.Listview;


import android.text.TextUtils;

import java.util.ArrayList;

public class BrandBean {
    /**
     * 城市名字首字母
     */
    private String headChar;
    /**
     * 城市id
     */
    private String city_id;
    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 省id
     */
    private String pro_id;
    /**
     * 城市名字
     */
    private String name;
    /**
     * 城市字母名字
     */
    private String name_en;
    /**
     * 是否是标题
     */
    private int type;

    public String getHeadChar() {
        return headChar;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        name_en = getPinYin(name);//获取字母名称
        name_en = name_en.toUpperCase();//把小写字母换成大写字母
        if (!TextUtils.isEmpty(name_en)) {
            char head = name_en.charAt(0);
            if (head < 'A' || head > 'Z') {
                head = '#';
            }
            headChar = head + "";
        }
    }

    public String getName_en() {
        return name_en;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    /**
     * 汉字转换拼音，字母原样返回，都转换为小写
     */
    public String getPinYin(String input) {
        ArrayList<HanziToPinyin.Token> tokens = HanziToPinyin.getInstance().get(input);
        StringBuilder sb = new StringBuilder();
        if (tokens != null && tokens.size() > 0) {
            for (HanziToPinyin.Token token : tokens) {
                if (token.type == HanziToPinyin.Token.PINYIN) {
                    sb.append(token.target);
                } else {
                    sb.append(token.source);
                }
            }
        }
        return sb.toString().toLowerCase();
    }

    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getPro_id() {
        return pro_id;
    }

    public void setPro_id(String pro_id) {
        this.pro_id = pro_id;
    }
}

package com.xaqianbai.QBHotelSecurutyGovernor.Entity;

/**
 * Created by lenovo on 2018/5/15.
 */

public class Crime {

    private String id;
    private String hname;
    private String date;
    private String type_one;
    private String type_two;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getType_one() {
        return type_one;
    }

    public void setType_one(String type_one) {
        this.type_one = type_one;
    }

    public String getType_two() {
        return type_two;
    }

    public void setType_two(String type_two) {
        this.type_two = type_two;
    }

    public String getDel() {
        return del;
    }

    public void setDel(String del) {
        this.del = del;
    }

    private String del;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }



    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }



}

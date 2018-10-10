package com.xaqianbai.QBHotelSecurutyGovernor.Entity;

/**
 * Created by lenovo on 2018/5/15.
 */

public class Log {

    private String id;
    private String day;
    private String date;
    private String org;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDet() {
        return det;
    }

    public void setDet(String det) {
        this.det = det;
    }

    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    private String det;

}

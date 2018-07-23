package com.xaqb.hotel.Entity;

/**
 * Created by lenovo on 2018/5/15.
 */

public class Clue {
    private String id;
    private String pic;
    private String name;
    private String date;
    private String tel;

    public String getGood() {
        return good;
    }

    public void setGood(String good) {
        this.good = good;
    }

    private String good;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;
}

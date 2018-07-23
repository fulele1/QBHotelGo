package com.xaqb.hotel.Entity;

import com.xaqb.hotel.Activity.RLview.Entity;

/**
 * Created by lenovo on 2018/5/18.
 */

public class Order extends Entity{
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    private String pic;
    private String passenger;
    private String sex;
    private String iden_type;
    private String iden;

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    private String pk;

    public String getDt_id() {
        return dt_id;
    }

    public void setDt_id(String dt_id) {
        this.dt_id = dt_id;
    }

    private String dt_id;

    public String getPass_type() {
        return pass_type;
    }

    public void setPass_type(String pass_type) {
        this.pass_type = pass_type;
    }

    private String pass_type;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPassenger() {
        return passenger;
    }

    public void setPassenger(String passenger) {
        this.passenger = passenger;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getIden_type() {
        return iden_type;
    }

    public void setIden_type(String iden_type) {
        this.iden_type = iden_type;
    }

    public String getIden() {
        return iden;
    }

    public void setIden(String iden) {
        this.iden = iden;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String address;
}

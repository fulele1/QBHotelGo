package com.xaqb.hotel.Entity;

/**
 * Created by lenovo on 2018/5/10.
 */

public class Passenger {
    private String id;
    private String name;
    private String iden;
    private String idenType;
    private String tel;
    private String sex;
    private String dt_id;

    public String getDt_id() {
        return dt_id;
    }

    public void setDt_id(String dt_id) {
        this.dt_id = dt_id;
    }

    public String getPassType() {
        return passType;
    }

    public void setPassType(String passType) {
        this.passType = passType;
    }

    private String passType;

    public String getIdenType() {
        return idenType;
    }

    public void setIdenType(String idenType) {
        this.idenType = idenType;
    }


    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String pic;
    private String address;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIden() {
        return iden;
    }

    public void setIden(String iden) {
        this.iden = iden;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;
}

package com.xaqb.unlock.Entity;

import java.io.Serializable;

/**
 * Created by fl on 2017/5/17.
 */

public class Order implements Serializable {

    private String eoid;
    private String eoorderno;
    private String eotitle;
    private String eostatus;
    private String eostuffimg;
    private String eoremark;
    private String eodestprovince;
    private String eodestcity;
    private String eodestdistrict;
    private String eodestaddress;
    private String eoexpressstaff;
    private String stafftel;
    private String eodestname;
    private String eodestmp;
    private String eocreatetime;

    public String getEodestcity() {
        return eodestcity;
    }

    public void setEodestcity(String eodestcity) {
        this.eodestcity = eodestcity;
    }

    public String getEodestdistrict() {
        return eodestdistrict;
    }

    public void setEodestdistrict(String eodestdistrict) {
        this.eodestdistrict = eodestdistrict;
    }

    public String getEodestaddress() {
        return eodestaddress;
    }

    public void setEodestaddress(String eodestaddress) {
        this.eodestaddress = eodestaddress;
    }

    public String getEoexpressstaff() {
        return eoexpressstaff;
    }

    public void setEoexpressstaff(String eoexpressstaff) {
        this.eoexpressstaff = eoexpressstaff;
    }

    public String getStafftel() {
        return stafftel;
    }

    public void setStafftel(String stafftel) {
        this.stafftel = stafftel;
    }

    public String getEodestname() {
        return eodestname;
    }

    public void setEodestname(String eodestname) {
        this.eodestname = eodestname;
    }

    public String getEodestmp() {
        return eodestmp;
    }

    public void setEodestmp(String eodestmp) {
        this.eodestmp = eodestmp;
    }

    public String getEocreatetime() {
        return eocreatetime;
    }

    public void setEocreatetime(String eocreatetime) {
        this.eocreatetime = eocreatetime;
    }

    public String getEoid() {
        return eoid;
    }

    public void setEoid(String eoid) {
        this.eoid = eoid;
    }

    public String getEoorderno() {
        return eoorderno;
    }

    public void setEoorderno(String eoorderno) {
        this.eoorderno = eoorderno;
    }

    public String getEotitle() {
        return eotitle;
    }

    public void setEotitle(String eotitle) {
        this.eotitle = eotitle;
    }

    public String getEostatus() {
        return eostatus;
    }

    public void setEostatus(String eostatus) {
        this.eostatus = eostatus;
    }

    public String getEostuffimg() {
        return eostuffimg;
    }

    public void setEostuffimg(String eostuffimg) {
        this.eostuffimg = eostuffimg;
    }

    public String getEoremark() {
        return eoremark;
    }

    public void setEoremark(String eoremark) {
        this.eoremark = eoremark;
    }

    public String getEodestprovince() {
        return eodestprovince;
    }

    public void setEodestprovince(String eodestprovince) {
        this.eodestprovince = eodestprovince;
    }
}

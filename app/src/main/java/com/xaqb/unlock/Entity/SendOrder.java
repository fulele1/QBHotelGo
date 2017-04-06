package com.xaqb.unlock.Entity;

/**
 * 已发数据订单实体类
 * Created by lenovo on 2017/4/1.
 */
public class SendOrder extends Entity {
    //订单发送时间
    private String orderTime;
    //订单发送地址
    private String orderAddress;
    //订单编号
    private String orderNo;
    //订单ID
    private String orderID;
    //订单支付状态
    private String orderPayStatus;

    public SendOrder() {
    }

    public SendOrder(String orderTime, String orderAddress, String orderNo, String orderID, String orderPayStatus) {
        this.orderTime = orderTime;
        this.orderAddress = orderAddress;
        this.orderNo = orderNo;
        this.orderID = orderID;
        this.orderPayStatus = orderPayStatus;
    }

    @Override
    public String toString() {
        return "SendOrder{" +
                "orderTime='" + orderTime + '\'' +
                ", orderAddress='" + orderAddress + '\'' +
                ", orderNo='" + orderNo + '\'' +
                ", orderID='" + orderID + '\'' +
                ", orderPayStatus='" + orderPayStatus + '\'' +
                '}';
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderAddress() {
        return orderAddress;
    }

    public void setOrderAddress(String orderAddress) {
        this.orderAddress = orderAddress;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderPayStatus() {
        return orderPayStatus;
    }

    public void setOrderPayStatus(String orderPayStatus) {
        this.orderPayStatus = orderPayStatus;
    }
}

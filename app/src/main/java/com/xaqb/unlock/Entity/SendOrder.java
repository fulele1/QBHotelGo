package com.xaqb.unlock.Entity;

/**
 * 已发数据订单实体类
 * Created by lenovo on 2017/4/1.
 */
public class SendOrder extends Entity{
    //订单发送时间
    private String orderTime;
    //订单发送地址
    private String orderAddress;
    //订单编号
    private String orderNo;

    public SendOrder(String orderTime, String orderAddress, String orderNo) {
        this.orderTime = orderTime;
        this.orderAddress = orderAddress;
        this.orderNo = orderNo;
    }

    public SendOrder() {
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

    @Override
    public String toString() {
        return "SendOrder{" +
                "orderTime='" + orderTime + '\'' +
                ", orderAddress='" + orderAddress + '\'' +
                ", orderNo='" + orderNo + '\'' +
                '}';
    }
}

package com.xaqb.unlock.Entity;

/**
 * 收入明细实体类
 * Created by lenovo on 2017/4/13.
 */
public class IncomeInfo extends Entity {


    private String id;
    /**
     * 订单编号
     */
    private String orderId;
    /**
     * 下单时间
     */
    private String orderTime;
    /**
     * 下单金额
     */
    private String orderPrice;
    /**
     * 支付类型
     */
    private String payType;
    /**
     * 支付流水号
     */
    private String serialNumber;
    /**
     * 支付状态
     */
    private String payStatus;

    public IncomeInfo() {
    }

    public IncomeInfo(String id, String orderId, String orderTime, String orderPrice, String payType, String serialNumber,String payStatus) {
        this.id = id;
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.orderPrice = orderPrice;
        this.payType = payType;
        this.serialNumber = serialNumber;
        this.payStatus = payStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(String orderPrice) {
        this.orderPrice = orderPrice;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getPayStatus() {
        return payStatus;
    }

    public void setPayStatus(String payStatus) {
        this.payStatus = payStatus;
    }

    @Override
    public String toString() {
        return "IncomeInfo{" +
                "serialNumber='" + serialNumber + '\'' +
                ", payType='" + payType + '\'' +
                ", orderPrice='" + orderPrice + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", orderId='" + orderId + '\'' +
                ", id='" + id + '\'' +
                ", payStatus='" + payStatus + '\'' +
                '}';
    }
}

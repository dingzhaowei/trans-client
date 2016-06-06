package com.ding.trans.client.model;

import java.util.Map;

import com.ding.trans.client.ClientUtil;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TransOrder {

    public static final String TRANS_ID = "transId";

    public static final String ORDER_NO = "orderNo";

    public static final String HAPPEN_TIME = "happenTime";

    public static final String ORDER_STATUS = "orderStatus";

    public static final String WAREHOUSE_NAME = "warehouseName";

    public static final String TRANSPLAN_NAME = "transPlanName";

    private StringProperty transId;

    private StringProperty orderNo;

    private StringProperty happenTime;

    private StringProperty orderStatus;

    private StringProperty warehouseName;

    private StringProperty transPlanName;

    public TransOrder() {
        transId = new SimpleStringProperty();
        orderNo = new SimpleStringProperty();
        happenTime = new SimpleStringProperty();
        orderStatus = new SimpleStringProperty();
        warehouseName = new SimpleStringProperty();
        transPlanName = new SimpleStringProperty();
    }

    public final StringProperty transIdProperty() {
        return this.transId;
    }

    public final String getTransId() {
        return this.transIdProperty().get();
    }

    public final void setTransId(final String transId) {
        this.transIdProperty().set(transId);
    }

    public final StringProperty orderNoProperty() {
        return this.orderNo;
    }

    public final String getOrderNo() {
        return this.orderNoProperty().get();
    }

    public final void setOrderNo(final String orderNo) {
        this.orderNoProperty().set(orderNo);
    }

    public final StringProperty happenTimeProperty() {
        return this.happenTime;
    }

    public final String getHappenTime() {
        return this.happenTimeProperty().get();
    }

    public final void setHappenTime(final String happenTime) {
        this.happenTimeProperty().set(happenTime);
    }

    public final StringProperty orderStatusProperty() {
        return this.orderStatus;
    }

    public final String getOrderStatus() {
        return this.orderStatusProperty().get();
    }

    public final void setOrderStatus(final String orderStatus) {
        this.orderStatusProperty().set(orderStatus);
    }

    public final StringProperty warehouseNameProperty() {
        return this.warehouseName;
    }

    public final String getWarehouseName() {
        return this.warehouseNameProperty().get();
    }

    public final void setWarehouseName(final String warehouseName) {
        this.warehouseNameProperty().set(warehouseName);
    }

    public final StringProperty transPlanNameProperty() {
        return this.transPlanName;
    }

    public final String getTransPlanName() {
        return this.transPlanNameProperty().get();
    }

    public final void setTransPlanName(final String transPlanName) {
        this.transPlanNameProperty().set(transPlanName);
    }

    public static TransOrder valueOf(Map<String, Object> m) {
        TransOrder order = new TransOrder();
        order.setTransId(ClientUtil.getStringValue(m.get(TRANS_ID)));
        order.setOrderNo(ClientUtil.getStringValue(m.get(ORDER_NO)));
        order.setHappenTime(ClientUtil.getStringValue(m.get(HAPPEN_TIME)));
        order.setOrderStatus(ClientUtil.getStringValue(m.get(ORDER_STATUS)));
        order.setWarehouseName(ClientUtil.getStringValue(m.get(WAREHOUSE_NAME)));
        order.setTransPlanName(ClientUtil.getStringValue(m.get(TRANSPLAN_NAME)));
        return order;
    }

}

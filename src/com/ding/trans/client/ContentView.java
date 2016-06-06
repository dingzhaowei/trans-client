package com.ding.trans.client;

import java.util.HashMap;
import java.util.Map;

import com.ding.trans.client.model.TransOrder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ContentView {

    // 待入库
    Tab drkOrdersTab;

    TableView<TransOrder> drkOrdersView;

    // 待付款
    Tab dfkOrdersTab;

    TableView<TransOrder> dfkOrdersView;

    // 待签收
    Tab dqsOrdersTab;

    TableView<TransOrder> dqsOrdersView;

    // 已签收
    Tab yqsOrdersTab;

    TableView<TransOrder> yqsOrdersView;

    private BorderPane layout = new BorderPane();

    private ScheduledService<Void> transOrderViewsUpdate;

    public ContentView() {
        drkOrdersTab = new Tab("待入库");
        drkOrdersTab.setClosable(false);
        drkOrdersView = new TableView<>();
        createTrasOrderViewUI(drkOrdersView);
        drkOrdersTab.setContent(drkOrdersView);

        dfkOrdersTab = new Tab("待付款");
        dfkOrdersTab.setClosable(false);
        dfkOrdersView = new TableView<>();
        createTrasOrderViewUI(dfkOrdersView);
        dfkOrdersTab.setContent(dfkOrdersView);

        dqsOrdersTab = new Tab("待签收");
        dqsOrdersTab.setClosable(false);
        dqsOrdersView = new TableView<>();
        createTrasOrderViewUI(dqsOrdersView);
        dqsOrdersTab.setContent(dqsOrdersView);

        yqsOrdersTab = new Tab("已签收");
        yqsOrdersTab.setClosable(false);
        yqsOrdersView = new TableView<>();
        createTrasOrderViewUI(yqsOrdersView);
        yqsOrdersTab.setContent(yqsOrdersView);

        TabPane centerTabPane = new TabPane();
        centerTabPane.setTabMinWidth(100.0);
        centerTabPane.getTabs().add(drkOrdersTab);
        centerTabPane.getTabs().add(dfkOrdersTab);
        centerTabPane.getTabs().add(dqsOrdersTab);
        centerTabPane.getTabs().add(yqsOrdersTab);
        centerTabPane.getStyleClass().add("content-center-tabpane");

        VBox centerLayout = new VBox();
        centerLayout.setSpacing(5);
        centerLayout.getChildren().addAll(centerTabPane);
        VBox.setVgrow(centerTabPane, Priority.ALWAYS);
        centerLayout.getStyleClass().add("content-center-layout");

        layout.setCenter(centerLayout);
        new ContentViewController(this).bind();
    }

    public Node getView() {
        return layout;
    }

    public void init() {
        startTransOrderViewsUpdate();
    }

    public void clear() {
        if (transOrderViewsUpdate != null) {
            transOrderViewsUpdate.cancel();
        }
        drkOrdersView.getItems().clear();
        showTransOrderCountOnTab("DRK", 0);
        dfkOrdersView.getItems().clear();
        showTransOrderCountOnTab("DFK", 0);
        dqsOrdersView.getItems().clear();
        showTransOrderCountOnTab("DQS", 0);
        yqsOrdersView.getItems().clear();
        showTransOrderCountOnTab("YQS", 0);
    }

    private void startTransOrderViewsUpdate() {
        Map<String, ObservableList<TransOrder>> m = new HashMap<>();

        transOrderViewsUpdate = new ScheduledService<Void>() {

            @Override
            protected Task<Void> createTask() {
                return ClientUtil.createAsyncTask(() -> {
                    m.put("DRK", FXCollections.observableArrayList());
                    m.put("DFK", FXCollections.observableArrayList());
                    m.put("DQS", FXCollections.observableArrayList());
                    m.put("YQS", FXCollections.observableArrayList());

                    RemoteDriver driver = RemoteDriver.instance();
                    for (TransOrder order : driver.fetchTransOrders()) {
                        String orderStatus = order.getOrderStatus();
                        if (orderStatus.contains("待入库")) {
                            m.get("DRK").add(order);
                        } else if (orderStatus.contains("待付款")) {
                            m.get("DFK").add(order);
                        } else if (orderStatus.contains("待签收")) {
                            m.get("DQS").add(order);
                        } else if (orderStatus.contains("已签收")) {
                            m.get("YQS").add(order);
                        }
                    }
                    return null;
                });
            }

        };

        ClientUtil.onAsyncTaskSuccess(transOrderViewsUpdate, () -> {
            if (!RemoteDriver.instance().isLogined()) {
                return;
            }
            drkOrdersView.setItems(m.get("DRK"));
            showTransOrderCountOnTab("DRK", m.get("DRK").size());
            dfkOrdersView.setItems(m.get("DFK"));
            showTransOrderCountOnTab("DFK", m.get("DFK").size());
            dqsOrdersView.setItems(m.get("DQS"));
            showTransOrderCountOnTab("DQS", m.get("DQS").size());
            yqsOrdersView.setItems(m.get("YQS"));
            showTransOrderCountOnTab("YQS", m.get("YQS").size());
        }, null);

        transOrderViewsUpdate.setRestartOnFailure(true);
        transOrderViewsUpdate.setPeriod(Duration.minutes(10.0));
        transOrderViewsUpdate.start();
    }

    private void showTransOrderCountOnTab(String status, int count) {
        switch (status) {
        case "DRK":
            drkOrdersTab.setText(count > 0 ? "待入库(" + count + ")" : "待入库");
            break;
        case "DFK":
            dfkOrdersTab.setText(count > 0 ? "待付款(" + count + ")" : "待付款");
            break;
        case "DQS":
            dqsOrdersTab.setText(count > 0 ? "待签收(" + count + ")" : "待签收");
            break;
        case "YQS":
            yqsOrdersTab.setText(count > 0 ? "已签收(" + count + ")" : "已签收");
            break;
        default:
            break;
        }
    }

    private void createTrasOrderViewUI(TableView<TransOrder> transOrdersView) {
        transOrdersView.setStyle("-fx-font-size: 12px");
        transOrdersView.setPlaceholder(new Label(""));
        transOrdersView.widthProperty().addListener((ob, ov, nv) -> {
            Pane header = (Pane) transOrdersView.lookup("TableHeaderRow");
            if (header != null && header.isVisible()) {
                header.setMaxHeight(0);
                header.setMinHeight(0);
                header.setPrefHeight(0);
                header.setVisible(false);
                header.setManaged(false);
            }
        });

        TableColumn<TransOrder, String> transIdCol;
        TableColumn<TransOrder, String> happenTimeCol;
        TableColumn<TransOrder, String> orderStatusCol;
        TableColumn<TransOrder, String> warehouseNameCol;
        TableColumn<TransOrder, String> transPlanNameCol;

        transIdCol = ClientUtil.createTableColumn("商家单号", TransOrder.TRANS_ID);
        transOrdersView.getColumns().add(transIdCol);
        happenTimeCol = ClientUtil.createTableColumn("发生时间", TransOrder.HAPPEN_TIME);
        transOrdersView.getColumns().add(happenTimeCol);
        orderStatusCol = ClientUtil.createTableColumn("运单状态", TransOrder.ORDER_STATUS);
        transOrdersView.getColumns().add(orderStatusCol);
        warehouseNameCol = ClientUtil.createTableColumn("所在仓库", TransOrder.WAREHOUSE_NAME);
        transOrdersView.getColumns().add(warehouseNameCol);
        transPlanNameCol = ClientUtil.createTableColumn("运输路线", TransOrder.TRANSPLAN_NAME);
        transOrdersView.getColumns().add(transPlanNameCol);
        transOrdersView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

}

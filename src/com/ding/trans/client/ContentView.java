package com.ding.trans.client;

import java.util.HashMap;
import java.util.Map;

import com.ding.trans.client.model.TransOrder;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToolBar;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.util.Duration;

public class ContentView {

    WebView orderDetailView;

    Button addOneTransOrderBtn;

    Button addManyTransOrderBtn;

    Button commentTransOrderBtn;

    Button payFreightBtn;

    Button payTaxBtn;

    Button deleteTransOrderBtn;

    Button modifyShipAddressBtn;

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

        ImageView icon1 = ClientUtil.createIcon("add-one.png", 24);
        addOneTransOrderBtn = ClientUtil.createImageButton(icon1);
        addOneTransOrderBtn.setPadding(new Insets(0));
        addOneTransOrderBtn.setDisable(true);

        ImageView icon2 = ClientUtil.createIcon("add-many.png", 24);
        addManyTransOrderBtn = ClientUtil.createImageButton(icon2);
        addManyTransOrderBtn.setPadding(new Insets(0));
        addManyTransOrderBtn.setDisable(true);

        ImageView icon3 = ClientUtil.createIcon("remark.png", 24);
        commentTransOrderBtn = ClientUtil.createImageButton(icon3);
        commentTransOrderBtn.setPadding(new Insets(0));
        commentTransOrderBtn.setDisable(true);

        ImageView icon4 = ClientUtil.createIcon("freight.png", 24);
        payFreightBtn = ClientUtil.createImageButton(icon4);
        payFreightBtn.setPadding(new Insets(0));
        payFreightBtn.setDisable(true);

        ImageView icon5 = ClientUtil.createIcon("taxation.png", 24);
        payTaxBtn = ClientUtil.createImageButton(icon5);
        payTaxBtn.setPadding(new Insets(0));
        payTaxBtn.setDisable(true);

        ImageView icon6 = ClientUtil.createIcon("address.png", 24);
        modifyShipAddressBtn = ClientUtil.createImageButton(icon6);
        modifyShipAddressBtn.setPadding(new Insets(0));
        modifyShipAddressBtn.setDisable(true);

        ImageView icon7 = ClientUtil.createIcon("delete.png", 24);
        deleteTransOrderBtn = ClientUtil.createImageButton(icon7);
        deleteTransOrderBtn.setPadding(new Insets(0));
        deleteTransOrderBtn.setDisable(true);

        ToolBar toolBar = new ToolBar();
        toolBar.setMaxWidth(250.0);
        toolBar.getItems().add(addOneTransOrderBtn);
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(addManyTransOrderBtn);
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(commentTransOrderBtn);
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(payFreightBtn);
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(payTaxBtn);
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(deleteTransOrderBtn);
        toolBar.getItems().add(new Separator(Orientation.VERTICAL));
        toolBar.getItems().add(modifyShipAddressBtn);

        orderDetailView = new WebView();
        orderDetailView.maxWidthProperty().bind(toolBar.widthProperty());

        VBox rightLayout = new VBox();
        rightLayout.setSpacing(5);
        rightLayout.getChildren().add(toolBar);
        rightLayout.getChildren().add(orderDetailView);
        VBox.setVgrow(orderDetailView, Priority.ALWAYS);
        rightLayout.getStyleClass().add("content-right-layout");

        layout.setCenter(centerLayout);
        layout.setRight(rightLayout);
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
                        } else if (orderStatus.contains("待出库")) {
                            m.get("DQS").add(order);
                        } else if (orderStatus.contains("派送中")) {
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

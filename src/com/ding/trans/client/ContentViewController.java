package com.ding.trans.client;

import com.ding.trans.client.model.TransOrder;
import com.ding.trans.client.model.TransOrderDetail;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.stage.Window;

public class ContentViewController {

    private ContentView view;

    private RemoteDriver driver;

    public ContentViewController(ContentView view) {
        this.view = view;
        this.driver = RemoteDriver.instance();
    }

    public void bind() {
        ChangeListener<TransOrder> orderSelChangeListener = (ob, ov, nv) -> {
            if (nv != null) {
                String transId = nv.getTransId();
                Window loading = Main.showLoadingMsg();
                Task<TransOrderDetail> task = ClientUtil.createAsyncTask(() -> {
                    return driver.fetchTransOrderDetail(transId);
                });
                ClientUtil.onAsyncTaskSuccess(task, () -> {
                    TransOrderDetail detail = task.getValue();
                    String formattedDetail = detail.getFormattedDetail();
                    view.orderDetailView.getEngine().loadContent(formattedDetail);
                }, null, loading);
                ClientUtil.onAsyncTaskFailure(task, null, loading);
                ClientUtil.runAsyncTask(task);
            }
        };

        view.drkOrdersView.getSelectionModel().selectedItemProperty().addListener(orderSelChangeListener);
        view.dfkOrdersView.getSelectionModel().selectedItemProperty().addListener(orderSelChangeListener);
        view.dqsOrdersView.getSelectionModel().selectedItemProperty().addListener(orderSelChangeListener);
        view.yqsOrdersView.getSelectionModel().selectedItemProperty().addListener(orderSelChangeListener);
    }

}

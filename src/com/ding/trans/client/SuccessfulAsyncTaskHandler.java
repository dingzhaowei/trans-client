package com.ding.trans.client;

import javafx.scene.Node;

public interface SuccessfulAsyncTaskHandler {

    void onSuccess(Node... nodesToUpdate);

}

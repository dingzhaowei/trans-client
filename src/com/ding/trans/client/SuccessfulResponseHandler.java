package com.ding.trans.client;

import org.apache.http.client.methods.CloseableHttpResponse;

public interface SuccessfulResponseHandler {

    void onSuccess(CloseableHttpResponse resp) throws Exception;

}

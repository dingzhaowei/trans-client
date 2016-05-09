package com.ding.trans.client;

public class RemoteDriverException extends Exception {

    private static final long serialVersionUID = 1L;

    public RemoteDriverException(String message) {
        super(message);
    }

    public RemoteDriverException(String message, Exception cause) {
        super(message, cause);
    }

}

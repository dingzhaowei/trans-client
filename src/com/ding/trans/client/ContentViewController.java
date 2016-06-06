package com.ding.trans.client;

public class ContentViewController {

    private ContentView view;

    private RemoteDriver driver;

    public ContentViewController(ContentView view) {
        this.view = view;
        this.driver = RemoteDriver.instance();
    }

    public void bind() {

    }

}

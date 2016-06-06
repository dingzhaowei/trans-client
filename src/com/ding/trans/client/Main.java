package com.ding.trans.client;

import java.io.File;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Main extends Application {

    public static final String CSS = Config.getResource("lunatrans.css").toExternalForm();

    private static Stage primaryStage;

    private static HeaderView headerView;

    private static ContentView contentView;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;

        BorderPane root = new BorderPane();
        headerView = new HeaderView();
        root.setTop(headerView.getView());
        contentView = new ContentView();
        root.setCenter(contentView.getView());

        Scene scene = new Scene(root);
        scene.getStylesheets().add(CSS);
        stage.setScene(scene);
        stage.setWidth(getScreenWidth() * 0.8);
        stage.setHeight(getScreenHeight() * 0.8);
        stage.setTitle("转运四方(luna)");
        stage.show();
    }

    public static void main(String[] args) {
        new File(Config.getApplicationDataDir()).mkdirs();
        launch(args);
    }

    public static ContentView getContentView() {
        return contentView;
    }

    public static double getScreenWidth() {
        return Screen.getPrimary().getVisualBounds().getWidth();
    }

    public static double getScreenHeight() {
        return Screen.getPrimary().getVisualBounds().getHeight();
    }

    public static void showSuccessMsg(String msg) {
        new SuccessMessagePane(getFocusedWindow(), msg).show();
    }

    public static void showFailureMsg(String msg, Runnable handler) {
        new FailureMessagePane(getFocusedWindow(), msg, handler).show();
    }

    public static void showConfirmMsg(String msg, Runnable handler) {
        new ConfirmMessagePane(getFocusedWindow(), msg, handler).show();
    }

    public static Stage showLoadingMsg() {
        return new LoadingMessagePane(getFocusedWindow()).show();
    }

    private static Window getFocusedWindow() {
        return primaryStage;
    }

}

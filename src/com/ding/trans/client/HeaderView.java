package com.ding.trans.client;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class HeaderView {

    TextField usernameField;

    PasswordField passwordField;

    Label welcomeLabel;

    HBox loginBox, logoutBox;

    Button loginBtn, logoutBtn, settingsBtn;

    private AnchorPane layout = new AnchorPane();

    public HeaderView() {
        ImageView logoImg = ClientUtil.createIcon("logo.gif", 200);

        loginBox = new HBox();
        loginBox.getStyleClass().add("header-login");

        Label usernameLabel = new Label("用户名:");
        loginBox.getChildren().add(usernameLabel);
        usernameField = new TextField();
        loginBox.getChildren().add(usernameField);

        Label passwordLabel = new Label("密码:");
        loginBox.getChildren().add(passwordLabel);
        passwordField = new PasswordField();
        loginBox.getChildren().add(passwordField);

        if (Config.getBoolean("AutoFillLogin")) {
            String v = Config.getValue("UserName");
            if (v != null && !v.isEmpty()) {
                String userName = ClientUtil.decipherSimply(v);
                usernameField.setText(userName);
            }
            v = Config.getValue("Password");
            if (v != null && !v.isEmpty()) {
                String password = ClientUtil.decipherSimply(v);
                passwordField.setText(password);
            }
        }

        loginBtn = new Button("登录");
        loginBox.getChildren().add(loginBtn);

        logoutBox = new HBox();
        logoutBox.getStyleClass().add("header-login");
        welcomeLabel = new Label("你好！");
        logoutBtn = new Button("退出");
        settingsBtn = new Button("设置");
        logoutBox.getChildren().add(welcomeLabel);
        logoutBox.getChildren().add(logoutBtn);
        logoutBox.getChildren().add(settingsBtn);

        StackPane stackPane = new StackPane(loginBox, logoutBox);
        loginBox.setVisible(true);
        logoutBox.setVisible(false);

        layout.getStyleClass().add("header-layout");
        layout.getChildren().addAll(logoImg, stackPane);
        AnchorPane.setLeftAnchor(logoImg, 20.0);
        AnchorPane.setRightAnchor(stackPane, 20.0);
        new HeaderViewController(this).bind();
    }

    public Node getView() {
        return layout;
    }

}

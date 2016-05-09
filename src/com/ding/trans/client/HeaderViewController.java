package com.ding.trans.client;

import javafx.concurrent.Task;
import javafx.stage.Window;

public class HeaderViewController {

    private HeaderView view;

    private RemoteDriver driver;

    public HeaderViewController(HeaderView view) {
        this.view = view;
        this.driver = RemoteDriver.instance();
    }

    public void bind() {
        view.passwordField.setOnAction(ae -> login());

        view.loginBtn.setOnAction(ae -> login());

        view.logoutBtn.setOnAction(ae -> logout());

        view.settingsBtn.setOnAction(ae -> {
            // SettingCenter.instance().show();
        });
    }

    private void login() {
        String userName = view.usernameField.getText().trim();
        String password = view.passwordField.getText().trim();
        view.loginBtn.setDisable(true);
        Window loading = Main.showLoadingMsg();
        Task<Void> task = ClientUtil.createAsyncTask(() -> {
            Validation.validateNotEmpty("用户名", userName);
            Validation.validateMaxSize("用户名", userName, 50);
            Validation.validateNotEmpty("密码", password);
            Validation.validateMaxSize("密码", password, 50);
            driver.login(userName, password);
            return null;
        });
        ClientUtil.onAsyncTaskSuccess(task, () -> {
            Config.setValue("UserName", ClientUtil.cipherSimply(userName));
            Config.setValue("Password", ClientUtil.cipherSimply(password));
            view.welcomeLabel.setText(userName);
            view.loginBox.setVisible(false);
            view.logoutBox.setVisible(true);
            view.logoutBtn.requestFocus();
        }, "登录成功！", view.loginBtn, loading);
        ClientUtil.onAsyncTaskFailure(task, null, view.loginBtn, loading);
        ClientUtil.runAsyncTask(task);
    }

    private void logout() {
        view.logoutBtn.setDisable(true);
        Window loading = Main.showLoadingMsg();
        Task<Void> task = ClientUtil.createAsyncTask(() -> {
            driver.logout();
            return null;
        });
        ClientUtil.onAsyncTaskSuccess(task, () -> {
            view.logoutBox.setVisible(false);
            view.loginBox.setVisible(true);
            view.loginBox.requestFocus();
            view.usernameField.requestFocus();
        }, "退出登录成功！", view.logoutBtn, loading);
        ClientUtil.onAsyncTaskFailure(task, null, view.logoutBtn, loading);
        ClientUtil.runAsyncTask(task);
    }

}

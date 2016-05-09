package com.ding.trans.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class InputDialog {

    TextField inputField;

    Button confirmBtn;

    Stage stage;

    public InputDialog(Window owner, String title, boolean required) {
        Label titleLabel = new Label(title);
        inputField = new TextField();
        Button confirmBtn = new Button("确定");
        confirmBtn.setOnAction(e -> {
            String text = inputField.getText();
            if (text.isEmpty() && required) {
                return;
            }
            stage.close();
        });

        HBox root = new HBox();
        root.setSpacing(10);
        root.setPadding(new Insets(5.0));
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("input-dialog");
        root.getChildren().addAll(titleLabel, inputField, confirmBtn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Main.CSS);

        stage = new Stage(StageStyle.TRANSPARENT);
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setScene(scene);
        stage.setOnShown(e -> ClientUtil.moveStageToOwnerCenter(stage));
    }

    public String show() {
        stage.showAndWait();
        return inputField.getText();
    }

}

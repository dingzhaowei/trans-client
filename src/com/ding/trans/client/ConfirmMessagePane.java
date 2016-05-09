package com.ding.trans.client;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class ConfirmMessagePane {

    Stage stage;

    public ConfirmMessagePane(Window owner, String message, Runnable handler) {
        Label messageLabel = new Label(message);
        messageLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        messageLabel.setGraphicTextGap(5);
        messageLabel.setMaxWidth(Main.getScreenWidth() * 0.5);

        Button confirmBtn = new Button("是");
        confirmBtn.setOnAction(e -> {
            stage.close();
            if (handler != null) {
                handler.run();
            }
        } );

        Button cancelBtn = new Button("否");
        cancelBtn.setOnAction(e -> stage.close());

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(5);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("confirm-message-box");
        layout.getChildren().addAll(messageLabel, confirmBtn, cancelBtn);

        Scene scene = new Scene(layout, layout.getPrefWidth(), 50);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Main.CSS);

        stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
    }

    public void show() {
        stage.show();
        ClientUtil.moveStageToOwnerCenter(stage);
    }

}

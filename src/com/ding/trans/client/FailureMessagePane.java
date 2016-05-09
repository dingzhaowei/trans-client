package com.ding.trans.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
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
import javafx.util.Duration;

public class FailureMessagePane {

    Stage stage;

    public FailureMessagePane(Window owner, String error, Runnable handler) {
        Label errorLabel = new Label(error, ClientUtil.createIcon("warn.png", 24));
        errorLabel.setTextOverrun(OverrunStyle.CENTER_ELLIPSIS);
        errorLabel.setGraphicTextGap(5);
        errorLabel.setMaxWidth(Main.getScreenWidth() * 0.5);

        Button closeBtn = new Button();
        closeBtn.setGraphic(ClientUtil.createIcon("close.png", 16));
        closeBtn.setStyle("-fx-background-color: transparent;");
        closeBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                stage.close();
                if (handler != null) {
                    handler.run();
                }
            }
        });

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(2);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("failure-message-box");
        layout.getChildren().addAll(errorLabel, closeBtn);

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

    public void showThenHide(double t) {
        show();
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(Duration.seconds(t), e -> stage.hide());
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

}

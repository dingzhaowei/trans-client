package com.ding.trans.client;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;

public class SuccessMessagePane {

    Stage stage;

    public SuccessMessagePane(Window owner, String msg) {
        Label successLabel = new Label(msg, ClientUtil.createIcon("done.png", 24));
        successLabel.setGraphicTextGap(5);
        successLabel.setMaxWidth(Main.getScreenWidth() * 0.5);

        HBox layout = new HBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(2);
        layout.setPadding(new Insets(10));
        layout.getStyleClass().add("success-message-box");
        layout.getChildren().addAll(successLabel);

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
        Timeline timeline = new Timeline();
        KeyFrame kf = new KeyFrame(Duration.seconds(1.5), e -> stage.hide());
        timeline.getKeyFrames().add(kf);
        timeline.play();
    }

}

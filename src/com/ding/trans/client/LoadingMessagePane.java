package com.ding.trans.client;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class LoadingMessagePane {

    Stage stage;

    public LoadingMessagePane(Window owner) {
        ImageView icon = ClientUtil.createIcon("loading.gif", 100);
        Group group = new Group();
        group.getChildren().add(icon);

        Scene scene = new Scene(group, 100, 9);
        scene.setFill(Color.TRANSPARENT);
        scene.getStylesheets().add(Main.CSS);

        stage = new Stage(StageStyle.TRANSPARENT);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(owner);
        stage.setScene(scene);
    }

    public Stage show() {
        stage.show();
        ClientUtil.moveStageToOwnerCenter(stage);
        return stage;
    }

}

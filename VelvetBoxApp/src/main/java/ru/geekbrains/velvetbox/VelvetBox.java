package ru.geekbrains.velvetbox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
@Slf4j
public class VelvetBox extends Application {
    public static final String CLASS_NAME_VARIABLE = "[" + VelvetBox.class.getName() + "]";

    @Override
    public void start(Stage stage) throws IOException {
        String CURRENT_METHOD_NAME = CLASS_NAME_VARIABLE + "(" + new Object() {}
                .getClass()
                .getEnclosingMethod()
                .getName() + ") ";

        log.info(CURRENT_METHOD_NAME + "Started");
        FXMLLoader fxmlLoader = new FXMLLoader(VelvetBox.class.getResource("velvet_box.fxml"));
        stage.getIcons().add(new Image(VelvetBox.class.getResourceAsStream("icons/velvetbox.png")));
        stage.setTitle("VelvetBox");
        stage.setScene(new Scene(fxmlLoader.load(), 800, 600));
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}